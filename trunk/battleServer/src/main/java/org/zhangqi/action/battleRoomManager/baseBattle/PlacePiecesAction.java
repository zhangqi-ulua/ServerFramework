package org.zhangqi.action.battleRoomManager.baseBattle;

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
import org.zhangqi.proto.BaseBattle.CurrentTurnInfo;
import org.zhangqi.proto.BaseBattle.EventMsg;
import org.zhangqi.proto.BaseBattle.EventMsgList;
import org.zhangqi.proto.BaseBattle.EventTypeEnum;
import org.zhangqi.proto.BaseBattle.PlacePiecesEvent;
import org.zhangqi.proto.BaseBattle.PlacePiecesRequest;
import org.zhangqi.proto.BaseBattle.PlacePiecesResponse;
import org.zhangqi.proto.Rpc.RpcErrorCodeEnum;
import org.zhangqi.proto.Rpc.RpcNameEnum;
import org.zhangqi.service.BaseBattleService;
import org.zhangqi.service.LoadBalanceService;

@Controller
@MessageClassMapping(value = RpcNameEnum.PlacePieces_VALUE, isNet = false)
public class PlacePiecesAction extends BaseMessageAction {

	private static final Logger logger = LoggerFactory.getLogger(PlacePiecesAction.class);

	@Autowired
	private BaseBattleService baseBattleService;
	@Autowired
	private LoadBalanceService loadBalanceService;

	@Override
	protected void LogRequest(IMessage requestMessage) throws Exception {
		NetMessage req = (NetMessage) requestMessage;
		logger.info("[req PlacePieces userId = {}]:\n{}", req.getUserId(),
				req.getProtobufText(PlacePiecesRequest.class));
	}

	@Override
	protected void LogResponse(IMessage responseMessage) throws Exception {
		NetMessage resp = (NetMessage) responseMessage;
		logger.info("[resp PlacePieces userId = {}]:\n{}", resp.getUserId(),
				resp.getProtobufText(PlacePiecesResponse.class));
	}

	@Override
	protected IMessage doAction(IMessage requestMessage) throws Exception {
		NetMessage msg = (NetMessage) requestMessage;
		int userId = msg.getUserId();
		PlacePiecesRequest req = msg.getLite(PlacePiecesRequest.class);
		int inputLastEventNum = req.getLastEventNum();
		int inputIndex = req.getIndex();

		String battleId = loadBalanceService.getBattleUserIdToBattleId(userId);
		if (battleId == null) {
			throw new RpcErrorException(RpcErrorCodeEnum.UserNotInBattle_VALUE);
		}

		Set<Integer> notReadyUserIds = baseBattleService.getOnebattleNotReadyUserIds(battleId);
		if (notReadyUserIds != null && notReadyUserIds.size() > 0) {
			throw new RpcErrorException(RpcErrorCodeEnum.BattleNotStart_VALUE);
		}

		CurrentTurnInfo currentTurnInfo = baseBattleService.getBattleCurrentTurnInfo(battleId);
		if (currentTurnInfo.getUserId() != userId) {
			throw new RpcErrorException(RpcErrorCodeEnum.IsNotUserTurn_VALUE);
		}

		int lastEventNum = baseBattleService.getLastEventNum(battleId);
		if (lastEventNum != inputLastEventNum) {
			throw new RpcErrorException(RpcErrorCodeEnum.InputLastEventNumError_VALUE);
		}

		if (inputIndex < 0 || inputIndex > 8) {
			throw new RpcErrorException(RpcErrorCodeEnum.PlacePiecesErrorIndexError_VALUE);
		}

		int oneCellInfo = baseBattleService.getOneBattleCellInfo(battleId, inputIndex);
		if (oneCellInfo != 0) {
			logger.error("cell already had pieces, inputIndex = {}, cellInfo = {}, userId = {}", inputIndex,
					oneCellInfo, userId);
			throw new RpcErrorException(RpcErrorCodeEnum.PlacePiecesErrorIndexIsNotEmpty_VALUE);
		}

		// 这里必须在doGameOverEvent之前取得，否则就会因为redis中对战相关信息被清除而无法取得
		int opponentUserId = baseBattleService.getOneUserOneOpponentUserId(battleId, userId);

		PlacePiecesEvent.Builder placePiecesEventBuilder = PlacePiecesEvent.newBuilder();
		placePiecesEventBuilder.setUserId(userId);
		placePiecesEventBuilder.setIndex(inputIndex);
		EventMsg.Builder eventMsgBuilder = baseBattleService.buildOneEvent(battleId, EventTypeEnum.EventTypePlacePieces,
				placePiecesEventBuilder);
		EventMsgList.Builder eventMsgListBuilder = baseBattleService.doEvent(battleId, eventMsgBuilder);

		// 将产生的一系列事件推送给对手
		baseBattleService.pushEventMsgListToOneBattlePlayer(opponentUserId, eventMsgListBuilder);

		PlacePiecesResponse.Builder builder = PlacePiecesResponse.newBuilder();
		builder.setEventList(eventMsgListBuilder);
		return super.buildResponseNetMsg(userId, RpcNameEnum.PlacePieces_VALUE, builder);
	}
}
