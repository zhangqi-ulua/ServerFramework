package org.zhangqi.dao;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Repository;
import org.zhangqi.constants.RedisKeyHelper;

@Repository(value = "loadBalanceDAO")
public class LoadBalanceDAO {

	@SuppressWarnings("rawtypes")
	@Autowired
	RedisTemplate jedisTemplate;

	@Resource(name = "stringTemplate")
	// 客户端自增的sessionId（value类型，自增的sessionId）
	private ValueOperations<String, String> sessionIdAutoIncreaseOps;
	@Resource(name = "integerTemplate")
	// 在线客户端所连的gatewayId（map类型，key:sessionId, value:gatewayId）
	private HashOperations<String, String, Integer> sessionIdToGatewayIdOps;
	@Resource(name = "integerTemplate")
	// 在线客户端对应UserActor所在的logic服务器（map类型，key:sessionId, value:logicServerId）
	private HashOperations<String, String, Integer> sessionIdToLogicServerIdOps;
	@Resource(name = "integerTemplate")
	// 已登录成功玩家userId与sessionId的对应关系（map类型，key:userId, value:sessionId）
	private HashOperations<String, String, Integer> userIdToSessionIdOps;
	@Resource(name = "integerTemplate")
	// 已登录成功玩家sessionId与userId的对应关系（map类型，key:sessionId, value:userId）
	private HashOperations<String, String, Integer> sessionIdToUserIdOps;
	@Resource(name = "stringTemplate")
	// 对战中的玩家userId与battleId的对应关系（map类型，key:userId, value:battleId）
	private HashOperations<String, String, String> battleUserIdToBattleIdOps;
	@Resource(name = "integerTemplate")
	// 进行中的battleId与处理这场战斗的battleServerId的对应关系（map类型，key:battleId,
	// value:battleServerId）
	private HashOperations<String, String, Integer> battleIdToBattleServerIdOps;
	@Resource(name = "stringTemplate")
	// logic服务器的负载（zset类型，score:服务器负载, value:logicServerId）
	private ZSetOperations<String, String> logicServerLoadBalanceOps;
	@Resource(name = "stringTemplate")
	// gateway服务器的负载（zset类型，score:服务器负载, value:gatewayId）
	private ZSetOperations<String, String> gatewayLoadBalanceOps;
	@Resource(name = "stringTemplate")
	// battle服务器的负载（zset类型，score:服务器负载, value:battleServerId）
	private ZSetOperations<String, String> battleServerLoadBalanceOps;
	@Resource(name = "stringTemplate")
	// 已注册到GM服务器的logic服务器id对应的akka地址（map类型，key:logicServerId, value:akka地址）
	private HashOperations<String, String, String> logicServerIdToAkkaPathOps;
	@Resource(name = "stringTemplate")
	// 已注册到GM服务器的gateway服务器id对应的akka地址（map类型，key:gatewayId, value:akka地址）
	private HashOperations<String, String, String> gatewayIdToAkkaPathOps;
	@Resource(name = "stringTemplate")
	// 已注册到GM服务器的battle服务器id对应的akka地址（map类型，key:battleServerId, value:akka地址）
	private HashOperations<String, String, String> battleServerIdToAkkaPathOps;
	@Resource(name = "stringTemplate")
	// 已注册到GM服务器的gateway服务器id对应的供客户端连接的地址（map类型，key:gatewayId, value:供客户端连接的地址）
	private HashOperations<String, String, String> gatewayIdToConnectPathOps;
	@Resource(name = "integerTemplate")
	// 已注册到GM服务器的主logic服务器id（value类型，mainLogicServerId）
	private ValueOperations<String, Integer> mainLogicServerIdOps;

	public int addAndGetNextAvailableSessionId() {
		return sessionIdAutoIncreaseOps.increment(RedisKeyHelper.getSessionIdAutoIncreaseRedisKey(), 1).intValue();
	}

	public void setOneSessionIdToGatewayId(int userId, int gatewayId) {
		sessionIdToGatewayIdOps.put(RedisKeyHelper.getSessionIdToGatewayIdRedisKey(), String.valueOf(userId),
				gatewayId);
	}

	public Integer getOneSessionIdToGatewayId(int userId) {
		return sessionIdToGatewayIdOps.get(RedisKeyHelper.getSessionIdToGatewayIdRedisKey(), String.valueOf(userId));
	}

	public void removeOneSessionIdToGatewayId(int userId) {
		sessionIdToGatewayIdOps.delete(RedisKeyHelper.getSessionIdToGatewayIdRedisKey(), String.valueOf(userId));
	}

	public void setOneSessionIdToLogicServerId(int userId, int logicServerId) {
		sessionIdToLogicServerIdOps.put(RedisKeyHelper.getSessionIdToLogicServerIdRedisKey(), String.valueOf(userId),
				logicServerId);
	}

	public Integer getOneSessionIdToLogicServerId(int userId) {
		return sessionIdToLogicServerIdOps.get(RedisKeyHelper.getSessionIdToLogicServerIdRedisKey(),
				String.valueOf(userId));
	}

	public void removeOneSessionIdToLogicServerId(int userId) {
		sessionIdToLogicServerIdOps.delete(RedisKeyHelper.getSessionIdToLogicServerIdRedisKey(),
				String.valueOf(userId));
	}

	public Integer getOneUserIdToSessionId(int userId) {
		return userIdToSessionIdOps.get(RedisKeyHelper.getUserIdToSessionIdRedisKey(), String.valueOf(userId));
	}

	public Map<Integer, Integer> getAllUserIdToSessionId() {
		Map<Integer, Integer> result = new HashMap<Integer, Integer>();
		Map<String, Integer> map = userIdToSessionIdOps.entries(RedisKeyHelper.getUserIdToSessionIdRedisKey());
		if (map != null) {
			for (Map.Entry<String, Integer> entry : map.entrySet()) {
				result.put(Integer.parseInt(entry.getKey()), entry.getValue());
			}
		}
		return result;
	}

	public void setOneUserIdToSessionId(int userId, int sessionId) {
		userIdToSessionIdOps.put(RedisKeyHelper.getUserIdToSessionIdRedisKey(), String.valueOf(userId), sessionId);
	}

	public void removeOneUserIdToSessionId(int userId) {
		userIdToSessionIdOps.delete(RedisKeyHelper.getUserIdToSessionIdRedisKey(), String.valueOf(userId));
	}

	public Integer getOneSessionIdToUserId(int sessionId) {
		return sessionIdToUserIdOps.get(RedisKeyHelper.getSessionIdToUserIdRedisKey(), String.valueOf(sessionId));
	}

	public void setOneSessionIdToUserId(int sessionId, int userId) {
		sessionIdToUserIdOps.put(RedisKeyHelper.getSessionIdToUserIdRedisKey(), String.valueOf(sessionId), userId);
	}

	public void removeOneSessionIdToUserId(int sessionId) {
		sessionIdToUserIdOps.delete(RedisKeyHelper.getSessionIdToUserIdRedisKey(), String.valueOf(sessionId));
	}

	public void setBattleUserIdToBattleId(int userId, String battleId) {
		battleUserIdToBattleIdOps.put(RedisKeyHelper.getBattleUserIdToBattleIdRedisKey(), String.valueOf(userId),
				battleId);
	}

	public String getBattleUserIdToBattleId(int userId) {
		return battleUserIdToBattleIdOps.get(RedisKeyHelper.getBattleUserIdToBattleIdRedisKey(),
				String.valueOf(userId));
	}

	public void removeBattleUserIdToBattleId(int userId) {
		battleUserIdToBattleIdOps.delete(RedisKeyHelper.getBattleUserIdToBattleIdRedisKey(), String.valueOf(userId));
	}

	public Integer getOneBattleIdToBattleServerId(String battleId) {
		return battleIdToBattleServerIdOps.get(RedisKeyHelper.getBattleIdToBattleServerIdRedisKey(), battleId);
	}

	public void setOneBattleIdToBattleServerId(String battleId, int battleServerId) {
		battleIdToBattleServerIdOps.put(RedisKeyHelper.getBattleIdToBattleServerIdRedisKey(), battleId, battleServerId);
	}

	public void removeOneBattleIdToBattleServerId(String battleId) {
		battleIdToBattleServerIdOps.delete(RedisKeyHelper.getBattleIdToBattleServerIdRedisKey(), battleId);
	}

	public void setOneLogicServerLoadBalance(int logicServerId, int count) {
		logicServerLoadBalanceOps.add(RedisKeyHelper.getLogicServerLoadBalanceRedisKey(), String.valueOf(logicServerId),
				count);
	}

	public void changeOneLogicServerLoadBalance(int logicServerId, int changeCount) {
		logicServerLoadBalanceOps.incrementScore(RedisKeyHelper.getLogicServerLoadBalanceRedisKey(),
				String.valueOf(logicServerId), changeCount);
	}

	public void removeOneLogicServerLoadBalance(int logicServerId) {
		logicServerLoadBalanceOps.remove(RedisKeyHelper.getLogicServerLoadBalanceRedisKey(),
				String.valueOf(logicServerId));
	}

	public Map<Integer, Integer> getAllLogicServerLoadBalance() {
		Map<Integer, Integer> result = new HashMap<Integer, Integer>();
		Set<TypedTuple<String>> set = logicServerLoadBalanceOps
				.rangeWithScores(RedisKeyHelper.getLogicServerLoadBalanceRedisKey(), 0, -1);
		if (set != null) {
			for (TypedTuple<String> tuple : set) {
				result.put(Integer.parseInt(tuple.getValue()), tuple.getScore().intValue());
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public void cleanLogicServerLoadBalance() {
		jedisTemplate.delete(RedisKeyHelper.getLogicServerLoadBalanceRedisKey());
	}

	public Integer getLeisureLogicServerId() {
		Set<String> serverSet = logicServerLoadBalanceOps.range(RedisKeyHelper.getLogicServerLoadBalanceRedisKey(), 0,
				0);
		if (serverSet != null && serverSet.isEmpty() == false) {
			return Integer.parseInt(serverSet.iterator().next());
		} else {
			return null;
		}
	}

	public void setOneGatewayLoadBalance(int gatewayId, int count) {
		gatewayLoadBalanceOps.add(RedisKeyHelper.getGatewayLoadBalanceRedisKey(), String.valueOf(gatewayId), count);
	}

	public void changeOneGatewayLoadBalance(int gatewayId, int changeCount) {
		gatewayLoadBalanceOps.incrementScore(RedisKeyHelper.getGatewayLoadBalanceRedisKey(), String.valueOf(gatewayId),
				changeCount);
	}

	public void removeOneGatewayLoadBalance(int gatewayId) {
		gatewayLoadBalanceOps.remove(RedisKeyHelper.getGatewayLoadBalanceRedisKey(), String.valueOf(gatewayId));
	}

	public Map<Integer, Integer> getAllGatewayLoadBalance() {
		Map<Integer, Integer> result = new HashMap<Integer, Integer>();
		Set<TypedTuple<String>> set = gatewayLoadBalanceOps
				.rangeWithScores(RedisKeyHelper.getGatewayLoadBalanceRedisKey(), 0, -1);
		if (set != null) {
			for (TypedTuple<String> tuple : set) {
				result.put(Integer.parseInt(tuple.getValue()), tuple.getScore().intValue());
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public void cleanGatewayLoadBalance() {
		jedisTemplate.delete(RedisKeyHelper.getGatewayLoadBalanceRedisKey());
	}

	public Integer getLeisureGatewayId() {
		Set<String> serverSet = gatewayLoadBalanceOps.range(RedisKeyHelper.getGatewayLoadBalanceRedisKey(), 0, 0);
		if (serverSet != null && serverSet.isEmpty() == false) {
			return Integer.parseInt(serverSet.iterator().next());
		} else {
			return null;
		}
	}

	public void setOneBattleServerLoadBalance(int battleServerId, int count) {
		battleServerLoadBalanceOps.add(RedisKeyHelper.getBattleServerLoadBalanceRedisKey(),
				String.valueOf(battleServerId), count);
	}

	public void changeOneBattleServerLoadBalance(int battleServerId, int changeCount) {
		battleServerLoadBalanceOps.incrementScore(RedisKeyHelper.getBattleServerLoadBalanceRedisKey(),
				String.valueOf(battleServerId), changeCount);
	}

	public void removeOneBattleServerLoadBalance(int battleServerId) {
		battleServerLoadBalanceOps.remove(RedisKeyHelper.getBattleServerLoadBalanceRedisKey(),
				String.valueOf(battleServerId));
	}

	public Map<Integer, Integer> getAllBattleServerLoadBalance() {
		Map<Integer, Integer> result = new HashMap<Integer, Integer>();
		Set<TypedTuple<String>> set = battleServerLoadBalanceOps
				.rangeWithScores(RedisKeyHelper.getBattleServerLoadBalanceRedisKey(), 0, -1);
		if (set != null) {
			for (TypedTuple<String> tuple : set) {
				result.put(Integer.parseInt(tuple.getValue()), tuple.getScore().intValue());
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public void cleanBattleServerLoadBalance() {
		jedisTemplate.delete(RedisKeyHelper.getBattleServerLoadBalanceRedisKey());
	}

	public Integer getLeisureBattleServerId() {
		Set<String> serverSet = battleServerLoadBalanceOps.range(RedisKeyHelper.getBattleServerLoadBalanceRedisKey(), 0,
				0);
		if (serverSet != null && serverSet.isEmpty() == false) {
			return Integer.parseInt(serverSet.iterator().next());
		} else {
			return null;
		}
	}

	public void setOneLogicServerIdToAkkaPath(int logicServerId, String akkaPath) {
		logicServerIdToAkkaPathOps.put(RedisKeyHelper.getLogicServerIdToAkkaPathRedisKey(),
				String.valueOf(logicServerId), akkaPath);
	}

	public String getOneLogicServerIdToAkkaPath(int logicServerId) {
		return logicServerIdToAkkaPathOps.get(RedisKeyHelper.getLogicServerIdToAkkaPathRedisKey(),
				String.valueOf(logicServerId));
	}

	public Map<Integer, String> getAllLogicServerIdToAkkaPath() {
		Map<Integer, String> result = new HashMap<Integer, String>();
		Map<String, String> map = logicServerIdToAkkaPathOps
				.entries(RedisKeyHelper.getLogicServerIdToAkkaPathRedisKey());
		if (map != null) {
			for (Map.Entry<String, String> entry : map.entrySet()) {
				result.put(Integer.parseInt(entry.getKey()), entry.getValue());
			}
		}
		return result;
	}

	public void removeOneLogicServerIdToAkkaPath(int logicServerId) {
		logicServerIdToAkkaPathOps.delete(RedisKeyHelper.getLogicServerIdToAkkaPathRedisKey(),
				String.valueOf(logicServerId));
	}

	@SuppressWarnings("unchecked")
	public void cleanLogicServerIdToAkkaPath() {
		jedisTemplate.delete(RedisKeyHelper.getLogicServerIdToAkkaPathRedisKey());
	}

	public void setOneGatewayIdToAkkaPath(int gatewayId, String akkaPath) {
		gatewayIdToAkkaPathOps.put(RedisKeyHelper.getGatewayIdToAkkaPathRedisKey(), String.valueOf(gatewayId),
				akkaPath);
	}

	public String getOneGatewayIdToAkkaPath(int gatewayId) {
		return gatewayIdToAkkaPathOps.get(RedisKeyHelper.getGatewayIdToAkkaPathRedisKey(), String.valueOf(gatewayId));
	}

	public Map<Integer, String> getAllGatewayIdToAkkaPath() {
		Map<Integer, String> result = new HashMap<Integer, String>();
		Map<String, String> map = gatewayIdToAkkaPathOps.entries(RedisKeyHelper.getGatewayIdToAkkaPathRedisKey());
		if (map != null) {
			for (Map.Entry<String, String> entry : map.entrySet()) {
				result.put(Integer.parseInt(entry.getKey()), entry.getValue());
			}
		}
		return result;
	}

	public void removeOneGatewayIdToAkkaPath(int gatewayId) {
		gatewayIdToAkkaPathOps.delete(RedisKeyHelper.getGatewayIdToAkkaPathRedisKey(), String.valueOf(gatewayId));
	}

	@SuppressWarnings("unchecked")
	public void cleanGatewayIdToAkkaPath() {
		jedisTemplate.delete(RedisKeyHelper.getGatewayIdToAkkaPathRedisKey());
	}

	public void setOneBattleServerIdToAkkaPath(int battleServerId, String akkaPath) {
		battleServerIdToAkkaPathOps.put(RedisKeyHelper.getBattleServerIdToAkkaPathRedisKey(),
				String.valueOf(battleServerId), akkaPath);
	}

	public String getOneBattleServerIdToAkkaPath(int battleServerId) {
		return battleServerIdToAkkaPathOps.get(RedisKeyHelper.getBattleServerIdToAkkaPathRedisKey(),
				String.valueOf(battleServerId));
	}

	public Map<Integer, String> getAllBattleServerIdToAkkaPath() {
		Map<Integer, String> result = new HashMap<Integer, String>();
		Map<String, String> map = battleServerIdToAkkaPathOps
				.entries(RedisKeyHelper.getBattleServerIdToAkkaPathRedisKey());
		if (map != null) {
			for (Map.Entry<String, String> entry : map.entrySet()) {
				result.put(Integer.parseInt(entry.getKey()), entry.getValue());
			}
		}
		return result;
	}

	public void removeOneBattleServerIdToAkkaPath(int battleServerId) {
		battleServerIdToAkkaPathOps.delete(RedisKeyHelper.getBattleServerIdToAkkaPathRedisKey(),
				String.valueOf(battleServerId));
	}

	@SuppressWarnings("unchecked")
	public void cleanBattleServerIdToAkkaPath() {
		jedisTemplate.delete(RedisKeyHelper.getBattleServerIdToAkkaPathRedisKey());
	}

	public void setOneGatewayIdToConnectPath(int gatewayId, String connectPath) {
		gatewayIdToConnectPathOps.put(RedisKeyHelper.getGatewayIdToConnectPathRedisKey(), String.valueOf(gatewayId),
				connectPath);
	}

	public String getOneGatewayIdToConnectPath(int gatewayId) {
		return gatewayIdToConnectPathOps.get(RedisKeyHelper.getGatewayIdToConnectPathRedisKey(),
				String.valueOf(gatewayId));
	}

	public void removeOneGatewayIdToConnectPath(int gatewayId) {
		gatewayIdToConnectPathOps.delete(RedisKeyHelper.getGatewayIdToConnectPathRedisKey(), String.valueOf(gatewayId));
	}

	@SuppressWarnings("unchecked")
	public void cleanGatewayIdToConnectPath() {
		jedisTemplate.delete(RedisKeyHelper.getGatewayIdToConnectPathRedisKey());
	}

	public void setMainLogicServerId(int mainLogicServerId) {
		mainLogicServerIdOps.set(RedisKeyHelper.getMainLogicServerIdRedisKey(), mainLogicServerId);
	}

	public int getMainLogicServerId() {
		Integer result = mainLogicServerIdOps.get(RedisKeyHelper.getMainLogicServerIdRedisKey());
		return result == null ? 0 : result;
	}
}
