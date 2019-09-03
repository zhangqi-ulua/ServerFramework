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
import org.zhangqi.proto.BaseBattle.ConcedeRequest;
import org.zhangqi.proto.BaseBattle.ConcedeResponse;
import org.zhangqi.proto.BaseBattle.EventMsg;
import org.zhangqi.proto.BaseBattle.EventMsgList;
import org.zhangqi.proto.BaseBattle.EventTypeEnum;
import org.zhangqi.proto.BaseBattle.GameOverEvent;
import org.zhangqi.proto.BaseBattle.GameOverReasonEnum;
import org.zhangqi.proto.Rpc.RpcErrorCodeEnum;
import org.zhangqi.proto.Rpc.RpcNameEnum;
import org.zhangqi.service.BaseBattleService;
import org.zhangqi.service.LoadBalanceService;

@Controller
@MessageClassMapping(value = RpcNameEnum.Concede_VALUE, isNet = false)
public class ConcedeAction extends BaseMessageAction {

	private static final Logger logger = LoggerFactory.getLogger(ConcedeAction.class);

	@Autowired
	private BaseBattleService baseBattleService;
	@Autowired
	private LoadBalanceService loadBalanceService;

	@Override
	protected void LogRequest(IMessage requestMessage) throws Exception {
		NetMessage req = (NetMessage) requestMessage;
		logger.info("[req Concede userId = {}]:\n{}", req.getUserId(), req.getProtobufText(ConcedeRequest.class));
	}

	@Override
	protected void LogResponse(IMessage responseMessage) throws Exception {
		NetMessage resp = (NetMessage) responseMessage;
		logger.info("[resp Concede userId = {}]:\n{}", resp.getUserId(), resp.getProtobufText(ConcedeResponse.class));
	}

	@Override
	protected IMessage doAction(IMessage requestMessage) throws Exception {
		NetMessage msg = (NetMessage) requestMessage;
		int userId = msg.getUserId();

		String battleId = loadBalanceService.getBattleUserIdToBattleId(userId);
		if (battleId == null) {
			throw new RpcErrorException(RpcErrorCodeEnum.UserNotInBattle_VALUE);
		}

		Set<Integer> notReadyUserIds = baseBattleService.getOnebattleNotReadyUserIds(battleId);
		if (notReadyUserIds != null && notReadyUserIds.size() > 0) {
			throw new RpcErrorException(RpcErrorCodeEnum.BattleNotStart_VALUE);
		}

		// 这里必须在doGameOverEvent之前取得，否则就会因为redis中对战相关信息被清除而无法取得
		List<Integer> userIds = baseBattleService.getOneBattleUserIds(battleId);

		GameOverEvent.Builder gameOverEventBuilder = GameOverEvent.newBuilder();
		gameOverEventBuilder.setGameOverReason(GameOverReasonEnum.GameOverPlayerConcede);
		gameOverEventBuilder.setWinnerUserId(baseBattleService.getOneUserOneOpponentUserId(battleId, userId));
		EventMsg.Builder eventMsgBuilder = baseBattleService.buildOneEvent(battleId, EventTypeEnum.EventTypeGameOver,
				gameOverEventBuilder);
		EventMsgList.Builder eventMsgListBuilder = baseBattleService.doEvent(battleId, eventMsgBuilder);

		// 推送给本场战斗中的所有玩家
		for (int oneUserId : userIds) {
			baseBattleService.pushEventMsgListToOneBattlePlayer(oneUserId, eventMsgListBuilder);
		}

		ConcedeResponse.Builder builder = ConcedeResponse.newBuilder();
		return super.buildResponseNetMsg(userId, RpcNameEnum.Concede_VALUE, builder);
	}
}
