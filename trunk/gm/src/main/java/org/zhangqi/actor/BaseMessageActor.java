package org.zhangqi.actor;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zhangqi.action.BaseMessageAction;
import org.zhangqi.annotation.MessageClassMapping;
import org.zhangqi.annotation.MessageMethodMapping;
import org.zhangqi.exception.RpcErrorException;
import org.zhangqi.manager.MessageManager;
import org.zhangqi.manager.SpringManager;
import org.zhangqi.msg.IMessage;
import org.zhangqi.msg.LocalMessage;
import org.zhangqi.msg.NetMessage;
import org.zhangqi.msg.RemoteMessage;
import org.zhangqi.proto.Gm.GmRpcErrorCodeEnum;
import org.zhangqi.proto.Gm.GmRpcNameEnum;
import org.zhangqi.proto.RemoteServer.RemoteRpcErrorCodeEnum;
import org.zhangqi.proto.RemoteServer.RemoteRpcNameEnum;
import org.zhangqi.utils.ClassScanerUtil;

import com.esotericsoftware.reflectasm.MethodAccess;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Cancellable;
import akka.actor.Terminated;
import akka.actor.UntypedAbstractActor;
import scala.concurrent.duration.Duration;

public class BaseMessageActor extends UntypedAbstractActor {

	private static final Logger logger = LoggerFactory.getLogger(BaseMessageActor.class);

	protected final Map<Integer, String> mappingMethodMap = new HashMap<Integer, String>();
	protected Map<Integer, Class<BaseMessageAction>> mappingActionClassMap = null;

	// 如果此Actor有下属的action包，包中有处理rpc的Action类，则必须进行指定。这样才能建立rpcNum与此Actor的对应关系
	private String actionPackageName;

	protected final MethodAccess methodAccess = MethodAccess.get(this.getClass());

	public BaseMessageActor() {
	}

	public BaseMessageActor(String actionPackageName) {
		this.actionPackageName = actionPackageName;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void preStart() {
		logger.info("preStart {}", this.getClass().getName());
		// 如果此Actor有下属的action包，则包中所有Action类所对应处理的rpc都将由这个Actor负责
		if (this.actionPackageName != null) {
			mappingActionClassMap = MessageManager.getInstance().getActionClassByActor(this.getClass());
			if (mappingActionClassMap == null) {
				mappingActionClassMap = new HashMap<Integer, Class<BaseMessageAction>>();
				Set<Class> actionClassNames = ClassScanerUtil.scan(actionPackageName);
				if (actionClassNames != null) {
					for (Class clazz : actionClassNames) {
						MessageClassMapping mapping = (MessageClassMapping) clazz
								.getAnnotation(MessageClassMapping.class);
						if (BaseMessageAction.class.isAssignableFrom(clazz) && mapping != null) {
							int rpcNum = mapping.value();
							if (mappingActionClassMap.containsKey(rpcNum) == true) {
								logger.error(
										"preStart error, multiple action class to handle same rpcNum = {}, action class = {} and {}",
										rpcNum, mappingActionClassMap.get(rpcNum).getClass().getName(),
										clazz.getName());
							}
							mappingActionClassMap.put(rpcNum, clazz);
						}
					}
				}
				MessageManager.getInstance().addActorToHandleAction(this.getClass(), mappingActionClassMap);
			}

			for (Map.Entry<Integer, Class<BaseMessageAction>> entry : mappingActionClassMap.entrySet()) {
				MessageClassMapping mapping = entry.getValue().getAnnotation(MessageClassMapping.class);
				if (mapping.isNet()) {
					int rpcNum = mapping.value();
					logger.info("regist handler for rpcNum = {}, rpcName = {}", rpcNum, GmRpcNameEnum.valueOf(rpcNum));
					MessageManager.getInstance().addRpcNumToHandleActorMap(rpcNum, self());
				}
			}
		}
		// 查找此Actor中是否有处理rpc的函数
		Method[] methods = this.getClass().getMethods();
		for (Method method : methods) {
			MessageMethodMapping mapping = method.getAnnotation(MessageMethodMapping.class);
			if (mapping != null) {
				int[] rpcNum = mapping.value();
				boolean isNet = mapping.isNet();
				for (int oneRpcNum : rpcNum) {
					if (mappingMethodMap.containsKey(oneRpcNum)) {
						logger.error("multiple actor method to handle same rpcNum = {}, method name = {} and {}",
								oneRpcNum, mappingMethodMap.get(oneRpcNum), method.getName());
					}
					if (mappingActionClassMap != null && mappingActionClassMap.containsKey(oneRpcNum)) {
						logger.error(
								"multiple actor class and method to handle same rpcName {}, action class = {}, actor method = {}",
								oneRpcNum, mappingActionClassMap.get(oneRpcNum).getName(), method.getName());
					}
					mappingMethodMap.put(oneRpcNum, method.getName());
					if (isNet) {
						logger.info("regist handler for rpcNum = {}", oneRpcNum);
						MessageManager.getInstance().addRpcNumToHandleActorMap(oneRpcNum, self());
					}
				}
			}
		}
	}

	@Override
	public void onReceive(Object arg0) throws Throwable {
		if (arg0 instanceof Terminated) {
			doTerminated((Terminated) arg0);
		}
		// 收到请求，先尝试寻找对应的Action类处理，找不到则在Actor中找对应处理函数
		else if (arg0 instanceof NetMessage) {
			NetMessage netMessage = (NetMessage) arg0;
			int rpcNum = netMessage.getRpcNum();
			GmRpcNameEnum rpcNameEnum = GmRpcNameEnum.valueOf(rpcNum);
			try {
				Class<BaseMessageAction> clazz = (mappingActionClassMap != null ? mappingActionClassMap.get(rpcNum)
						: null);
				if (clazz != null) {
					BaseMessageAction action = SpringManager.getInstance().getBean(clazz);
					action.handleMessage(netMessage);
				} else if (mappingMethodMap.containsKey(rpcNum)) {
					methodAccess.invoke(this, mappingMethodMap.get(rpcNum), netMessage);
				} else {
					logger.error("onReceive netMessage error, unsupport netMessage rpcNum = {}", rpcNum);
					sendErrorToGmClient(netMessage, GmRpcErrorCodeEnum.GmRpcServerError_VALUE);
				}
			} catch (RpcErrorException e) {
				int errorCode = e.getErrorCode();
				GmRpcErrorCodeEnum errorCodeEnum = GmRpcErrorCodeEnum.valueOf(errorCode);
				logger.error("[resp {} error]: errorCode = {}, means = {}", rpcNameEnum == null ? rpcNum : rpcNameEnum,
						errorCode, errorCodeEnum);

				sendErrorToGmClient(netMessage, errorCode);
			} catch (Exception e) {
				logger.error("[resp {} error]: error = ", rpcNameEnum == null ? rpcNum : rpcNameEnum, e);

				sendErrorToGmClient(netMessage, GmRpcErrorCodeEnum.GmRpcServerError_VALUE);
			}
		} else if (arg0 instanceof LocalMessage) {
			LocalMessage localMessage = (LocalMessage) arg0;
			int rpcNum = localMessage.getRpcNum();
			try {
				Class<BaseMessageAction> clazz = (mappingActionClassMap != null ? mappingActionClassMap.get(rpcNum)
						: null);
				if (clazz != null) {
					BaseMessageAction action = SpringManager.getInstance().getBean(clazz);
					action.handleMessage(localMessage);
				} else if (mappingMethodMap.containsKey(rpcNum)) {
					methodAccess.invoke(this, mappingMethodMap.get(rpcNum), localMessage);
				} else {
					logger.error("onReceive localMessage error, unsupport localMessage rpcNum = {}", rpcNum);
				}
			} catch (Exception e) {
				logger.error("handle localMessage {} error, error = ", rpcNum, e);
			}
		} else if (arg0 instanceof RemoteMessage) {
			RemoteMessage remoteMessage = (RemoteMessage) arg0;
			int rpcNum = remoteMessage.getRpcNum();
			RemoteRpcNameEnum rpcNameEnum = RemoteRpcNameEnum.valueOf(rpcNum);
			try {
				Class<BaseMessageAction> clazz = (mappingActionClassMap != null ? mappingActionClassMap.get(rpcNum)
						: null);
				if (clazz != null) {
					BaseMessageAction action = SpringManager.getInstance().getBean(clazz);
					action.handleMessage(remoteMessage);
				} else if (mappingMethodMap.containsKey(rpcNum)) {
					methodAccess.invoke(this, mappingMethodMap.get(rpcNum), remoteMessage);
				} else {
					logger.error("onReceive remoteMessage error, unsupport remoteMessage rpcNum = {}", rpcNum);
					sendErrorToRemoteSever(remoteMessage, RemoteRpcErrorCodeEnum.RemoteRpcServerError_VALUE);
				}
			} catch (RpcErrorException e) {
				int errorCode = e.getErrorCode();
				RemoteRpcErrorCodeEnum errorCodeEnum = RemoteRpcErrorCodeEnum.valueOf(errorCode);
				logger.error("handle remoteMessage {} error, errorCode = {}, means = {}",
						rpcNameEnum == null ? rpcNum : rpcNameEnum, errorCode, errorCodeEnum);

				sendErrorToRemoteSever(remoteMessage, errorCode);
			} catch (Exception e) {
				logger.error("handle remoteMessage {} error, error = ", rpcNameEnum == null ? rpcNum : rpcNameEnum, e);

				sendErrorToRemoteSever(remoteMessage, RemoteRpcErrorCodeEnum.RemoteRpcServerError_VALUE);
			}
		} else {
			logger.error("onReceive error, unsupport message type = {}", arg0.getClass().getName());
		}
	}

	protected void sendErrorToGmClient(NetMessage netMessage, int errorCode) {
		NetMessage resp = new NetMessage(netMessage.getRpcNum(), errorCode);
		if (netMessage.getSession() != null) {
			netMessage.getSession().write(resp);
		}
	}

	protected void sendErrorToRemoteSever(RemoteMessage remoteMessage, int errorCode) {
		RemoteMessage resp = new RemoteMessage(remoteMessage.getRpcNum(), errorCode);
		sender().tell(resp, self());
	}

	protected Cancellable scheduleOnce(int initialDelaySecond, IMessage msg) {
		ActorSystem system = context().system();
		return system.scheduler().scheduleOnce(Duration.create(initialDelaySecond, TimeUnit.SECONDS), getSelf(), msg,
				system.dispatcher(), ActorRef.noSender());
	}

	protected Cancellable schedule(int initialDelaySecond, int intervalSecond, IMessage msg) {
		ActorSystem system = context().system();
		return system.scheduler().schedule(Duration.create(initialDelaySecond, TimeUnit.SECONDS),
				Duration.create(intervalSecond, TimeUnit.SECONDS), getSelf(), msg, system.dispatcher(),
				ActorRef.noSender());
	}

	protected void doTerminated(Terminated t) throws Exception {
	}
}
