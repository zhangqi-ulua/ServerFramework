package org.zhangqi.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zhangqi.action.BaseMessageAction;
import org.zhangqi.actor.BaseMessageActor;
import org.zhangqi.actor.LogicServerActor;
import org.zhangqi.actor.LoginActor;
import org.zhangqi.actor.MatchActor;
import org.zhangqi.actor.RegistActor;
import org.zhangqi.actor.UserActor;
import org.zhangqi.actor.UserManagerActor;
import org.zhangqi.constants.GlobalConstant;
import org.zhangqi.msg.NetMessage;
import org.zhangqi.msg.RemoteMessage;
import org.zhangqi.proto.Common.BattleTypeEnum;
import org.zhangqi.proto.Common.RemoteServerTypeEnum;
import org.zhangqi.proto.RemoteServer.BattleRoomInfo;
import org.zhangqi.proto.RemoteServer.NoticeBattleServerCreateNewBattleRequest;
import org.zhangqi.proto.RemoteServer.NoticeGmServerTextMsgRequest;
import org.zhangqi.proto.RemoteServer.RemoteRpcErrorCodeEnum;
import org.zhangqi.proto.RemoteServer.RemoteRpcNameEnum;
import org.zhangqi.proto.Rpc.RpcErrorCodeEnum;
import org.zhangqi.proto.Rpc.RpcNameEnum;
import org.zhangqi.service.LoadBalanceService;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class MessageManager implements IManager {
	private static final Logger logger = LoggerFactory.getLogger(MessageManager.class);

	private static MessageManager instance = new MessageManager();

	public static MessageManager getInstance() {
		return instance;
	}

	private ActorSystem system;

	private ActorRef logicServerActor;
	private ActorRef matchActor;

	private ActorSelection gmRemoteActor;

	// 是否连接上GM服务器
	public boolean isConnectToGm = false;

	// key:rpcNum, value:对应处理这个消息的Actor
	private final Map<Integer, ActorRef> rpcNumToHandleActorMap = new HashMap<Integer, ActorRef>();
	// key:actor, value:map(key:rpcNum, value:处理这个消息的Action)
	private final Map<Class<? extends BaseMessageActor>, Map<Integer, Class<BaseMessageAction>>> actorToHandleActionMap = new HashMap<Class<? extends BaseMessageActor>, Map<Integer, Class<BaseMessageAction>>>();

	private LoadBalanceService loadBalanceService;

	@Override
	public void init() {
		loadBalanceService = SpringManager.getInstance().getBean(LoadBalanceService.class);

		system = ActorSystem.create(GlobalConstant.LOGIC_SERVER_SYSTEM_PREFIX + ConfigManager.LOGIC_SERVER_ID);

		gmRemoteActor = system.actorSelection(ConfigManager.REMOTE_GM_AKKA_PATH);

		system.actorOf(Props.create(UserManagerActor.class), "userManagerActor");
		system.actorOf(Props.create(LoginActor.class), "loginActor");

		if (ConfigManager.IS_MAIN_LOGIC_SERVER == true) {
			system.actorOf(Props.create(RegistActor.class, "org.zhangqi.action.regist"), "registActor");
			matchActor = system.actorOf(Props.create(MatchActor.class, "org.zhangqi.action.match"), "matchActor");
		}

		logicServerActor = system.actorOf(Props.create(LogicServerActor.class), GlobalConstant.LOGIC_SERVER_ACTOR_NAME);
	}

	@Override
	public void shutdown() {
	}

	public boolean isAvailableForUpdateLoadBalance() {
		return isConnectToGm;
	}

	public ActorRef createUserActor() {
		return system.actorOf(Props.create(UserActor.class, "org.zhangqi.action.userManager"));
	}

	public void sendRemoteMsgToGm(RemoteMessage msg) {
		gmRemoteActor.tell(msg, logicServerActor);
	}

	public void noticeGmServerTextMsg(String text) {
		NoticeGmServerTextMsgRequest.Builder builder = NoticeGmServerTextMsgRequest.newBuilder();
		builder.setServerType(RemoteServerTypeEnum.ServerTypeLogic);
		builder.setServerId(ConfigManager.LOGIC_SERVER_ID);
		builder.setText(text);
		RemoteMessage remoteMsg = new RemoteMessage(RemoteRpcNameEnum.RemoteRpcNoticeGmServerTextMsg_VALUE, builder);
		sendRemoteMsgToGm(remoteMsg);
	}

	public void addRpcNumToHandleActorMap(int rpcNum, ActorRef actor) {
		if (rpcNumToHandleActorMap.containsKey(rpcNum) == true) {
			logger.error(
					"addRpcNumToHandleActorMap error, multiple actor to handle same rpcNum = {}, actorName = {} and {}",
					rpcNum, rpcNumToHandleActorMap.get(rpcNum).getClass().getName(), actor.getClass().getName());
		}
		rpcNumToHandleActorMap.put(rpcNum, actor);
	}

	public Map<Integer, Class<BaseMessageAction>> getActionClassByActor(Class<? extends BaseMessageActor> clazz) {
		return actorToHandleActionMap.get(clazz);
	}

	public void addActorToHandleAction(Class<? extends BaseMessageActor> clazz,
			Map<Integer, Class<BaseMessageAction>> map) {
		actorToHandleActionMap.put(clazz, map);
	}

	public void handleRequest(NetMessage message, ActorRef sender) {
		ActorRef actor = rpcNumToHandleActorMap.get(message.getRpcNum());
		if (actor != null) {
			actor.tell(message, sender);
		} else {
			logger.error("handleRequest error, unsupport rpcNum = {}", message.getRpcNum());
			NetMessage errorNetMsg = new NetMessage(message.getRpcNum(), RpcErrorCodeEnum.ServerError_VALUE);
			sender.tell(errorNetMsg, ActorRef.noSender());
		}
	}

	public void handleRemoteMsg(RemoteMessage message, ActorRef sender) {
		ActorRef actor = rpcNumToHandleActorMap.get(message.getRpcNum());
		if (actor != null) {
			actor.tell(message, sender);
		} else {
			logger.error("handleRemoteMsg error, unsupport rpcNum = {}", message.getRpcNum());
			RemoteMessage errorRemoteMsg = new RemoteMessage(message.getRpcNum(),
					RemoteRpcErrorCodeEnum.RemoteRpcServerError_VALUE);
			sender.tell(errorRemoteMsg, ActorRef.noSender());
		}
	}

	public boolean sendRemoteMsgToGataway(RemoteMessage msg, int gatewayId) {
		String gatewayAkkaPath = loadBalanceService.getOneGatewayIdToAkkaPath(gatewayId);
		if (gatewayAkkaPath != null) {
			system.actorSelection(gatewayAkkaPath).tell(msg, logicServerActor);
			return true;
		} else {
			return false;
		}
	}

	public boolean sendNetMsgToOneSession(int sessionId, NetMessage netMsg) {
		ActorRef gatewayResponseActor = OnlineClientManager.getInstance().getGatewayResponseActor(sessionId);
		if (gatewayResponseActor != null) {
			gatewayResponseActor.tell(netMsg, ActorRef.noSender());
			return true;
		} else {
			return false;
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean sendNetMsgToOneUser(int userId, NetMessage netMsg, Class protobufClass) {
		logger.info(
				"sendNetMsgToOneUser userId = {}, rpcNum = {}, rpcName = {}, errorCode = {}, means {}, protobuf text = {}",
				userId, netMsg.getRpcNum(), RpcNameEnum.valueOf(netMsg.getRpcNum()), netMsg.getErrorCode(),
				RpcErrorCodeEnum.valueOf(netMsg.getErrorCode()),
				protobufClass != null ? netMsg.getProtobufText(protobufClass) : "null");
		Integer sessionId = loadBalanceService.getOneUserIdToSessionId(userId);
		if (sessionId != null) {
			return sendNetMsgToOneSession(sessionId, netMsg);
		} else {
			return false;
		}
	}

	public boolean noticeBattleServerCreateNewBattle(BattleTypeEnum battleType, String battleId,
			List<Integer> userIds) {
		Integer leisureBattleServerId = loadBalanceService.getLeisureBattleServerId();
		if (leisureBattleServerId != null) {
			String battleServerAkkaPath = loadBalanceService.getOneBattleServerIdToAkkaPath(leisureBattleServerId);
			if (battleServerAkkaPath != null) {
				BattleRoomInfo.Builder battleRoomInfoBuilder = BattleRoomInfo.newBuilder();
				battleRoomInfoBuilder.setBattleType(battleType);
				battleRoomInfoBuilder.setBattleId(battleId);
				battleRoomInfoBuilder.addAllUserIds(userIds);

				NoticeBattleServerCreateNewBattleRequest.Builder builder = NoticeBattleServerCreateNewBattleRequest
						.newBuilder();
				builder.setBattleRoomInfo(battleRoomInfoBuilder);

				RemoteMessage remoteMsg = new RemoteMessage(
						RemoteRpcNameEnum.RemoteRpcNoticeBattleServerCreateNewBattle_VALUE, builder);

				system.actorSelection(battleServerAkkaPath).tell(remoteMsg, matchActor);
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
}
