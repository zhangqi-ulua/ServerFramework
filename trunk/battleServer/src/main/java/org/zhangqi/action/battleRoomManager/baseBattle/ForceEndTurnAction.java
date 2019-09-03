package org.zhangqi.action.battleRoomManager.baseBattle;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.zhangqi.action.BaseMessageAction;
import org.zhangqi.annotation.MessageClassMapping;
import org.zhangqi.constants.SystemConfigKeyConstant;
import org.zhangqi.exception.RpcErrorException;
import org.zhangqi.manager.TableConfigManager;
import org.zhangqi.msg.IMessage;
import org.zhangqi.msg.NetMessage;
import org.zhangqi.proto.BaseBattle.CurrentTurnInfo;
import org.zhangqi.proto.BaseBattle.EndTurnEvent;
import org.zhangqi.proto.BaseBattle.EventMsg;
import org.zhangqi.proto.BaseBattle.EventMsgList;
import org.zhangqi.proto.BaseBattle.EventTypeEnum;
import org.zhangqi.proto.BaseBattle.ForceEndTurnRequest;
import org.zhangqi.proto.BaseBattle.ForceEndTurnResponse;
import org.zhangqi.proto.Rpc.RpcErrorCodeEnum;
import org.zhangqi.proto.Rpc.RpcNameEnum;
import org.zhangqi.service.BaseBattleService;
import org.zhangqi.service.LoadBalanceService;
import org.zhangqi.utils.DateTimeUtil;

@Controller
@MessageClassMapping(value = RpcNameEnum.ForceEndTurn_VALUE, isNet = false)
public class ForceEndTurnAction extends BaseMessageAction {

	private static final Logger logger = LoggerFactory.getLogger(ForceEndTurnAction.class);

	@Autowired
	private BaseBattleService baseBattleService;
	@Autowired
	private LoadBalanceService loadBalanceService;

	@Override
	protected void LogRequest(IMessage requestMessage) throws Exception {
		NetMessage req = (NetMessage) requestMessage;
		logger.info("[req ForceEndTurn userId = {}]:\n{}", req.getUserId(),
				req.getProtobufText(ForceEndTurnRequest.class));
	}

	@Override
	protected void LogResponse(IMessage responseMessage) throws Exception {
		NetMessage resp = (NetMessage) responseMessage;
		logger.info("[resp ForceEndTurn userId = {}]:\n{}", resp.getUserId(),
				resp.getProtobufText(ForceEndTurnResponse.class));
	}

	@Override
	protected IMessage doAction(IMessage requestMessage) throws Exception {
		NetMessage msg = (NetMessage) requestMessage;
		int userId = msg.getUserId();
		ForceEndTurnRequest req = msg.getLite(ForceEndTurnRequest.class);
		CurrentTurnInfo inputForceEndTurnInfo = req.getForceEndTurnInfo();

		String battleId = loadBalanceService.getBattleUserIdToBattleId(userId);
		if (battleId == null) {
			throw new RpcErrorException(RpcErrorCodeEnum.UserNotInBattle_VALUE);
		}

		Set<Integer> notReadyUserIds = baseBattleService.getOnebattleNotReadyUserIds(battleId);
		if (notReadyUserIds != null && notReadyUserIds.size() > 0) {
			throw new RpcErrorException(RpcErrorCodeEnum.BattleNotStart_VALUE);
		}

		ForceEndTurnResponse.Builder builder = ForceEndTurnResponse.newBuilder();

		CurrentTurnInfo currentTurnInfo = baseBattleService.getBattleCurrentTurnInfo(battleId);
		// 判断客户端认为的当前回合信息与实际是否相符
		if (currentTurnInfo.getTurnCount() != inputForceEndTurnInfo.getTurnCount()
				|| currentTurnInfo.getUserId() != inputForceEndTurnInfo.getUserId()) {
			builder.setIsChecked(false);
		} else {
			// 判断是不是真的超时
			long turnMsec = TableConfigManager.getInstance()
					.getSystemIntConfigByKey(SystemConfigKeyConstant.TURN_SECOND) * 1000L;
			long passMsec = DateTimeUtil.getCurrentTimestamp() - currentTurnInfo.getTurnStartTimestamp();
			if (passMsec < turnMsec) {
				builder.setIsChecked(false);
				builder.setForceEndTurnTimestamp(currentTurnInfo.getTurnStartTimestamp() + turnMsec);
				builder.setRemainMsec(turnMsec - passMsec);
			} else {
				// 通过校验，确实因超时要强制结束回合
				builder.setIsChecked(true);

				EndTurnEvent.Builder endTurnEventBuilder = EndTurnEvent.newBuilder();
				endTurnEventBuilder.setEndTurnUserId(currentTurnInfo.getUserId());
				endTurnEventBuilder.setIsForceEndTurn(true);
				EventMsg.Builder eventMsgBuilder = baseBattleService.buildOneEvent(battleId,
						EventTypeEnum.EventTypeEndTurn, endTurnEventBuilder);
				EventMsgList.Builder eventMsgListBuilder = baseBattleService.doEvent(battleId, eventMsgBuilder);

				// 将强制结束回合以及后续事件推送给所有玩家
				baseBattleService.pushEventMsgListToAllBattlePlayers(battleId, eventMsgListBuilder);
			}
		}

		return super.buildResponseNetMsg(userId, RpcNameEnum.ForceEndTurn_VALUE, builder);
	}
}
