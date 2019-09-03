package org.zhangqi.constants;

import com.google.common.base.Joiner;

public class RedisKeyHelper {

	private static final String SPRITE_STRING = ":";
	private static final Joiner joiner = Joiner.on(SPRITE_STRING);

	public static String join(Object... strs) {
		return joiner.join(strs);
	}

	public static String getSessionIdAutoIncreaseRedisKey() {
		return RedisKeyConstant.SESSION_ID_AUTO_INCREASE;
	}

	public static String getSessionIdToGatewayIdRedisKey() {
		return RedisKeyConstant.SESSION_ID_TO_GATEWAY_ID;
	}

	public static String getSessionIdToLogicServerIdRedisKey() {
		return RedisKeyConstant.SESSION_ID_TO_LOGIC_SERVER_ID;
	}

	public static String getUserIdToSessionIdRedisKey() {
		return RedisKeyConstant.USER_ID_TO_SESSION_ID;
	}

	public static String getSessionIdToUserIdRedisKey() {
		return RedisKeyConstant.SESSION_ID_TO_USER_ID;
	}

	public static String getBattleUserIdToBattleIdRedisKey() {
		return RedisKeyConstant.BATTLE_USER_ID_TO_BATTLE_ID;
	}

	public static String getBattleIdToBattleServerIdRedisKey() {
		return RedisKeyConstant.BATTLE_ID_TO_BATTLE_SERVER_ID;
	}

	public static String getLogicServerLoadBalanceRedisKey() {
		return RedisKeyConstant.LOGIC_SERVER_LOAD_BALANCE;
	}

	public static String getGatewayLoadBalanceRedisKey() {
		return RedisKeyConstant.GATEWAY_LOAD_BALANCE;
	}

	public static String getBattleServerLoadBalanceRedisKey() {
		return RedisKeyConstant.BATTLE_SERVER_LOAD_BALANCE;
	}

	public static String getUserIdAutoIncreaseRedisKey() {
		return RedisKeyConstant.USER_ID_AUTO_INCREASE;
	}

	public static String getUserDataRedisKey() {
		return RedisKeyConstant.USER_DATA;
	}

	public static String getUsernameToIdRedisKey() {
		return RedisKeyConstant.USERNAME_TO_ID;
	}

	public static String getUserStateRedisKey() {
		return RedisKeyConstant.USER_STATE;
	}

	public static String getLogicServerIdToAkkaPathRedisKey() {
		return RedisKeyConstant.LOGIC_SERVER_ID_TO_AKKA_PATH;
	}

	public static String getGatewayIdToAkkaPathRedisKey() {
		return RedisKeyConstant.GATEWAY_ID_TO_AKKA_PATH;
	}

	public static String getBattleServerIdToAkkaPathRedisKey() {
		return RedisKeyConstant.BATTLE_SERVER_ID_TO_AKKA_PATH;
	}

	public static String getGatewayIdToConnectPathRedisKey() {
		return RedisKeyConstant.GATEWAY_ID_TO_CONNECT_PATH;
	}

	public static String getMainLogicServerIdRedisKey() {
		return RedisKeyConstant.MAIN_LOGIC_SERVER_ID;
	}
}
