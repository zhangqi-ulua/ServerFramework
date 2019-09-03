package org.zhangqi.manager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.zhangqi.service.LoadBalanceService;

import akka.actor.ActorRef;
import akka.actor.PoisonPill;

public class OnlineClientManager implements IManager {

	private static OnlineClientManager instance = new OnlineClientManager();

	public static OnlineClientManager getInstance() {
		return instance;
	}

	// key:sessionId, value:这个客户端对应在本logicServer的UserActor
	private final Map<Integer, ActorRef> sessionIdToUserActorMap = new ConcurrentHashMap<Integer, ActorRef>();
	// key:sessionId,
	// value:这个客户端对应gateway中ResponseActor（玩家发的消息，gateway转发到logicServer，sender为ResponseActor）
	private final Map<Integer, ActorRef> sessionIdToGatewayResponseActor = new ConcurrentHashMap<Integer, ActorRef>();

	private LoadBalanceService loadBalanceService;

	public void addUserActor(int sessionId, ActorRef userActor) {
		sessionIdToUserActorMap.put(sessionId, userActor);
		loadBalanceService.setOneSessionIdToLogicServerId(sessionId, ConfigManager.LOGIC_SERVER_ID);
		if (MessageManager.getInstance().isAvailableForUpdateLoadBalance() == true) {
			loadBalanceService.setOneLogicServerLoadBalance(ConfigManager.LOGIC_SERVER_ID,
					sessionIdToUserActorMap.size());
		}
	}

	public void removeUserActor(int sessionId) {
		ActorRef userActor = sessionIdToUserActorMap.remove(sessionId);
		userActor.tell(PoisonPill.getInstance(), ActorRef.noSender());
		loadBalanceService.removeOneSessionIdToLogicServerId(sessionId);
		if (MessageManager.getInstance().isAvailableForUpdateLoadBalance() == true) {
			loadBalanceService.setOneLogicServerLoadBalance(ConfigManager.LOGIC_SERVER_ID,
					sessionIdToUserActorMap.size());
		}
	}

	public void addSessionIdToGatewayResponseActor(int sessionId, ActorRef gatewayResponseActor) {
		sessionIdToGatewayResponseActor.put(sessionId, gatewayResponseActor);
	}

	public void removeSessionIdToGatewayResponseActor(int sessionId) {
		sessionIdToGatewayResponseActor.remove(sessionId);
	}

	public ActorRef getGatewayResponseActor(int sessionId) {
		return sessionIdToGatewayResponseActor.get(sessionId);
	}

	public int getUserActorCount() {
		return sessionIdToUserActorMap.size();
	}

	public ActorRef getUserActor(int sessionId) {
		return sessionIdToUserActorMap.get(sessionId);
	}

	@Override
	public void init() {
		loadBalanceService = SpringManager.getInstance().getBean(LoadBalanceService.class);
	}

	@Override
	public void shutdown() {
		// 服务器关闭时，清除服务器连接地址、负载信息
		loadBalanceService.removeOneLogicServerLoadBalance(ConfigManager.LOGIC_SERVER_ID);
		loadBalanceService.removeOneLogicServerIdToAkkaPath(ConfigManager.LOGIC_SERVER_ID);
		if (ConfigManager.IS_MAIN_LOGIC_SERVER == true
				&& loadBalanceService.getMainLogicServerId() == ConfigManager.LOGIC_SERVER_ID) {
			loadBalanceService.setMainLogicServerId(0);
		}
		// 清除连接本logicServer的玩家的sessionId与logicServerId的对应信息
		for (int sessionId : sessionIdToUserActorMap.keySet()) {
			loadBalanceService.removeOneSessionIdToLogicServerId(sessionId);
		}
	}
}
