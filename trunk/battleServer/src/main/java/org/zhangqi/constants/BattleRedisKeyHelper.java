package org.zhangqi.constants;

import org.zhangqi.proto.Common.BattleTypeEnum;

import com.google.common.base.Joiner;

public class BattleRedisKeyHelper {

	private static final String SPRITE_STRING = ":";
	private static final Joiner joiner = Joiner.on(SPRITE_STRING);

	public static String join(Object... strs) {
		return joiner.join(strs);
	}

	public static String getBattlePlayingBattleIdsRedisKey(BattleTypeEnum battleType) {
		return join(BattleRedisKeyConstant.BATTLE_PLAYING_BATTLE_IDS, battleType.getNumber());
	}

	public static String getBattleUserIdsRedisKey(String battleId) {
		return join(BattleRedisKeyConstant.BATTLE_USER_IDS, battleId);
	}

	public static String getBattleCurrentTurnInfoRedisKey() {
		return BattleRedisKeyConstant.BATTLE_CURRENT_TURN_INFO;
	}

	public static String getBattleCellInfoRedisKey(String battleId) {
		return join(BattleRedisKeyConstant.BATTLE_CELL_INFO, battleId);
	}

	public static String getBattleEventListRedisKey(String battleId) {
		return join(BattleRedisKeyConstant.BATTLE_EVENT_LIST, battleId);
	}

	public static String getBattleStartTimestampRedisKey() {
		return BattleRedisKeyConstant.BATTLE_START_TIMESTAMP;
	}

	public static String getBattleLastEventNumRedisKey() {
		return BattleRedisKeyConstant.BATTLE_LAST_EVENT_NUM;
	}

	public static String getBattleNotReadyUserIdsRedisKey(String battleId) {
		return join(BattleRedisKeyConstant.BATTLE_NOT_READY_USER_IDS, battleId);
	}

	public static String getBattleRecordListRedisKey(BattleTypeEnum battleType, long oneDayZeroClockTimestamp) {
		return join(BattleRedisKeyConstant.BATTLE_RECORD_LIST, battleType.getNumber(), oneDayZeroClockTimestamp);
	}
}
