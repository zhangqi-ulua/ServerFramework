package org.zhangqi.manager;

import java.util.List;
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

	// key:battleId, value:这个对战房间对应的BattleActor
	private final Map<String, ActorRef> battleIdToBattleActor = new ConcurrentHashMap<String, ActorRef>();
	// key:sessionId,
	// value:这个客户端对应gateway中ResponseActor（玩家发的消息，gateway转发到battleServer，sender为ResponseActor）
	private final Map<Integer, ActorRef> sessionIdToGatewayResponseActor = new ConcurrentHashMap<Integer, ActorRef>();

	private LoadBalanceService loadBalanceService;

	public void addBattleActor(String battleId, ActorRef battleActor, List<Integer> userIds) {
		battleIdToBattleActor.put(battleId, battleActor);
		for (int userId : userIds) {
			loadBalanceService.setBattleUserIdToBattleId(userId, battleId);
		}
		loadBalanceService.setOneBattleIdToBattleServerId(battleId, ConfigManager.BATTLE_SERVER_ID);
		if (MessageManager.getInstance().isAvailableForUpdateLoadBalance() == true) {
			loadBalanceService.setOneBattleServerLoadBalance(ConfigManager.BATTLE_SERVER_ID,
					battleIdToBattleActor.size());
		}
	}

	public void removeBattleActor(String battleId, List<Integer> userIds) {
		ActorRef battleActor = battleIdToBattleActor.remove(battleId);
		battleActor.tell(PoisonPill.getInstance(), ActorRef.noSender());
		for (int userId : userIds) {
			loadBalanceService.removeBattleUserIdToBattleId(userId);
		}
		loadBalanceService.removeOneBattleIdToBattleServerId(battleId);
		if (MessageManager.getInstance().isAvailableForUpdateLoadBalance() == true) {
			loadBalanceService.setOneBattleServerLoadBalance(ConfigManager.BATTLE_SERVER_ID,
					battleIdToBattleActor.size());
		}
	}

	public ActorRef getBattleActor(String battleId) {
		return battleIdToBattleActor.get(battleId);
	}

	public int getBattleCount() {
		return battleIdToBattleActor.size();
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

	@Override
	public void init() {
		loadBalanceService = SpringManager.getInstance().getBean(LoadBalanceService.class);
	}

	@Override
	public void shutdown() {
		// 服务器关闭时，清除服务器连接地址、负载信息
		loadBalanceService.removeOneBattleServerLoadBalance(ConfigManager.BATTLE_SERVER_ID);
		loadBalanceService.removeOneBattleServerIdToAkkaPath(ConfigManager.BATTLE_SERVER_ID);
	}
}
