package org.zhangqi.actor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zhangqi.annotation.MessageMethodMapping;
import org.zhangqi.exception.RpcErrorException;
import org.zhangqi.manager.MessageManager;
import org.zhangqi.manager.OnlineClientManager;
import org.zhangqi.manager.SpringManager;
import org.zhangqi.msg.IMessage;
import org.zhangqi.msg.NetMessage;
import org.zhangqi.msg.RemoteMessage;
import org.zhangqi.proto.Common.ForceOfflineReasonEnum;
import org.zhangqi.proto.Common.LoginRequest;
import org.zhangqi.proto.Common.LoginResponse;
import org.zhangqi.proto.Common.UserData;
import org.zhangqi.proto.Common.UserForbidInfo;
import org.zhangqi.proto.RemoteServer.LogicServerNoticeGatewayForceOfflineClientPush;
import org.zhangqi.proto.RemoteServer.RemoteRpcNameEnum;
import org.zhangqi.proto.Rpc.RpcErrorCodeEnum;
import org.zhangqi.proto.Rpc.RpcNameEnum;
import org.zhangqi.service.LoadBalanceService;
import org.zhangqi.service.UserService;
import org.zhangqi.utils.DateTimeUtil;
import org.zhangqi.utils.StringUtil;

import akka.actor.ActorRef;

public class LoginActor extends BaseMessageActor {

	private static final Logger logger = LoggerFactory.getLogger(LoginActor.class);

	public LoginActor() {
		super();
	}

	public LoginActor(String actionPackageName) {
		super(actionPackageName);
	}

	private LoadBalanceService loadBalanceService = SpringManager.getInstance().getBean(LoadBalanceService.class);
	private UserService userService = SpringManager.getInstance().getBean(UserService.class);

	// 因为在登录时需拿到本次请求时对应gateway的ResponseActor进行比较
	// 故无法在BaseMessageAction中实现
	@MessageMethodMapping(value = RpcNameEnum.Login_VALUE, isNet = true)
	public void doLoginAction(IMessage requestMessage) throws Exception {
		NetMessage msg = (NetMessage) requestMessage;
		int sessionId = msg.getSessionId();
		LoginRequest req = msg.getLite(LoginRequest.class);
		logger.info("[req Login]:\n{}", msg.getProtobufText(LoginRequest.class));
		String username = req.getUsername();
		String passwordMD5 = req.getPasswordMD5();

		if (StringUtil.isNullOrEmpty(username) || StringUtil.isNullOrEmpty(passwordMD5)) {
			throw new RpcErrorException(RpcErrorCodeEnum.ClientError_VALUE);
		}
		Integer userId = userService.getUserIdByUsername(username);
		if (userId == null) {
			throw new RpcErrorException(RpcErrorCodeEnum.LoginErrorUsernameIsNotExist_VALUE);
		}
		UserData userData = userService.getUserData(userId);
		if (userData.getPasswordMD5().equals(passwordMD5.toUpperCase()) == false) {
			throw new RpcErrorException(RpcErrorCodeEnum.LoginErrorPasswordWrong_VALUE);
		}
		UserData.Builder userDataBuilder = userData.toBuilder();
		if (userData.hasForbidInfo() == true) {
			UserForbidInfo forbidInfo = userData.getForbidInfo();
			if (DateTimeUtil.getCurrentTimestamp() < forbidInfo.getForbidEndTimestamp()) {
				throw new RpcErrorException(RpcErrorCodeEnum.LoginErrorForbid_VALUE);
			} else {
				userDataBuilder.clearForbidInfo();
			}
		}

		// 判断是否是同一账号二次登录
		Integer oldSessionId = loadBalanceService.getOneUserIdToSessionId(userId);
		if (oldSessionId != null) {
			// 通知旧session所在的gateway踢掉之前登录的账号
			Integer gatewayId = loadBalanceService.getOneSessionIdToGatewayId(oldSessionId);
			if (gatewayId == null) {
				logger.error("can't find old session to gatewayId, userId = {}, old sessionId = {}", userId,
						oldSessionId);
			} else {
				LogicServerNoticeGatewayForceOfflineClientPush.Builder pushBuilder = LogicServerNoticeGatewayForceOfflineClientPush
						.newBuilder();
				pushBuilder.setSessionId(oldSessionId);
				pushBuilder.setForceOfflineReason(ForceOfflineReasonEnum.ForceOfflineSameUserLogin);
				RemoteMessage remoteMsg = new RemoteMessage(
						RemoteRpcNameEnum.RemoteRpcLogicServerNoticeGatewayForceOfflineClient_VALUE, pushBuilder);
				if (MessageManager.getInstance().sendRemoteMsgToGataway(remoteMsg, gatewayId) == false) {
					logger.error(
							"can't send force offline clinet remoteMsg to gateway, userId = {}, old sessionId = {}, gatewayId = {}",
							userId, oldSessionId, gatewayId);
				}
			}
		}

		// 记录sessionId与userId的对应关系
		loadBalanceService.setOneUserIdToSessionId(userId, sessionId);
		loadBalanceService.setOneSessionIdToUserId(sessionId, userId);
		// 记录该玩家在gateway的ResponseActor，并为其创建UserActor
		OnlineClientManager.getInstance().addSessionIdToGatewayResponseActor(sessionId, sender());
		ActorRef userActor = MessageManager.getInstance().createUserActor();
		OnlineClientManager.getInstance().addUserActor(sessionId, userActor);

		// 修改玩家状态为在线
		userService.changeUserOnlineState(userId, true);
		// 更新本次登录信息
		userDataBuilder.setLastLoginIp(msg.getUserIp());
		userDataBuilder.setLastLoginTimestamp(DateTimeUtil.getCurrentTimestamp());
		userData = userDataBuilder.build();
		userService.setUserData(userId, userData);

		LoginResponse.Builder builder = LoginResponse.newBuilder();
		builder.setUserInfo(userService.getUserInfo(userId));
		NetMessage respMsg = new NetMessage(RpcNameEnum.Login_VALUE, builder);
		respMsg.setUserId(userId);
		logger.info("[resp Login]:\n{}", respMsg.getProtobufText(LoginResponse.class));
		sender().tell(respMsg, ActorRef.noSender());
	}
}
