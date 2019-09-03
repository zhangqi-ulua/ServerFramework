package org.zhangqi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zhangqi.dao.UserDAO;
import org.zhangqi.proto.Common.BattleTypeEnum;
import org.zhangqi.proto.Common.ChannelEnum;
import org.zhangqi.proto.Common.LangEnum;
import org.zhangqi.proto.Common.UserActionStateEnum;
import org.zhangqi.proto.Common.UserBriefInfo;
import org.zhangqi.proto.Common.UserData;
import org.zhangqi.proto.Common.UserInfo;
import org.zhangqi.proto.Common.UserOnlineStateEnum;
import org.zhangqi.proto.Common.UserState;
import org.zhangqi.utils.DateTimeUtil;

@Service(value = "userService")
public class UserService {

	@Autowired
	private UserDAO userDAO;

	public int addAndGetNextAvailableUserId() {
		return userDAO.addAndGetNextAvailableUserId();
	}

	public int getMaxUserId() {
		return userDAO.getMaxUserId();
	}

	public void setUserData(int userId, UserData userData) {
		userDAO.setUserData(userId, userData);
	}

	public boolean hasUserId(int userId) {
		return userDAO.hasUserId(userId);
	}

	public UserData getUserData(int userId) throws Exception {
		return userDAO.getUserData(userId);
	}

	public UserInfo getUserInfo(int userId) throws Exception {
		UserData userData = getUserData(userId);
		if (userData == null) {
			return null;
		} else {
			UserInfo.Builder builder = UserInfo.newBuilder();
			builder.setUserId(userData.getUserId());
			builder.setUsername(userData.getUsername());
			if (userData.hasNickname()) {
				builder.setNickname(userData.getNickname());
			}
			builder.setChannel(userData.getChannel());
			builder.setLang(userData.getLang());
			builder.setUserState(userDAO.getUserState(userId));
			return builder.build();
		}
	}

	public UserBriefInfo getUserBriefInfo(int userId) throws Exception {
		UserData userData = getUserData(userId);
		if (userData == null) {
			return null;
		} else {
			UserBriefInfo.Builder builder = UserBriefInfo.newBuilder();
			builder.setUserId(userId);
			if (userData.hasNickname()) {
				builder.setNickname(userData.getNickname());
			}
			builder.setChannel(userData.getChannel());
			builder.setLang(userData.getLang());
			builder.setUserState(userDAO.getUserState(userId));
			return builder.build();
		}
	}

	public void setUsernameToId(String username, int userId) {
		userDAO.setUsernameToId(username, userId);
	}

	public Integer getUserIdByUsername(String username) {
		return userDAO.getUserIdByUsername(username);
	}

	public boolean hasUsername(String username) {
		return userDAO.hasUsername(username);
	}

	public void setUserState(int userId, UserState userState) {
		userDAO.setUserState(userId, userState);
	}

	public UserState getUserState(int userId) throws Exception {
		return userDAO.getUserState(userId);
	}

	public UserData createNewUser(int zoneNum, String username, String passwordMD5, ChannelEnum channelType,
			LangEnum langType, String ip) {
		int userId = zoneNum + userDAO.addAndGetNextAvailableUserId();

		// 存储username与userId的对应关系
		userDAO.setUsernameToId(username, userId);

		UserData.Builder userDataBuilder = UserData.newBuilder();
		userDataBuilder.setUserId(userId);
		userDataBuilder.setUsername(username);
		userDataBuilder.setPasswordMD5(passwordMD5);
		userDataBuilder.setChannel(channelType);
		userDataBuilder.setLang(langType);
		userDataBuilder.setRegistTimestamp(DateTimeUtil.getCurrentTimestamp());
		userDataBuilder.setRegistIp(ip);
		UserData userData = userDataBuilder.build();
		userDAO.setUserData(userId, userData);

		// 初始化UserState
		UserState.Builder userStateBuilder = UserState.newBuilder();
		userStateBuilder.setOnlineState(UserOnlineStateEnum.Offline);
		userStateBuilder.setActionState(UserActionStateEnum.ActionNone);
		UserState userState = userStateBuilder.build();
		userDAO.setUserState(userId, userState);

		return userData;
	}

	public void changeUserOnlineState(int userId, boolean isOnline) throws Exception {
		UserState.Builder userStateBuilder = userDAO.getUserState(userId).toBuilder();
		userStateBuilder.setOnlineState(isOnline ? UserOnlineStateEnum.Online : UserOnlineStateEnum.Offline);
		userDAO.setUserState(userId, userStateBuilder.build());
	}

	public void changeUserActionStateToNone(int userId) throws Exception {
		UserState.Builder userStateBuilder = userDAO.getUserState(userId).toBuilder();
		userStateBuilder.setActionState(UserActionStateEnum.ActionNone);
		userDAO.setUserState(userId, userStateBuilder.build());
	}

	public void changeUserActionStateToMatching(int userId, BattleTypeEnum battleType) throws Exception {
		UserState.Builder userStateBuilder = userDAO.getUserState(userId).toBuilder();
		userStateBuilder.setActionState(UserActionStateEnum.Matching);
		userStateBuilder.setBattleType(battleType);
		userDAO.setUserState(userId, userStateBuilder.build());
	}

	public void changeUserActionStateToPlaying(int userId, BattleTypeEnum battleType, String battleId)
			throws Exception {
		UserState.Builder userStateBuilder = userDAO.getUserState(userId).toBuilder();
		userStateBuilder.setActionState(UserActionStateEnum.Playing);
		userStateBuilder.setBattleType(battleType);
		userStateBuilder.setBattleId(battleId);
		userDAO.setUserState(userId, userStateBuilder.build());
	}
}
