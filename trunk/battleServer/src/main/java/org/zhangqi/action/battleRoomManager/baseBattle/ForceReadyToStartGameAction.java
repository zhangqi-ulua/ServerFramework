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
import org.zhangqi.proto.BaseBattle.ForceReadyToStartGameRequest;
import org.zhangqi.proto.BaseBattle.ForceReadyToStartGameResponse;
import org.zhangqi.proto.Rpc.RpcErrorCodeEnum;
import org.zhangqi.proto.Rpc.RpcNameEnum;
import org.zhangqi.service.BaseBattleService;
import org.zhangqi.service.LoadBalanceService;
import org.zhangqi.utils.DateTimeUtil;

@Controller
@MessageClassMapping(value = RpcNameEnum.ForceReadyToStartGame_VALUE, isNet = false)
public class ForceReadyToStartGameAction extends BaseMessageAction {

	private static final Logger logger = LoggerFactory.getLogger(ForceReadyToStartGameAction.class);

	@Autowired
	private BaseBattleService baseBattleService;
	@Autowired
	private LoadBalanceService loadBalanceService;

	@Override
	protected void LogRequest(IMessage requestMessage) throws Exception {
		NetMessage req = (NetMessage) requestMessage;
		logger.info("[req ForceReadyToStartGame userId = {}]:\n{}", req.getUserId(),
				req.getProtobufText(ForceReadyToStartGameRequest.class));
	}

	@Override
	protected void LogResponse(IMessage responseMessage) throws Exception {
		NetMessage resp = (NetMessage) responseMessage;
		logger.info("[resp ForceReadyToStartGame userId = {}]:\n{}", resp.getUserId(),
				resp.getProtobufText(ForceReadyToStartGameResponse.class));
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
		if (notReadyUserIds == null || notReadyUserIds.size() < 1) {
			throw new RpcErrorException(RpcErrorCodeEnum.ForceReadyToStartGameErrorAlreadyStart_VALUE);
		}

		ForceReadyToStartGameResponse.Builder builder = ForceReadyToStartGameResponse.newBuilder();

		long battleStartTimestamp = baseBattleService.getOneBattleStartTimestamp(battleId);
		long currentTimestamp = DateTimeUtil.getCurrentTimestamp();
		long readyToStartGameMsec = TableConfigManager.getInstance()
				.getSystemIntConfigByKey(SystemConfigKeyConstant.READY_TO_START_GAME_SECOND) * 1000L;

		long passMsec = currentTimestamp - battleStartTimestamp;
		if (passMsec >= readyToStartGameMsec) {
			builder.setIsChecked(true);

			baseBattleService.cleanOnebattleNotReadyUserIds(battleId);
			baseBattleService.startFirstTurn(battleId);
		} else {
			builder.setIsChecked(false);
			builder.setForceStartGameTimestamp(battleStartTimestamp + readyToStartGameMsec);
			builder.setRemainMsec(readyToStartGameMsec - passMsec);
		}

		return super.buildResponseNetMsg(userId, RpcNameEnum.ForceReadyToStartGame_VALUE, builder);
	}
}
