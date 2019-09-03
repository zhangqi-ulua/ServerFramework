package org.zhangqi.manager;

import org.zhangqi.actor.ChannelActor;
import org.zhangqi.actor.GatewayActor;
import org.zhangqi.constants.GlobalConstant;
import org.zhangqi.msg.NetMessage;
import org.zhangqi.msg.RemoteMessage;
import org.zhangqi.proto.Common.RemoteServerTypeEnum;
import org.zhangqi.proto.RemoteServer.NoticeGmServerTextMsgRequest;
import org.zhangqi.proto.RemoteServer.RemoteRpcNameEnum;
import org.zhangqi.service.LoadBalanceService;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Props;
import io.netty.channel.Channel;

public class MessageManager implements IManager {

	private static MessageManager instance = new MessageManager();

	public static MessageManager getInstance() {
		return instance;
	}

	private ActorSystem system;

	private ActorRef gatewayActor;

	private ActorSelection gmRemoteActor;

	// 是否连接上GM服务器
	public boolean isConnectToGm = false;

	private LoadBalanceService loadBalanceService;

	@Override
	public void init() {
		loadBalanceService = SpringManager.getInstance().getBean(LoadBalanceService.class);

		system = ActorSystem.create(GlobalConstant.GATEWAY_SYSTEM_PREFIX + ConfigManager.GATEWAY_ID);

		gmRemoteActor = system.actorSelection(ConfigManager.REMOTE_GM_AKKA_PATH);

		gatewayActor = system.actorOf(Props.create(GatewayActor.class), GlobalConstant.GATEWAY_ACTOR_NAME);
	}

	@Override
	public void shutdown() {
	}

	public boolean isAvailableForClient() {
		return isConnectToGm && loadBalanceService.getMainLogicServerId() > 0;
	}

	public boolean isAvailableForUpdateLoadBalance() {
		return isConnectToGm;
	}

	public ActorRef createChannelActor(Channel channel) {
		return system.actorOf(Props.create(ChannelActor.class, channel));
	}

	public boolean sendNetMsgToMainLogicServer(NetMessage msg, ActorRef sender) {
		int mainLogicServerId = loadBalanceService.getMainLogicServerId();
		if (mainLogicServerId > 0) {
			String akkaPath = loadBalanceService.getOneLogicServerIdToAkkaPath(mainLogicServerId);
			if (akkaPath != null) {
				ActorSelection actorSelection = system.actorSelection(akkaPath);
				actorSelection.tell(msg, sender);
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * 将客户端请求转发到对应logic服务器
	 * 在redis中查找该客户端所在的logic服务器进行发送，如果没有指定或者指定服务器已下线，则选取最空闲的logic服务器让其处理
	 */
	public boolean sendNetMsgToLogicServer(NetMessage msg, ActorRef sender) {
		ActorSelection actorSelection = null;
		int sessionId = msg.getSessionId();
		Integer logicServerId = loadBalanceService.getOneSessionIdToLogicServerId(sessionId);
		if (logicServerId != null) {
			String akkaPath = loadBalanceService.getOneLogicServerIdToAkkaPath(logicServerId);
			if (akkaPath != null) {
				actorSelection = system.actorSelection(akkaPath);
				actorSelection.tell(msg, sender);
				return true;
			}
		}

		Integer leisureLogicServerId = loadBalanceService.getLeisureLogicServerId();
		if (leisureLogicServerId != null) {
			String akkaPath = loadBalanceService.getOneLogicServerIdToAkkaPath(leisureLogicServerId);
			if (akkaPath != null) {
				actorSelection = system.actorSelection(akkaPath);
				actorSelection.tell(msg, sender);
				return true;
			}
		}

		return false;
	}

	public boolean sendNetMsgToBattleServer(NetMessage msg, ActorRef sender) {
		ActorSelection actorSelection = null;
		int userId = msg.getUserId();
		String battleId = loadBalanceService.getBattleUserIdToBattleId(userId);
		if (battleId != null) {
			Integer battleServerId = loadBalanceService.getOneBattleIdToBattleServerId(battleId);
			if (battleServerId != null) {
				String akkaPath = loadBalanceService.getOneBattleServerIdToAkkaPath(battleServerId);
				if (akkaPath != null) {
					actorSelection = system.actorSelection(akkaPath);
					actorSelection.tell(msg, sender);
					return true;
				}
			}
		} else {
			return false;
		}

		Integer leisureBattleServerId = loadBalanceService.getLeisureBattleServerId();
		if (leisureBattleServerId != null) {
			String akkaPath = loadBalanceService.getOneBattleServerIdToAkkaPath(leisureBattleServerId);
			if (akkaPath != null) {
				actorSelection = system.actorSelection(akkaPath);
				actorSelection.tell(msg, sender);
				return true;
			}
		}

		return false;
	}

	public boolean sendRemoteMsgToLogicServer(RemoteMessage msg, int logicServerId) {
		String logicServerAkkaPath = loadBalanceService.getOneLogicServerIdToAkkaPath(logicServerId);
		if (logicServerAkkaPath != null) {
			system.actorSelection(logicServerAkkaPath).tell(msg, gatewayActor);
			return true;
		} else {
			return false;
		}
	}

	public boolean sendRemoteMsgToBattleServer(RemoteMessage msg, int battleServerId) {
		String battleServerAkkaPath = loadBalanceService.getOneBattleServerIdToAkkaPath(battleServerId);
		if (battleServerAkkaPath != null) {
			system.actorSelection(battleServerAkkaPath).tell(msg, gatewayActor);
			return true;
		} else {
			return false;
		}
	}

	public void sendRemoteMsgToGm(RemoteMessage msg) {
		gmRemoteActor.tell(msg, gatewayActor);
	}

	public void noticeGmServerTextMsg(String text) {
		NoticeGmServerTextMsgRequest.Builder builder = NoticeGmServerTextMsgRequest.newBuilder();
		builder.setServerType(RemoteServerTypeEnum.ServerTypeGateway);
		builder.setServerId(ConfigManager.GATEWAY_ID);
		builder.setText(text);
		RemoteMessage remoteMsg = new RemoteMessage(RemoteRpcNameEnum.RemoteRpcNoticeGmServerTextMsg_VALUE, builder);
		sendRemoteMsgToGm(remoteMsg);
	}
}
