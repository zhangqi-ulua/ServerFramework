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
import org.zhangqi.proto.BaseBattle.ReadyToStartGameRequest;
import org.zhangqi.proto.BaseBattle.ReadyToStartGameResponse;
import org.zhangqi.proto.Rpc.RpcErrorCodeEnum;
import org.zhangqi.proto.Rpc.RpcNameEnum;
import org.zhangqi.service.BaseBattleService;
import org.zhangqi.service.LoadBalanceService;

@Controller
@MessageClassMapping(value = RpcNameEnum.ReadyToStartGame_VALUE, isNet = false)
public class ReadyToStartGameAction extends BaseMessageAction {

	private static final Logger logger = LoggerFactory.getLogger(ReadyToStartGameAction.class);

	@Autowired
	private BaseBattleService baseBattleService;
	@Autowired
	private LoadBalanceService loadBalanceService;

	@Override
	protected void LogRequest(IMessage requestMessage) throws Exception {
		NetMessage req = (NetMessage) requestMessage;
		logger.info("[req ReadyToStartGame userId = {}]:\n{}", req.getUserId(),
				req.getProtobufText(ReadyToStartGameRequest.class));
	}

	@Override
	protected void LogResponse(IMessage responseMessage) throws Exception {
		NetMessage resp = (NetMessage) responseMessage;
		logger.info("[resp ReadyToStartGame userId = {}]:\n{}", resp.getUserId(),
				resp.getProtobufText(ReadyToStartGameResponse.class));
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
		if (notReadyUserIds == null || notReadyUserIds.contains(userId) == false) {
			throw new RpcErrorException(RpcErrorCodeEnum.ReadyToStartGameErrorAlreadyReady_VALUE);
		}
		notReadyUserIds.remove(userId);
		baseBattleService.removeOnebattleNotReadyUserId(battleId, userId);

		if (notReadyUserIds.size() < 1) {
			baseBattleService.startFirstTurn(battleId);
		}

		ReadyToStartGameResponse.Builder builder = ReadyToStartGameResponse.newBuilder();
		return super.buildResponseNetMsg(userId, RpcNameEnum.ReadyToStartGame_VALUE, builder);
	}
}
