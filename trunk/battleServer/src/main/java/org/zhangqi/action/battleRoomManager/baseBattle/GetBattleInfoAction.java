package org.zhangqi.action.battleRoomManager.baseBattle;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.zhangqi.action.BaseMessageAction;
import org.zhangqi.annotation.MessageClassMapping;
import org.zhangqi.exception.RpcErrorException;
import org.zhangqi.msg.IMessage;
import org.zhangqi.msg.NetMessage;
import org.zhangqi.proto.BaseBattle.BattleInfo;
import org.zhangqi.proto.BaseBattle.GetBattleInfoRequest;
import org.zhangqi.proto.BaseBattle.GetBattleInfoResponse;
import org.zhangqi.proto.Rpc.RpcErrorCodeEnum;
import org.zhangqi.proto.Rpc.RpcNameEnum;
import org.zhangqi.service.BaseBattleService;
import org.zhangqi.service.LoadBalanceService;
import org.zhangqi.service.UserService;

@Controller
@MessageClassMapping(value = RpcNameEnum.GetBattleInfo_VALUE, isNet = false)
public class GetBattleInfoAction extends BaseMessageAction {

	private static final Logger logger = LoggerFactory.getLogger(GetBattleInfoAction.class);

	@Autowired
	private BaseBattleService baseBattleService;
	@Autowired
	private UserService userService;
	@Autowired
	private LoadBalanceService loadBalanceService;

	@Override
	protected void LogRequest(IMessage requestMessage) throws Exception {
		NetMessage req = (NetMessage) requestMessage;
		logger.info("[req GetBattleInfo userId = {}]:\n{}", req.getUserId(),
				req.getProtobufText(GetBattleInfoRequest.class));
	}

	@Override
	protected void LogResponse(IMessage responseMessage) throws Exception {
		NetMessage resp = (NetMessage) responseMessage;
		logger.info("[resp GetBattleInfo userId = {}]:\n{}", resp.getUserId(),
				resp.getProtobufText(GetBattleInfoResponse.class));
	}

	@Override
	protected IMessage doAction(IMessage requestMessage) throws Exception {
		NetMessage msg = (NetMessage) requestMessage;
		int userId = msg.getUserId();

		String battleId = loadBalanceService.getBattleUserIdToBattleId(userId);
		if (battleId == null) {
			throw new RpcErrorException(RpcErrorCodeEnum.UserNotInBattle_VALUE);
		}

		GetBattleInfoResponse.Builder builder = GetBattleInfoResponse.newBuilder();
		BattleInfo.Builder battleInfoBuilder = BattleInfo.newBuilder();
		List<Integer> userIds = baseBattleService.getOneBattleUserIds(battleId);
		for (int oneUserId : userIds) {
			battleInfoBuilder.addUserBriefInfos(userService.getUserBriefInfo(oneUserId));
		}
		battleInfoBuilder.setBattleStartTimestamp(baseBattleService.getOneBattleStartTimestamp(battleId));
		battleInfoBuilder.addAllBattleCellInfo(baseBattleService.getAllBattleCellInfo(battleId));
		battleInfoBuilder.setLastEventNum(baseBattleService.getLastEventNum(battleId));
		Set<Integer> notReadyUserIds = baseBattleService.getOnebattleNotReadyUserIds(battleId);
		if (notReadyUserIds == null || notReadyUserIds.size() < 1) {
			battleInfoBuilder.setCurrentTurnInfo(baseBattleService.getBattleCurrentTurnInfo(battleId));
		} else {
			battleInfoBuilder.addAllNotReadyUserIds(notReadyUserIds);
		}
		builder.setBattleInfo(battleInfoBuilder);

		return super.buildResponseNetMsg(userId, RpcNameEnum.GetBattleInfo_VALUE, builder);
	}
}
