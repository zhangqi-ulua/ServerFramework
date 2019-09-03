package org.zhangqi.actor;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zhangqi.manager.ConfigManager;
import org.zhangqi.manager.CoreManager;
import org.zhangqi.manager.MessageManager;
import org.zhangqi.manager.OnlineClientManager;
import org.zhangqi.manager.SpringManager;
import org.zhangqi.manager.TableConfigManager;
import org.zhangqi.msg.IMessage;
import org.zhangqi.msg.LocalMessage;
import org.zhangqi.msg.NetMessage;
import org.zhangqi.msg.RemoteMessage;
import org.zhangqi.proto.Common.GmCmdTypeEnum;
import org.zhangqi.proto.Common.RemoteServerTypeEnum;
import org.zhangqi.proto.Common.UserActionStateEnum;
import org.zhangqi.proto.Common.UserState;
import org.zhangqi.proto.LocalServer.LocalRpcNameEnum;
import org.zhangqi.proto.RemoteServer.GatewayNoticeClientOfflinePush;
import org.zhangqi.proto.RemoteServer.NoticeExecuteGmCmdRequest;
import org.zhangqi.proto.RemoteServer.RegistServerRequest;
import org.zhangqi.proto.RemoteServer.RemoteRpcErrorCodeEnum;
import org.zhangqi.proto.RemoteServer.RemoteRpcNameEnum;
import org.zhangqi.proto.RemoteServer.RemoteServerInfo;
import org.zhangqi.service.LoadBalanceService;
import org.zhangqi.service.MatchService;
import org.zhangqi.service.UserService;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Cancellable;
import akka.actor.Terminated;
import akka.actor.UntypedAbstractActor;
import scala.concurrent.duration.Duration;

public class LogicServerActor extends UntypedAbstractActor {

	private static final Logger logger = LoggerFactory.getLogger(LogicServerActor.class);

	private Cancellable logicServerReconnectGmSchedule = null;

	private LoadBalanceService loadBalanceService = SpringManager.getInstance().getBean(LoadBalanceService.class);
	private UserService userService = SpringManager.getInstance().getBean(UserService.class);
	private MatchService matchService = SpringManager.getInstance().getBean(MatchService.class);

	@Override
	public void preStart() throws Exception {
		// 开启定时任务，每5秒尝试与GM服务器进行通讯，直到连接成功
		if (logicServerReconnectGmSchedule == null) {
			logicServerReconnectGmSchedule = schedule(0, 5,
					new LocalMessage(LocalRpcNameEnum.LocalRpcRegistToGmServer_VALUE));
		}
	}

	@Override
	public void onReceive(Object arg0) throws Throwable {
		if (arg0 instanceof Terminated) {
			doTerminated((Terminated) arg0);
		} else if (arg0 instanceof NetMessage) {
			NetMessage netMessage = (NetMessage) arg0;
			MessageManager.getInstance().handleRequest(netMessage, sender());
		} else if (arg0 instanceof RemoteMessage) {
			RemoteMessage remoteMessage = (RemoteMessage) arg0;
			int errorCode = remoteMessage.getErrorCode();
			RemoteRpcErrorCodeEnum errorCodeEnum = RemoteRpcErrorCodeEnum.valueOf(errorCode);
			switch (remoteMessage.getRpcNum()) {
			case RemoteRpcNameEnum.RemoteRpcRegistServer_VALUE: {
				if (errorCode == RemoteRpcErrorCodeEnum.RemoteRpcOk_VALUE) {
					context().watch(sender());
					MessageManager.getInstance().isConnectToGm = true;
					logger.info("[regist to GM server success]");
					if (logicServerReconnectGmSchedule != null) {
						logicServerReconnectGmSchedule.cancel();
						logicServerReconnectGmSchedule = null;
					}
					// 连接GM成功后，写入负载情况
					loadBalanceService.setOneLogicServerLoadBalance(ConfigManager.LOGIC_SERVER_ID,
							OnlineClientManager.getInstance().getUserActorCount());
				} else {
					logger.error("[regist to GM server error], errorCodeEnum = {}", errorCodeEnum);
					System.exit(-1);
				}

				break;
			}
			case RemoteRpcNameEnum.RemoteRpcGatewayNoticeClientOfflinePush_VALUE: {
				GatewayNoticeClientOfflinePush push = remoteMessage.getLite(GatewayNoticeClientOfflinePush.class);
				if (push.hasUserId() == true && push.getIsUserOffline() == true) {
					int offlineUserId = push.getUserId();
					// 如果玩家正在匹配，强制结束匹配
					if (ConfigManager.IS_MAIN_LOGIC_SERVER == true) {
						UserState userState = userService.getUserState(offlineUserId);
						if (userState.getActionState() == UserActionStateEnum.Matching) {
							matchService.removeMatchPlayer(offlineUserId, userState.getBattleType());
						}
					}
				}
				int offlineSessionId = push.getSessionId();
				OnlineClientManager.getInstance().removeUserActor(offlineSessionId);
				OnlineClientManager.getInstance().removeSessionIdToGatewayResponseActor(offlineSessionId);
				break;
			}
			case RemoteRpcNameEnum.RemoteRpcNoticeExecuteGmCmd_VALUE: {
				NoticeExecuteGmCmdRequest req = remoteMessage.getLite(NoticeExecuteGmCmdRequest.class);
				GmCmdTypeEnum cmdType = req.getCmdType();
				if (cmdType == GmCmdTypeEnum.GmCmdCloseServer) {
					MessageManager.getInstance().isConnectToGm = false;
					MessageManager.getInstance().noticeGmServerTextMsg("start execute gm cmd GmCmdCloseServer");
					CoreManager.getInstance().shutdown();
					System.exit(0);
				} else if (cmdType == GmCmdTypeEnum.GmCmdReloadTableConfig) {
					MessageManager.getInstance().noticeGmServerTextMsg("start execute gm cmd GmCmdReloadTableConfig");
					TableConfigManager.getInstance().reloadTableConfig();
				} else {
					logger.error("onReceive RemoteRpcNoticeExecuteGmCmd, unsupport cmdType = {}", cmdType);
					MessageManager.getInstance()
							.noticeGmServerTextMsg("execute gm cmd error, unsupport cmdType = " + cmdType);
				}
				break;
			}
			default: {
				logger.error("onReceive remoteMessage error, unsupport remoteMessage rpcNum = {}",
						remoteMessage.getRpcNum());
				break;
			}
			}
		} else if (arg0 instanceof LocalMessage) {
			LocalMessage localMessage = (LocalMessage) arg0;
			switch (localMessage.getRpcNum()) {
			case LocalRpcNameEnum.LocalRpcRegistToGmServer_VALUE: {
				logger.info("try to connect GM server");
				RegistServerRequest.Builder registLogicServerBuilder = RegistServerRequest.newBuilder();
				RemoteServerInfo.Builder serverInfoBuilder = RemoteServerInfo.newBuilder();
				serverInfoBuilder.setServerType(RemoteServerTypeEnum.ServerTypeLogic);
				serverInfoBuilder.setServerId(ConfigManager.LOGIC_SERVER_ID);
				serverInfoBuilder.setAkkaPath(ConfigManager.LOGIC_SERVER_AKKA_PATH);
				serverInfoBuilder.setIsMainLogicServer(ConfigManager.IS_MAIN_LOGIC_SERVER);
				registLogicServerBuilder.setServerInfo(serverInfoBuilder);
				MessageManager.getInstance().sendRemoteMsgToGm(
						new RemoteMessage(RemoteRpcNameEnum.RemoteRpcRegistServer_VALUE, registLogicServerBuilder));

				break;
			}
			default: {
				logger.error("onReceive localMessage error, unsupport localMessage rpcNum = {}",
						localMessage.getRpcNum());
				break;
			}
			}
		} else {
			logger.error("onReceive error, unsupport msg type = {}", arg0.getClass().getName());
		}
	}

	private Cancellable schedule(int initialDelaySecond, int intervalSecond, IMessage msg) {
		ActorSystem system = context().system();
		return system.scheduler().schedule(Duration.create(initialDelaySecond, TimeUnit.SECONDS),
				Duration.create(intervalSecond, TimeUnit.SECONDS), getSelf(), msg, system.dispatcher(),
				ActorRef.noSender());
	}

	private void doTerminated(Terminated t) throws Exception {
		// 监测到GM断线后，开启定时任务，尝试重新连接
		MessageManager.getInstance().isConnectToGm = false;
		logger.warn("[can't connect GM server], start reconnect task, intervalSecond = 5s");
		if (logicServerReconnectGmSchedule == null) {
			logicServerReconnectGmSchedule = schedule(0, 5,
					new LocalMessage(LocalRpcNameEnum.LocalRpcRegistToGmServer_VALUE));
		}
	}
}
