package org.zhangqi.constants;

public class BattleRedisKeyConstant {

	// 当前进行中的对战battleId集合（set类型，value:进行中的对战battleId）
	public static final String BATTLE_PLAYING_BATTLE_IDS = "battlePlayingBattleIds";
	// 一场战斗中按先后手顺利排列的userId（list类型，value:先后手顺利排列的userId）
	public static final String BATTLE_USER_IDS = "battleUserIds";
	// 一场战斗中当前回合的信息（map类型，key:battleId, value:protobuf定义的CurrentTurnInfo）
	public static final String BATTLE_CURRENT_TURN_INFO = "battleCurrentTurnInfo";
	// 一场战斗中当前棋盘中棋子信息（list类型，value:按下标位置排列的对应格子的棋子情况）
	public static final String BATTLE_CELL_INFO = "battleCellInfo";
	// 一场战斗中发生的所有事件（list类型，value:protobuf定义的EventMsg）
	public static final String BATTLE_EVENT_LIST = "battleEventList";
	// 一场战斗中上一个已发生事件的eventNum（map类型，key:battleId, value:该战场中上一个已发生事件的eventNum）
	public static final String BATTLE_LAST_EVENT_NUM = "battleLastEventNum";
	// 一场战斗开始的时间戳（map类型，key:battleId, value:开始的时间戳）
	public static final String BATTLE_START_TIMESTAMP = "battleStartTimestamp";
	// 一场战斗尚未准备开始游戏（为了照顾有些客户端可能载入资源过慢，在一定时限内，所有玩家都准备就绪后再正式开始对战）的玩家userId（set类型，value:尚未准备开始游戏的玩家userId）
	public static final String BATTLE_NOT_READY_USER_IDS = "battleNotReadyUserIds";
	// 某日的对战战报（list类型，value:protobuf定义的BattleRecordData）
	public static final String BATTLE_RECORD_LIST = "battleRecordList";
}
