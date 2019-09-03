package org.zhangqi.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zhangqi.action.BaseMessageAction;
import org.zhangqi.actor.BaseMessageActor;
import org.zhangqi.actor.GmActor;
import org.zhangqi.actor.GmUserActor;
import org.zhangqi.actor.ServerManagerActor;
import org.zhangqi.constants.GlobalConstant;
import org.zhangqi.msg.NetMessage;
import org.zhangqi.msg.RemoteMessage;
import org.zhangqi.proto.Common.RemoteServerTypeEnum;
import org.zhangqi.proto.Gm.GmRpcErrorCodeEnum;
import org.zhangqi.proto.RemoteServer.RemoteRpcErrorCodeEnum;
import org.zhangqi.proto.RemoteServer.RemoteRpcNameEnum;
import org.zhangqi.service.LoadBalanceService;
import org.zhangqi.utils.StringUtil;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Address;
import akka.actor.Props;

public class MessageManager implements IManager {

	private static final Logger logger = LoggerFactory.getLogger(MessageManager.class);

	private static MessageManager instance = new MessageManager();

	public static MessageManager getInstance() {
		return instance;
	}

	private ActorSystem system;

	private ActorRef gmActor;

	// key:rpcNum, value:对应处理这个消息的Actor
	private final Map<Integer, ActorRef> rpcNumToHandleActorMap = new HashMap<Integer, ActorRef>();
	// key:actor, value:map(key:rpcNum, value:处理这个消息的Action)
	private final Map<Class<? extends BaseMessageActor>, Map<Integer, Class<BaseMessageAction>>> actorToHandleActionMap = new HashMap<Class<? extends BaseMessageActor>, Map<Integer, Class<BaseMessageAction>>>();

	private int mainLogicServeId;

	private final Map<Integer, ActorRef> logicServerIdToActorMap = new ConcurrentHashMap<Integer, ActorRef>();
	private final Map<Integer, ActorRef> battleServerIdToActorMap = new ConcurrentHashMap<Integer, ActorRef>();
	private final Map<Integer, ActorRef> gatewayIdToActorMap = new ConcurrentHashMap<Integer, ActorRef>();

	private final Map<ActorRef, Integer> logicActorToServerIdMap = new ConcurrentHashMap<ActorRef, Integer>();
	private final Map<ActorRef, Integer> battleActorToServerIdMap = new ConcurrentHashMap<ActorRef, Integer>();
	private final Map<ActorRef, Integer> gatewayActorToIdMap = new ConcurrentHashMap<ActorRef, Integer>();

	private LoadBalanceService loadBalanceService;

	@Override
	public void init() {
		loadBalanceService = SpringManager.getInstance().getBean(LoadBalanceService.class);

		system = ActorSystem.create(GlobalConstant.GM_SYSTEM_NAME);

		gmActor = system.actorOf(Props.create(GmActor.class), GlobalConstant.GM_ACTOR_NAME);

		system.actorOf(Props.create(GmUserActor.class, "org.zhangqi.action.gmUser"), "gmUserActor");
		system.actorOf(Props.create(ServerManagerActor.class, "org.zhangqi.action.serverManager"),
				"serverManagerActor");
	}

	@Override
	public void shutdown() {
	}

	public void addRpcNumToHandleActorMap(int rpcNum, ActorRef actor) {
		if (rpcNumToHandleActorMap.containsKey(rpcNum) == true) {
			logger.error(
					"addRpcNumToHandleActorMap error, multiple actor to handle same rpcNum = {}, actorName = {} and {}",
					rpcNum, rpcNumToHandleActorMap.get(rpcNum).getClass().getName(), actor.getClass().getName());
		}
		rpcNumToHandleActorMap.put(rpcNum, actor);
	}

	public void handleGmRequest(NetMessage message) {
		ActorRef actor = rpcNumToHandleActorMap.get(message.getRpcNum());
		if (actor != null) {
			actor.tell(message, ActorRef.noSender());
		} else {
			logger.error("handleGmRequest error, unsupport rpcNum = {}", message.getRpcNum());
			NetMessage errorNetMsg = new NetMessage(message.getRpcNum(), GmRpcErrorCodeEnum.GmRpcServerError_VALUE);
			message.getSession().write(errorNetMsg);
		}
	}

	public Map<Integer, Class<BaseMessageAction>> getActionClassByActor(Class<? extends BaseMessageActor> clazz) {
		return actorToHandleActionMap.get(clazz);
	}

	public void addActorToHandleAction(Class<? extends BaseMessageActor> clazz,
			Map<Integer, Class<BaseMessageAction>> map) {
		actorToHandleActionMap.put(clazz, map);
	}

	public boolean addLogicServer(int logicServerId, String akkaPath, boolean isMainLogicServer, ActorRef actor) {
		if (logicServerIdToActorMap.containsKey(logicServerId) == true) {
			return false;
		}
		if (isMainLogicServer == true) {
			if (this.mainLogicServeId > 0) {
				return false;
			}
			this.mainLogicServeId = logicServerId;
			loadBalanceService.setMainLogicServerId(logicServerId);
		}
		logicServerIdToActorMap.put(logicServerId, actor);
		logicActorToServerIdMap.put(actor, logicServerId);
		loadBalanceService.setOneLogicServerIdToAkkaPath(logicServerId, akkaPath);
		return true;
	}

	public boolean removeLogicServer(int logicServerId) {
		if (logicServerIdToActorMap.containsKey(logicServerId) == true) {
			ActorRef actor = logicServerIdToActorMap.remove(logicServerId);
			logicActorToServerIdMap.remove(actor);
			loadBalanceService.removeOneLogicServerIdToAkkaPath(logicServerId);
			if (logicServerId == this.mainLogicServeId) {
				this.mainLogicServeId = 0;
				loadBalanceService.setMainLogicServerId(0);
			}
			return true;
		} else {
			return false;
		}
	}

	public boolean removeLogicServer(ActorRef actor) {
		if (logicActorToServerIdMap.containsKey(actor) == true) {
			int logicServerId = logicActorToServerIdMap.remove(actor);
			logicServerIdToActorMap.remove(logicServerId);
			loadBalanceService.removeOneLogicServerIdToAkkaPath(logicServerId);
			if (logicServerId == this.mainLogicServeId) {
				this.mainLogicServeId = 0;
				loadBalanceService.setMainLogicServerId(0);
			}
			return true;
		} else {
			return false;
		}
	}

	public boolean addBattleServer(int battleServerId, String akkaPath, ActorRef actor) {
		battleServerIdToActorMap.put(battleServerId, actor);
		battleActorToServerIdMap.put(actor, battleServerId);
		loadBalanceService.setOneBattleServerIdToAkkaPath(battleServerId, akkaPath);
		return true;
	}

	public boolean removeBattleServer(int battleServerId) {
		if (battleServerIdToActorMap.containsKey(battleServerId) == true) {
			ActorRef actor = battleServerIdToActorMap.remove(battleServerId);
			battleActorToServerIdMap.remove(actor);
			loadBalanceService.removeOneBattleServerIdToAkkaPath(battleServerId);
			return true;
		} else {
			return false;
		}
	}

	public boolean removeBattleServer(ActorRef actor) {
		if (battleActorToServerIdMap.containsKey(actor) == true) {
			int battleServerId = battleActorToServerIdMap.remove(actor);
			battleServerIdToActorMap.remove(battleServerId);
			loadBalanceService.removeOneBattleServerIdToAkkaPath(battleServerId);
			return true;
		} else {
			return false;
		}
	}

	public boolean addGateway(int gatewayId, String akkaPath, String connectPath, ActorRef actor) {
		if (gatewayIdToActorMap.containsKey(gatewayId) == true) {
			return false;
		} else {
			gatewayIdToActorMap.put(gatewayId, actor);
			gatewayActorToIdMap.put(actor, gatewayId);
			loadBalanceService.setOneGatewayIdToAkkaPath(gatewayId, akkaPath);
			loadBalanceService.setOneGatewayIdToConnectPath(gatewayId, connectPath);
			return true;
		}
	}

	public boolean removeGateway(int gatewayId) {
		if (gatewayIdToActorMap.containsKey(gatewayId) == true) {
			ActorRef actor = gatewayIdToActorMap.remove(gatewayId);
			gatewayActorToIdMap.remove(actor);
			loadBalanceService.removeOneGatewayIdToAkkaPath(gatewayId);
			loadBalanceService.removeOneGatewayIdToConnectPath(gatewayId);
			return true;
		} else {
			return false;
		}
	}

	public boolean removeGateway(ActorRef actor) {
		if (gatewayActorToIdMap.containsKey(actor) == true) {
			int gatewayId = gatewayActorToIdMap.remove(actor);
			gatewayIdToActorMap.remove(gatewayId);
			loadBalanceService.removeOneGatewayIdToAkkaPath(gatewayId);
			loadBalanceService.removeOneGatewayIdToConnectPath(gatewayId);
			return true;
		} else {
			return false;
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void sendRemoteMsgToGateway(RemoteMessage remoteMsg, List<Integer> serverIds, Class protobufClass) {
		logger.info(
				"sendRemoteMsgToGateway serverIdList = {}, rpcNum = {}, rpcName = {}, errorCode = {}, means {}, protobuf text = {}",
				StringUtil.getCollectionMemberString(serverIds, ","), remoteMsg.getRpcNum(),
				RemoteRpcNameEnum.valueOf(remoteMsg.getRpcNum()), remoteMsg.getErrorCode(),
				RemoteRpcErrorCodeEnum.valueOf(remoteMsg.getErrorCode()),
				protobufClass != null ? remoteMsg.getProtobufText(protobufClass) : "null");
		for (Integer oneServerId : serverIds) {
			ActorRef actor = gatewayIdToActorMap.get(oneServerId);
			if (actor != null) {
				actor.tell(remoteMsg, gmActor);
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void sendRemoteMsgToAllGateway(RemoteMessage remoteMsg, Class protobufClass) {
		logger.info(
				"sendRemoteMsgToAllGateway serverIdList = {}, rpcNum = {}, rpcName = {}, errorCode = {}, means {}, protobuf text = {}",
				StringUtil.getCollectionMemberString(gatewayIdToActorMap.keySet(), ","), remoteMsg.getRpcNum(),
				RemoteRpcNameEnum.valueOf(remoteMsg.getRpcNum()), remoteMsg.getErrorCode(),
				RemoteRpcErrorCodeEnum.valueOf(remoteMsg.getErrorCode()),
				protobufClass != null ? remoteMsg.getProtobufText(protobufClass) : "null");
		for (ActorRef oneGatewayActor : gatewayActorToIdMap.keySet()) {
			oneGatewayActor.tell(remoteMsg, gmActor);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void sendRemoteMsgToAllLogicServer(RemoteMessage remoteMsg, Class protobufClass) {
		logger.info(
				"sendRemoteMsgToAllLogicServer serverIdList = {}, rpcNum = {}, rpcName = {}, errorCode = {}, means {}, protobuf text = {}",
				StringUtil.getCollectionMemberString(logicServerIdToActorMap.keySet(), ","), remoteMsg.getRpcNum(),
				RemoteRpcNameEnum.valueOf(remoteMsg.getRpcNum()), remoteMsg.getErrorCode(),
				RemoteRpcErrorCodeEnum.valueOf(remoteMsg.getErrorCode()),
				protobufClass != null ? remoteMsg.getProtobufText(protobufClass) : "null");
		for (ActorRef oneLogicServerActor : logicActorToServerIdMap.keySet()) {
			oneLogicServerActor.tell(remoteMsg, gmActor);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void sendRemoteMsgToLogicServer(RemoteMessage remoteMsg, List<Integer> serverIds, Class protobufClass) {
		logger.info(
				"sendRemoteMsgToLogicServer serverIdList = {}, rpcNum = {}, rpcName = {}, errorCode = {}, means {}, protobuf text = {}",
				StringUtil.getCollectionMemberString(serverIds, ","), remoteMsg.getRpcNum(),
				RemoteRpcNameEnum.valueOf(remoteMsg.getRpcNum()), remoteMsg.getErrorCode(),
				RemoteRpcErrorCodeEnum.valueOf(remoteMsg.getErrorCode()),
				protobufClass != null ? remoteMsg.getProtobufText(protobufClass) : "null");
		for (Integer oneServerId : serverIds) {
			ActorRef actor = logicServerIdToActorMap.get(oneServerId);
			if (actor != null) {
				actor.tell(remoteMsg, gmActor);
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void sendRemoteMsgToAllBattleServer(RemoteMessage remoteMsg, Class protobufClass) {
		logger.info(
				"sendRemoteMsgToAllBattleServer serverIdList = {}, rpcNum = {}, rpcName = {}, errorCode = {}, means {}, protobuf text = {}",
				StringUtil.getCollectionMemberString(battleServerIdToActorMap.keySet(), ","), remoteMsg.getRpcNum(),
				RemoteRpcNameEnum.valueOf(remoteMsg.getRpcNum()), remoteMsg.getErrorCode(),
				RemoteRpcErrorCodeEnum.valueOf(remoteMsg.getErrorCode()),
				protobufClass != null ? remoteMsg.getProtobufText(protobufClass) : "null");
		for (ActorRef oneBattleServerActor : battleActorToServerIdMap.keySet()) {
			oneBattleServerActor.tell(remoteMsg, gmActor);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void sendRemoteMsgToBattleServer(RemoteMessage remoteMsg, List<Integer> serverIds, Class protobufClass) {
		logger.info(
				"sendRemoteMsgToBattleServer serverIdList = {}, rpcNum = {}, rpcName = {}, errorCode = {}, means {}, protobuf text = {}",
				StringUtil.getCollectionMemberString(serverIds, ","), remoteMsg.getRpcNum(),
				RemoteRpcNameEnum.valueOf(remoteMsg.getRpcNum()), remoteMsg.getErrorCode(),
				RemoteRpcErrorCodeEnum.valueOf(remoteMsg.getErrorCode()),
				protobufClass != null ? remoteMsg.getProtobufText(protobufClass) : "null");
		for (Integer oneServerId : serverIds) {
			ActorRef actor = battleServerIdToActorMap.get(oneServerId);
			if (actor != null) {
				actor.tell(remoteMsg, gmActor);
			}
		}
	}

	public String getServerIpWithPort(RemoteServerTypeEnum serverType, int serverId) {
		ActorRef actor = null;
		if (serverType == RemoteServerTypeEnum.ServerTypeLogic) {
			actor = logicServerIdToActorMap.get(serverId);
		} else if (serverType == RemoteServerTypeEnum.ServerTypeBattle) {
			actor = battleServerIdToActorMap.get(serverId);
		} else if (serverType == RemoteServerTypeEnum.ServerTypeGateway) {
			actor = gatewayIdToActorMap.get(serverId);
		} else {
			logger.error("getServerIp error, unsupport serverType = {}", serverType);
			return null;
		}

		if (actor != null) {
			Address address = actor.path().address();
			return address.host().get() + ":" + address.port().get();
		} else {
			return null;
		}
	}
}
