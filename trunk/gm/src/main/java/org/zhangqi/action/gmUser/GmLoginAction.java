package org.zhangqi.action.gmUser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.zhangqi.action.BaseMessageAction;
import org.zhangqi.annotation.MessageClassMapping;
import org.zhangqi.constants.GmSessionDataKeyConstant;
import org.zhangqi.exception.RpcErrorException;
import org.zhangqi.manager.OnlineClientManager;
import org.zhangqi.msg.IMessage;
import org.zhangqi.msg.NetMessage;
import org.zhangqi.network.session.ISession;
import org.zhangqi.proto.Gm.GmForceOfflinePush;
import org.zhangqi.proto.Gm.GmForceOfflineReasonEnum;
import org.zhangqi.proto.Gm.GmLoginRequest;
import org.zhangqi.proto.Gm.GmLoginResponse;
import org.zhangqi.proto.Gm.GmRpcErrorCodeEnum;
import org.zhangqi.proto.Gm.GmRpcNameEnum;
import org.zhangqi.proto.Gm.GmUserData;
import org.zhangqi.service.GmUserService;
import org.zhangqi.utils.DateTimeUtil;
import org.zhangqi.utils.StringUtil;

@Controller
@MessageClassMapping(GmRpcNameEnum.GmRpcLogin_VALUE)
public class GmLoginAction extends BaseMessageAction {

	private static final Logger logger = LoggerFactory.getLogger(GmLoginAction.class);

	@Autowired
	private GmUserService gmUserService;

	@Override
	protected void LogRequest(IMessage requestMessage) throws Exception {
		NetMessage req = (NetMessage) requestMessage;
		logger.info("[req GmLogin]:\n{}", req.getProtobufText(GmLoginRequest.class));
	}

	@Override
	protected void LogResponse(IMessage responseMessage) throws Exception {
		NetMessage resp = (NetMessage) responseMessage;
		logger.info("[resp GmLogin]:\n{}", resp.getProtobufText(GmLoginResponse.class));
	}

	@Override
	protected IMessage doAction(IMessage requestMessage) throws Exception {
		ISession session = ((NetMessage) requestMessage).getSession();
		NetMessage msg = (NetMessage) requestMessage;
		GmLoginRequest req = msg.getLite(GmLoginRequest.class);
		String username = req.getUsername();
		String passwordMD5 = req.getPasswordMD5();

		if (StringUtil.isNullOrEmpty(username)) {
			throw new RpcErrorException(GmRpcErrorCodeEnum.GmRpcLoginErrorUsernameIsNotExist_VALUE);
		}
		Integer gmUserId = gmUserService.getGmUserIdByGmUsername(username);
		if (gmUserId == null) {
			throw new RpcErrorException(GmRpcErrorCodeEnum.GmRpcLoginErrorUsernameIsNotExist_VALUE);
		}
		GmUserData gmUserData = gmUserService.getGmUserData(gmUserId);
		if (gmUserData == null) {
			logger.error("can't find gmUserData by exist gmUserId = {}, gmUsername = {}", gmUserId, username);
			throw new RpcErrorException(GmRpcErrorCodeEnum.GmRpcServerError_VALUE);
		}
		if (gmUserData.getPasswordMD5().equals(passwordMD5) == false) {
			throw new RpcErrorException(GmRpcErrorCodeEnum.GmRpcLoginErrorPasswordWrong_VALUE);
		}

		// 同一GM账号二次登录时，需要踢掉之前登录的
		ISession oldSession = OnlineClientManager.getInstance().getOnlineGmUserSession(gmUserId);
		if (oldSession != null) {
			GmForceOfflinePush.Builder builder = GmForceOfflinePush.newBuilder();
			builder.setForceOfflineReason(GmForceOfflineReasonEnum.GmForceOfflineSameUserLogin);
			NetMessage message = new NetMessage(GmRpcNameEnum.GmRpcForceOfflinePush_VALUE, builder);
			OnlineClientManager.getInstance().pushToOneGmSession(oldSession, message, GmForceOfflinePush.class);
			oldSession.close();
		}

		session.putData(GmSessionDataKeyConstant.GM_USER_ID_KEY, gmUserId);
		OnlineClientManager.getInstance().addOnlineGmUser(gmUserId, session);

		// 修改这个GM用户最后登录时间、IP
		GmUserData.Builder gmUserDataBuilder = gmUserData.toBuilder();
		gmUserDataBuilder.setLastLoginTimestamp(DateTimeUtil.getCurrentTimestamp());
		gmUserDataBuilder.setLastLoginIp(msg.getUserIp());
		gmUserService.setGmUserData(gmUserId, gmUserDataBuilder.build());

		logger.info("gmUser login success, gmUserId = {}, gmUsername = {}", gmUserId, username);

		GmLoginResponse.Builder builder = GmLoginResponse.newBuilder();
		builder.setGmUserInfo(gmUserService.getGmUserInfo(gmUserId));
		return super.buildResponseNetMsg(session, GmRpcNameEnum.GmRpcLogin_VALUE, builder);
	}
}
