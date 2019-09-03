package org.zhangqi.action.match;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.zhangqi.action.BaseMessageAction;
import org.zhangqi.annotation.MessageClassMapping;
import org.zhangqi.exception.RpcErrorException;
import org.zhangqi.msg.IMessage;
import org.zhangqi.msg.NetMessage;
import org.zhangqi.proto.Common.BattleTypeEnum;
import org.zhangqi.proto.Common.MatchRequest;
import org.zhangqi.proto.Common.MatchResponse;
import org.zhangqi.proto.Common.UserActionStateEnum;
import org.zhangqi.proto.Common.UserState;
import org.zhangqi.proto.Rpc.RpcErrorCodeEnum;
import org.zhangqi.proto.Rpc.RpcNameEnum;
import org.zhangqi.service.MatchService;
import org.zhangqi.service.UserService;

@Controller
@MessageClassMapping(RpcNameEnum.Match_VALUE)
public class MatchAction extends BaseMessageAction {

	private static final Logger logger = LoggerFactory.getLogger(MatchAction.class);

	@Autowired
	private UserService userService;
	@Autowired
	private MatchService matchService;

	@Override
	protected void LogRequest(IMessage requestMessage) throws Exception {
		NetMessage req = (NetMessage) requestMessage;
		logger.info("[req Match userId = {}]:\n{}", req.getUserId(), req.getProtobufText(MatchRequest.class));
	}

	@Override
	protected void LogResponse(IMessage responseMessage) throws Exception {
		NetMessage resp = (NetMessage) responseMessage;
		logger.info("[resp Match userId = {}]:\n{}", resp.getUserId(), resp.getProtobufText(MatchResponse.class));
	}

	@Override
	protected IMessage doAction(IMessage requestMessage) throws Exception {
		NetMessage msg = (NetMessage) requestMessage;
		int userId = msg.getUserId();

		MatchRequest req = msg.getLite(MatchRequest.class);
		BattleTypeEnum battleType = req.getBattleType();

		UserState userState = userService.getUserState(userId);
		UserActionStateEnum actionState = userState.getActionState();
		if (actionState == UserActionStateEnum.Matching) {
			throw new RpcErrorException(RpcErrorCodeEnum.MatchErrorMatching_VALUE);
		} else if (actionState == UserActionStateEnum.Playing) {
			throw new RpcErrorException(RpcErrorCodeEnum.MatchErrorPlaying_VALUE);
		} else if (actionState != UserActionStateEnum.ActionNone) {
			throw new RpcErrorException(RpcErrorCodeEnum.MatchErrorOtherActionState_VALUE);
		}

		if (matchService.addMatchPlayer(userId, battleType) == false) {
			throw new RpcErrorException(RpcErrorCodeEnum.ServerError_VALUE);
		}

		MatchResponse.Builder builder = MatchResponse.newBuilder();
		return super.buildResponseNetMsg(userId, RpcNameEnum.Match_VALUE, builder);
	}
}
