package org.zhangqi.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zhangqi.manager.MessageManager;
import org.zhangqi.msg.NetMessage;
import org.zhangqi.proto.Common.BattleTypeEnum;
import org.zhangqi.proto.Common.MatchResultPush;
import org.zhangqi.proto.Rpc.RpcNameEnum;

@Service(value = "matchService")
public class MatchService {

	private static final Logger logger = LoggerFactory.getLogger(MatchService.class);

	@Autowired
	UserService userService;

	private final Vector<Integer> matchTwoPlayerBattleUserIds = new Vector<Integer>();

	private boolean isStopMatch = false;

	public void doMatch() throws Exception {
		if (isStopMatch == false) {
			doSimpleTwoPlayerBattleMatch();
		}
	}

	public boolean addMatchPlayer(int userId, BattleTypeEnum battleType) throws Exception {
		switch (battleType.getNumber()) {
		case BattleTypeEnum.BattleTypeTwoPlayer_VALUE: {
			matchTwoPlayerBattleUserIds.add(userId);
			break;
		}
		default: {
			logger.error("addMatchPlayer error, unsupport battleType = {}", battleType);
			return false;
		}
		}

		// 修改玩家action状态为匹配中
		userService.changeUserActionStateToMatching(userId, battleType);
		return true;

	}

	public boolean removeMatchPlayer(Integer userId, BattleTypeEnum battleType) throws Exception {
		boolean isRemoveSuccess = false;
		switch (battleType.getNumber()) {
		case BattleTypeEnum.BattleTypeTwoPlayer_VALUE: {
			isRemoveSuccess = matchTwoPlayerBattleUserIds.remove(userId);
			break;
		}
		default: {
			logger.error("removeMatchPlayer error, unsupport battleType = {}", battleType);
			return false;
		}
		}

		if (isRemoveSuccess == true) {
			// 清除玩家匹配中的action状态
			userService.changeUserActionStateToNone(userId);
			return true;
		} else {
			return false;
		}
	}

	public void stopMatch() throws Exception {
		isStopMatch = true;
		for (int userId : matchTwoPlayerBattleUserIds) {
			userService.changeUserActionStateToNone(userId);
		}
	}

	private void doSimpleTwoPlayerBattleMatch() throws Exception {
		while (matchTwoPlayerBattleUserIds.size() > 1) {
			List<Integer> matchUserIds = new ArrayList<Integer>();
			matchUserIds.add(matchTwoPlayerBattleUserIds.remove(0));
			matchUserIds.add(matchTwoPlayerBattleUserIds.remove(0));
			Collections.shuffle(matchUserIds);

			logger.info("match twoPlayerBattle userId = {} and {}", matchUserIds.get(0), matchUserIds.get(1));
			doAfterMatchSuccess(BattleTypeEnum.BattleTypeTwoPlayer, matchUserIds);
		}
	}

	private void doAfterMatchSuccess(BattleTypeEnum battleType, List<Integer> userIds) throws Exception {
		String battleId = generateBattleId(battleType);

		if (MessageManager.getInstance().noticeBattleServerCreateNewBattle(battleType, battleId, userIds) == false) {
			sendMatchFailPush(battleType, userIds);
		} else {
			// 将玩家标记为对战状态
			for (int userId : userIds) {
				userService.changeUserActionStateToPlaying(userId, battleType, battleId);
			}
		}
	}

	private void sendMatchFailPush(BattleTypeEnum battleType, List<Integer> userIds) {
		MatchResultPush.Builder pushBuilder = MatchResultPush.newBuilder();
		pushBuilder.setIsSuccess(false);
		pushBuilder.setBattleType(battleType);
		NetMessage netMsg = new NetMessage(RpcNameEnum.MatchResultPush_VALUE, pushBuilder);

		for (int userId : userIds) {
			MessageManager.getInstance().sendNetMsgToOneUser(userId, netMsg, MatchResultPush.class);
		}
	}

	private String generateBattleId(BattleTypeEnum battleType) {
		return battleType.getNumber() + "_" + UUID.randomUUID().toString().replace("-", "");
	}
}
