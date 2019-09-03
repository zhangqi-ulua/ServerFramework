package org.zhangqi.action.serverManager;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.zhangqi.action.BaseMessageAction;
import org.zhangqi.annotation.MessageClassMapping;
import org.zhangqi.constants.GmSessionDataKeyConstant;
import org.zhangqi.msg.IMessage;
import org.zhangqi.msg.NetMessage;
import org.zhangqi.network.session.ISession;
import org.zhangqi.proto.Common.OnlineUserInfo;
import org.zhangqi.proto.Common.UserActionStateEnum;
import org.zhangqi.proto.Common.UserData;
import org.zhangqi.proto.Common.UserState;
import org.zhangqi.proto.Gm.GmGetOnlineUserInfoRequest;
import org.zhangqi.proto.Gm.GmGetOnlineUserInfoResponse;
import org.zhangqi.proto.Gm.GmRpcNameEnum;
import org.zhangqi.service.LoadBalanceService;
import org.zhangqi.service.UserService;

@Controller
@MessageClassMapping(GmRpcNameEnum.GmRpcGetOnlineUserInfo_VALUE)
public class GmGetOnlineUserInfoAction extends BaseMessageAction {

	private static final Logger logger = LoggerFactory.getLogger(GmGetOnlineUserInfoAction.class);

	@Autowired
	private LoadBalanceService loadBalanceService;
	@Autowired
	private UserService userService;

	@Override
	protected void LogRequest(IMessage requestMessage) throws Exception {
		NetMessage req = (NetMessage) requestMessage;
		logger.info("[req GmGetOnlineUserInfo gmUserId = {}]:\n{}",
				req.getSession().getData(GmSessionDataKeyConstant.GM_USER_ID_KEY),
				req.getProtobufText(GmGetOnlineUserInfoRequest.class));
	}

	@Override
	protected void LogResponse(IMessage responseMessage) throws Exception {
		NetMessage resp = (NetMessage) responseMessage;
		logger.info("[resp GmGetOnlineUserInfo gmUserId = {}]:\n{}",
				resp.getSession().getData(GmSessionDataKeyConstant.GM_USER_ID_KEY),
				resp.getProtobufText(GmGetOnlineUserInfoResponse.class));
	}

	@Override
	protected IMessage doAction(IMessage requestMessage) throws Exception {
		ISession session = ((NetMessage) requestMessage).getSession();
		GmGetOnlineUserInfoResponse.Builder builder = GmGetOnlineUserInfoResponse.newBuilder();

		Map<Integer, Integer> allUserIdToSessionId = loadBalanceService.getAllUserIdToSessionId();
		for (Map.Entry<Integer, Integer> entry : allUserIdToSessionId.entrySet()) {
			int userId = entry.getKey();
			int sessionId = entry.getValue();
			UserData userData = userService.getUserData(userId);
			UserState userState = userService.getUserState(userId);
			Integer gatewayId = loadBalanceService.getOneSessionIdToGatewayId(sessionId);
			Integer logicServerId = loadBalanceService.getOneSessionIdToLogicServerId(sessionId);
			OnlineUserInfo.Builder oneInfoBuilder = OnlineUserInfo.newBuilder();
			oneInfoBuilder.setUserId(userId);
			oneInfoBuilder.setChannel(userData.getChannel());
			if (userData.hasUsername()) {
				oneInfoBuilder.setUsername(userData.getUsername());
			}
			if (userData.hasNickname()) {
				oneInfoBuilder.setNickname(userData.getNickname());
			}
			oneInfoBuilder.setUserState(userState);
			oneInfoBuilder.setSessionId(sessionId);
			if (gatewayId != null) {
				oneInfoBuilder.setConnectGatewayId(gatewayId);
			}
			if (logicServerId != null) {
				oneInfoBuilder.setConnectLogicServerId(logicServerId);
			}
			if (userState.getActionState() == UserActionStateEnum.Playing) {
				String battleId = userState.getBattleId();
				Integer battleServerId = loadBalanceService.getOneBattleIdToBattleServerId(battleId);
				if (battleServerId != null) {
					oneInfoBuilder.setConnectBattleServerId(battleServerId);
				}
			}
			oneInfoBuilder.setLoginedTimestamp(userData.getLastLoginTimestamp());

			builder.addOnlineUserInfos(oneInfoBuilder);
		}

		return super.buildResponseNetMsg(session, GmRpcNameEnum.GmRpcGetOnlineUserInfo_VALUE, builder);
	}
}
