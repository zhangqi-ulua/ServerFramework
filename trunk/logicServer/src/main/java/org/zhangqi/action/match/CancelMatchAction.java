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
import org.zhangqi.proto.Common.CancelMatchRequest;
import org.zhangqi.proto.Common.CancelMatchResponse;
import org.zhangqi.proto.Common.UserActionStateEnum;
import org.zhangqi.proto.Common.UserState;
import org.zhangqi.proto.Rpc.RpcErrorCodeEnum;
import org.zhangqi.proto.Rpc.RpcNameEnum;
import org.zhangqi.service.MatchService;
import org.zhangqi.service.UserService;

@Controller
@MessageClassMapping(RpcNameEnum.CancelMatch_VALUE)
public class CancelMatchAction extends BaseMessageAction {

	private static final Logger logger = LoggerFactory.getLogger(CancelMatchAction.class);

	@Autowired
	private UserService userService;
	@Autowired
	private MatchService matchService;

	@Override
	protected void LogRequest(IMessage requestMessage) throws Exception {
		NetMessage req = (NetMessage) requestMessage;
		logger.info("[req CancelMatch userId = {}]:\n{}", req.getUserId(),
				req.getProtobufText(CancelMatchRequest.class));
	}

	@Override
	protected void LogResponse(IMessage responseMessage) throws Exception {
		NetMessage resp = (NetMessage) responseMessage;
		logger.info("[resp CancelMatch userId = {}]:\n{}", resp.getUserId(),
				resp.getProtobufText(CancelMatchResponse.class));
	}

	@Override
	protected IMessage doAction(IMessage requestMessage) throws Exception {
		NetMessage msg = (NetMessage) requestMessage;
		int userId = msg.getUserId();

		UserState userState = userService.getUserState(userId);
		UserActionStateEnum actionState = userState.getActionState();
		if (actionState == UserActionStateEnum.Playing) {
			throw new RpcErrorException(RpcErrorCodeEnum.CancelMatchErrorPlaying_VALUE);
		} else if (actionState != UserActionStateEnum.Matching) {
			throw new RpcErrorException(RpcErrorCodeEnum.CancelMatchErrorNotMatching_VALUE);
		}

		if (matchService.removeMatchPlayer(userId, userState.getBattleType()) == false) {
			throw new RpcErrorException(RpcErrorCodeEnum.ServerError_VALUE);
		}

		CancelMatchResponse.Builder builder = CancelMatchResponse.newBuilder();
		return super.buildResponseNetMsg(userId, RpcNameEnum.CancelMatch_VALUE, builder);
	}
}
