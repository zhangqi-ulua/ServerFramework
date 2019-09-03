package org.zhangqi.action.regist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.zhangqi.action.BaseMessageAction;
import org.zhangqi.annotation.MessageClassMapping;
import org.zhangqi.constants.SystemConfigKeyConstant;
import org.zhangqi.exception.RpcErrorException;
import org.zhangqi.manager.ConfigManager;
import org.zhangqi.manager.TableConfigManager;
import org.zhangqi.msg.IMessage;
import org.zhangqi.msg.NetMessage;
import org.zhangqi.proto.Common.ChannelEnum;
import org.zhangqi.proto.Common.LangEnum;
import org.zhangqi.proto.Common.RegistRequest;
import org.zhangqi.proto.Common.RegistResponse;
import org.zhangqi.proto.Rpc.RpcErrorCodeEnum;
import org.zhangqi.proto.Rpc.RpcNameEnum;
import org.zhangqi.service.UserService;
import org.zhangqi.utils.MD5Util;
import org.zhangqi.utils.StringUtil;

@Controller
@MessageClassMapping(RpcNameEnum.Regist_VALUE)
public class RegistAction extends BaseMessageAction {

	private static final Logger logger = LoggerFactory.getLogger(RegistAction.class);

	@Autowired
	private UserService userService;

	@Override
	protected void LogRequest(IMessage requestMessage) throws Exception {
		NetMessage req = (NetMessage) requestMessage;
		logger.info("[req Regist]:\n{}", req.getProtobufText(RegistRequest.class));
	}

	@Override
	protected void LogResponse(IMessage responseMessage) throws Exception {
		NetMessage resp = (NetMessage) responseMessage;
		logger.info("[resp Regist]:\n{}", resp.getProtobufText(RegistResponse.class));
	}

	@Override
	protected IMessage doAction(IMessage requestMessage) throws Exception {
		NetMessage msg = (NetMessage) requestMessage;
		RegistRequest req = msg.getLite(RegistRequest.class);
		String username = req.getUsername();
		String password = req.getPassword();
		ChannelEnum channelType = req.hasChannel() ? req.getChannel() : ChannelEnum.ChannelNone;
		LangEnum langType = req.hasLang() ? req.getLang() : LangEnum.LangCn;

		if (isLegalUsername(username) == false) {
			throw new RpcErrorException(RpcErrorCodeEnum.RegisErrorUsernameIllegal_VALUE);
		}
		if (isLegalPassword(password) == false) {
			throw new RpcErrorException(RpcErrorCodeEnum.RegisErrorPasswordIllegal_VALUE);
		}
		if (userService.hasUsername(username) == true) {
			throw new RpcErrorException(RpcErrorCodeEnum.RegisErrorUsernameIsExist_VALUE);
		}

		userService.createNewUser(ConfigManager.ZONE_ID * ConfigManager.ZONE_ID_TIMES, username, MD5Util.md5(password),
				channelType, langType, msg.getUserIp());

		RegistResponse.Builder builder = RegistResponse.newBuilder();
		return super.buildResponseNetMsg(0, RpcNameEnum.Regist_VALUE, builder);
	}

	// username需满足SystemConfig配置的长度要求，且只能由数字或字母组成
	private boolean isLegalUsername(String username) {
		if (StringUtil.isNullOrEmpty(username) || username.length() > TableConfigManager.getInstance()
				.getSystemIntConfigByKey(SystemConfigKeyConstant.USERNAME_MAX_LENGTH)) {
			return false;
		}
		for (char c : username.toCharArray()) {
			if (StringUtil.isLetterChar(c) == false && StringUtil.isDigitChar(c) == false) {
				return false;
			}
		}

		return true;
	}

	// password需满足SystemConfig配置的长度要求，且只能由数字或字母组成
	private boolean isLegalPassword(String password) {
		if (StringUtil.isNullOrEmpty(password)
				|| password.length() < TableConfigManager.getInstance()
						.getSystemIntConfigByKey(SystemConfigKeyConstant.PASSWORD_MIN_LENGTH)
				|| password.length() > TableConfigManager.getInstance()
						.getSystemIntConfigByKey(SystemConfigKeyConstant.PASSWORD_MAX_LENGTH)) {
			return false;
		}
		for (char c : password.toCharArray()) {
			if (StringUtil.isLetterChar(c) == false && StringUtil.isDigitChar(c) == false) {
				return false;
			}
		}

		return true;
	}
}
