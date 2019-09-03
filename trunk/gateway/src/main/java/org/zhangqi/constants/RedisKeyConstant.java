package org.zhangqi.constants;

public class RedisKeyConstant {

	// 客户端自增的sessionId（value类型，自增的sessionId）
	public static final String SESSION_ID_AUTO_INCREASE = "sessionIdAutoIncrease";
	// 在线客户端所连的gatewayId（map类型，key:sessionId, value:gatewayId）
	public static final String SESSION_ID_TO_GATEWAY_ID = "sessionIdToGatewayId";
	// 在线客户端对应UserActor所在的logic服务器（map类型，key:sessionId, value:logicServerId）
	public static final String SESSION_ID_TO_LOGIC_SERVER_ID = "sessionIdToLogicServerId";
	// 已登录成功玩家userId与sessionId的对应关系（map类型，key:userId, value:sessionId）
	public static final String USER_ID_TO_SESSION_ID = "userIdToSessionId";
	// 已登录成功玩家sessionId与userId的对应关系（map类型，key:sessionId, value:userId）
	public static final String SESSION_ID_TO_USER_ID = "sessionIdToUserId";
	// 对战中的玩家userId与battleId的对应关系（map类型，key:userId, value:battleId）
	public static final String BATTLE_USER_ID_TO_BATTLE_ID = "battleUserIdToBattleId";
	// 进行中的battleId与处理这场战斗的battleServerId的对应关系（map类型，key:battleId,
	// value:battleServerId）
	public static final String BATTLE_ID_TO_BATTLE_SERVER_ID = "battleIdToBattleServerId";
	// logic服务器的负载（zset类型，score:服务器负载, value:logicServerId）
	public static final String LOGIC_SERVER_LOAD_BALANCE = "logicServerLoadBalance";
	// gateway服务器的负载（zset类型，score:服务器负载, value:gatewayId）
	public static final String GATEWAY_LOAD_BALANCE = "gatewayLoadBalance";
	// battle服务器的负载（zset类型，score:服务器负载, value:battleServerId）
	public static final String BATTLE_SERVER_LOAD_BALANCE = "battleServerLoadBalance";
	// 已注册到GM服务器的logic服务器id对应的akka地址（map类型，key:logicServerId, value:akka地址）
	public static final String LOGIC_SERVER_ID_TO_AKKA_PATH = "logicServerIdToAkkaPath";
	// 已注册到GM服务器的gateway服务器id对应的akka地址（map类型，key:gatewayId, value:akka地址）
	public static final String GATEWAY_ID_TO_AKKA_PATH = "gatewayIdToAkkaPath";
	// 已注册到GM服务器的battle服务器id对应的akka地址（map类型，key:battleServerId, value:akka地址）
	public static final String BATTLE_SERVER_ID_TO_AKKA_PATH = "battleServerIdToAkkaPath";
	// 已注册到GM服务器的gateway服务器id对应的供客户端连接的地址（map类型，key:gatewayId, value:供客户端连接的地址）
	public static final String GATEWAY_ID_TO_CONNECT_PATH = "gatewayIdToConnectPath";
	// 已注册到GM服务器的主logic服务器id（value类型，mainLogicServerId）
	public static final String MAIN_LOGIC_SERVER_ID = "mainLogicServerId";

	// 玩家自增的userId（value类型，自增的userId）
	public static final String USER_ID_AUTO_INCREASE = "userIdAutoIncrease";
	// 玩家信息（map类型，key:userId, value:protobuf定义的UserData）
	public static final String USER_DATA = "userData";
	// 玩家名与userId的对应关系（map类型，key:username, value:userId）
	public static final String USERNAME_TO_ID = "usernameToId";
	// 玩家状态（map类型，key:userId, value:protobuf定义的UserState）
	public static final String USER_STATE = "userState";
}
