package org.zhangqi.actor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zhangqi.annotation.MessageMethodMapping;
import org.zhangqi.constants.GlobalConstant;
import org.zhangqi.manager.MessageManager;
import org.zhangqi.manager.OnlineClientManager;
import org.zhangqi.manager.SpringManager;
import org.zhangqi.msg.NetMessage;
import org.zhangqi.msg.RemoteMessage;
import org.zhangqi.proto.Common.RemoteServerTypeEnum;
import org.zhangqi.proto.Gm.GmRpcNameEnum;
import org.zhangqi.proto.Gm.GmTextMsgPush;
import org.zhangqi.proto.RemoteServer.NoticeGmServerTextMsgRequest;
import org.zhangqi.proto.RemoteServer.RegistServerRequest;
import org.zhangqi.proto.RemoteServer.RegistServerResponse;
import org.zhangqi.proto.RemoteServer.RemoteRpcErrorCodeEnum;
import org.zhangqi.proto.RemoteServer.RemoteRpcNameEnum;
import org.zhangqi.proto.RemoteServer.RemoteServerInfo;
import org.zhangqi.service.LoadBalanceService;

import akka.actor.ActorRef;
import akka.actor.Terminated;

public class GmActor extends BaseMessageActor {

	private static final Logger logger = LoggerFactory.getLogger(GmActor.class);

	public GmActor() {
		super();
	}

	public GmActor(String actionPackageName) {
		super(actionPackageName);
	}

	@Override
	public void preStart() {
		super.preStart();

		// GM服务器开启后，清空所有功能服务器的负载、地址等信息，并等待它们重新来注册
		LoadBalanceService loadBalanceService = SpringManager.getInstance().getBean(LoadBalanceService.class);
		loadBalanceService.cleanLogicServerLoadBalance();
		loadBalanceService.cleanLogicServerIdToAkkaPath();
		loadBalanceService.setMainLogicServerId(0);
		loadBalanceService.cleanGatewayLoadBalance();
		loadBalanceService.cleanGatewayIdToAkkaPath();
		loadBalanceService.cleanGatewayIdToConnectPath();
		loadBalanceService.cleanBattleServerLoadBalance();
		loadBalanceService.cleanBattleServerIdToAkkaPath();
	}

	@MessageMethodMapping(RemoteRpcNameEnum.RemoteRpcRegistServer_VALUE)
	public void registServer(RemoteMessage remoteMsg) {
		RegistServerRequest req = remoteMsg.getLite(RegistServerRequest.class);
		RemoteServerInfo serverInfo = req.getServerInfo();
		RemoteServerTypeEnum serverType = serverInfo.getServerType();
		int serverId = serverInfo.getServerId();
		String akkaPath = serverInfo.getAkkaPath();
		boolean isRegistSuccess = false;
		if (serverType == RemoteServerTypeEnum.ServerTypeLogic) {
			isRegistSuccess = MessageManager.getInstance().addLogicServer(serverId, akkaPath,
					serverInfo.getIsMainLogicServer(), sender());
		} else if (serverType == RemoteServerTypeEnum.ServerTypeBattle) {
			isRegistSuccess = MessageManager.getInstance().addBattleServer(serverId, akkaPath, sender());
		} else if (serverType == RemoteServerTypeEnum.ServerTypeGateway) {
			isRegistSuccess = MessageManager.getInstance().addGateway(serverId, akkaPath,
					serverInfo.getGatewayConnectPath(), sender());
		} else {
			logger.error("registServer error, unsupport server type = {}", serverType);
			sender().tell(new RemoteMessage(RemoteRpcNameEnum.RemoteRpcRegistServer_VALUE,
					RemoteRpcErrorCodeEnum.RemoteRpcServerError_VALUE), self());
			return;
		}
		if (isRegistSuccess == true) {
			getContext().watch(sender());
			if (serverType == RemoteServerTypeEnum.ServerTypeLogic) {
				logger.info("[regist new logic server], id = {}, akkaPath = {}, isMainLogicServer = {}", serverId,
						akkaPath, serverInfo.getIsMainLogicServer());
			} else if (serverType == RemoteServerTypeEnum.ServerTypeBattle) {
				logger.info("[regist new battle server], id = {}, akkaPath = {}", serverId, akkaPath);
			} else if (serverType == RemoteServerTypeEnum.ServerTypeGateway) {
				logger.info("[regist new gateway], id = {}, akkaPath = {}, connectPath = {}", serverId, akkaPath,
						serverInfo.getGatewayConnectPath());
			} else {
				logger.error("after registServer success error, unsupport server type = {}", serverType);
				sender().tell(new RemoteMessage(RemoteRpcNameEnum.RemoteRpcRegistServer_VALUE,
						RemoteRpcErrorCodeEnum.RemoteRpcServerError_VALUE), self());
				return;
			}

			RegistServerResponse.Builder respBuilder = RegistServerResponse.newBuilder();
			sender().tell(new RemoteMessage(RemoteRpcNameEnum.RemoteRpcRegistServer_VALUE, respBuilder), self());
		} else {
			logger.error("registServer error, has registed, serverType = {}, akkaPath = {}", serverType, akkaPath);
			sender().tell(new RemoteMessage(RemoteRpcNameEnum.RemoteRpcRegistServer_VALUE,
					RemoteRpcErrorCodeEnum.RemoteRpcRegistServerErrorHasRegisted_VALUE), self());
		}
	}

	@MessageMethodMapping(RemoteRpcNameEnum.RemoteRpcNoticeGmServerTextMsg_VALUE)
	public void noticeGmServerTextMsg(RemoteMessage remoteMsg) {
		NoticeGmServerTextMsgRequest req = remoteMsg.getLite(NoticeGmServerTextMsgRequest.class);
		StringBuilder sb = new StringBuilder();
		sb.append("[").append(req.getServerType()).append("  ").append(req.getServerId()).append("]")
				.append(req.getText());
		GmTextMsgPush.Builder pushBuilder = GmTextMsgPush.newBuilder();
		pushBuilder.setText(sb.toString());
		NetMessage netMsg = new NetMessage(GmRpcNameEnum.GmRpcTextMsgPush_VALUE, pushBuilder);
		OnlineClientManager.getInstance().pushToAllGmSession(netMsg, GmTextMsgPush.class);
	}

	@Override
	protected void doTerminated(Terminated t) throws Exception {
		ActorRef actor = t.getActor();
		String actorName = actor.path().name();
		if (GlobalConstant.GATEWAY_ACTOR_NAME.equals(actorName)) {
			if (MessageManager.getInstance().removeGateway(actor) == false) {
				logger.error("doTerminated gateway is offline, but remove from MessageManager fail, akkaPath = {}",
						actor.path());
			} else {
				logger.info("[remove offline gateway], akkaPath = {}", actor.path().address());
			}
		} else if (GlobalConstant.LOGIC_SERVER_ACTOR_NAME.equals(actorName)) {
			if (MessageManager.getInstance().removeLogicServer(actor) == false) {
				logger.error("doTerminated logic server is offline, but remove from MessageManager fail, akkaPath = {}",
						actor.path());
			} else {
				logger.info("[remove offline logic server], akkaPath = {}", actor.path().address());
			}
		} else if (GlobalConstant.BATTLE_SERVER_ACTOR_NAME.equals(actorName)) {
			if (MessageManager.getInstance().removeBattleServer(actor) == false) {
				logger.error(
						"doTerminated battle server is offline, but remove from MessageManager fail, akkaPath = {}",
						actor.path());
			} else {
				logger.info("[remove offline battle server], akkaPath = {}", actor.path().address());
			}
		} else {
			logger.error("doTerminated error, unsupport server type, akkaPath = {}", actor.path());
		}
	}
}
