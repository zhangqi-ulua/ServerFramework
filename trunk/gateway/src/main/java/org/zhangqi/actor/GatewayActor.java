package org.zhangqi.actor;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zhangqi.manager.ConfigManager;
import org.zhangqi.manager.CoreManager;
import org.zhangqi.manager.MessageManager;
import org.zhangqi.manager.OnlineClientManager;
import org.zhangqi.manager.SpringManager;
import org.zhangqi.msg.IMessage;
import org.zhangqi.msg.LocalMessage;
import org.zhangqi.msg.NetMessage;
import org.zhangqi.msg.RemoteMessage;
import org.zhangqi.proto.Common.ForceOfflinePush;
import org.zhangqi.proto.Common.GmCmdTypeEnum;
import org.zhangqi.proto.Common.RemoteServerTypeEnum;
import org.zhangqi.proto.LocalServer.LocalRpcNameEnum;
import org.zhangqi.proto.RemoteServer.LogicServerNoticeGatewayForceOfflineClientPush;
import org.zhangqi.proto.RemoteServer.NoticeExecuteGmCmdRequest;
import org.zhangqi.proto.RemoteServer.RegistServerRequest;
import org.zhangqi.proto.RemoteServer.RemoteRpcErrorCodeEnum;
import org.zhangqi.proto.RemoteServer.RemoteRpcNameEnum;
import org.zhangqi.proto.RemoteServer.RemoteServerInfo;
import org.zhangqi.proto.Rpc.RpcNameEnum;
import org.zhangqi.service.LoadBalanceService;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Cancellable;
import akka.actor.Terminated;
import akka.actor.UntypedAbstractActor;
import io.netty.channel.Channel;
import scala.concurrent.duration.Duration;

public class GatewayActor extends UntypedAbstractActor {

	private static final Logger logger = LoggerFactory.getLogger(GatewayActor.class);

	private Cancellable gatewayReconnectGmSchedule = null;

	private LoadBalanceService loadBalanceService = SpringManager.getInstance().getBean(LoadBalanceService.class);

	@Override
	public void preStart() throws Exception {
		// 开启定时任务，每5秒尝试与GM服务器进行通讯，直到连接成功
		if (gatewayReconnectGmSchedule == null) {
			gatewayReconnectGmSchedule = schedule(0, 5,
					new LocalMessage(LocalRpcNameEnum.LocalRpcRegistToGmServer_VALUE));
		}
	}

	@Override
	public void onReceive(Object arg0) throws Throwable {
		if (arg0 instanceof Terminated) {
			doTerminated((Terminated) arg0);
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
					if (gatewayReconnectGmSchedule != null) {
						gatewayReconnectGmSchedule.cancel();
						gatewayReconnectGmSchedule = null;
					}
					// 连接GM成功后，写入负载情况
					loadBalanceService.setOneGatewayLoadBalance(ConfigManager.GATEWAY_ID,
							OnlineClientManager.getInstance().getOnlineSessionCount());
				} else {
					logger.error("[regist to GM server error], errorCodeEnum = {}", errorCodeEnum);
					System.exit(-1);
				}

				break;
			}
			case RemoteRpcNameEnum.RemoteRpcLogicServerNoticeGatewayForceOfflineClient_VALUE: {
				LogicServerNoticeGatewayForceOfflineClientPush forceOfflineClientPush = remoteMessage
						.getLite(LogicServerNoticeGatewayForceOfflineClientPush.class);
				int sessionId = forceOfflineClientPush.getSessionId();
				Channel channel = OnlineClientManager.getInstance().getChannel(sessionId);
				if (channel == null) {
					logger.error(
							"handle LogicServerNoticeGatewayForceOfflineClient, but can't find channel, sessionId = {}",
							sessionId);
				} else {
					ForceOfflinePush.Builder pushBuilder = ForceOfflinePush.newBuilder();
					pushBuilder.setForceOfflineReason(forceOfflineClientPush.getForceOfflineReason());
					NetMessage netMsg = new NetMessage(RpcNameEnum.ForceOfflinePush_VALUE, pushBuilder);
					write(netMsg, channel);
					channel.close();
				}
			}
			case RemoteRpcNameEnum.RemoteRpcNoticeExecuteGmCmd_VALUE: {
				NoticeExecuteGmCmdRequest req = remoteMessage.getLite(NoticeExecuteGmCmdRequest.class);
				GmCmdTypeEnum cmdType = req.getCmdType();
				if (cmdType == GmCmdTypeEnum.GmCmdCloseServer) {
					MessageManager.getInstance().isConnectToGm = false;
					MessageManager.getInstance().noticeGmServerTextMsg("start execute gm cmd GmCmdCloseServer");
					CoreManager.getInstance().shutdown();
					System.exit(0);
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
				RegistServerRequest.Builder registGatewayBuilder = RegistServerRequest.newBuilder();
				RemoteServerInfo.Builder serverInfoBuilder = RemoteServerInfo.newBuilder();
				serverInfoBuilder.setServerType(RemoteServerTypeEnum.ServerTypeGateway);
				serverInfoBuilder.setServerId(ConfigManager.GATEWAY_ID);
				serverInfoBuilder.setAkkaPath(ConfigManager.GATEWAY_AKKA_PATH);
				serverInfoBuilder.setGatewayConnectPath(ConfigManager.GATEWAY_CONNECT_PATH);
				registGatewayBuilder.setServerInfo(serverInfoBuilder);
				MessageManager.getInstance().sendRemoteMsgToGm(
						new RemoteMessage(RemoteRpcNameEnum.RemoteRpcRegistServer_VALUE, registGatewayBuilder));

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
		if (gatewayReconnectGmSchedule == null) {
			gatewayReconnectGmSchedule = schedule(0, 5,
					new LocalMessage(LocalRpcNameEnum.LocalRpcRegistToGmServer_VALUE));
		}
	}

	private void write(IMessage message, Channel channel) {
		if (channel != null && channel.isActive() && channel.isWritable()) {
			channel.writeAndFlush(message.toBinaryWebSocketFrame());
		}
	}
}
