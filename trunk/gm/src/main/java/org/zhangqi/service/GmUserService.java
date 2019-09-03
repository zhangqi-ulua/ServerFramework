package org.zhangqi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zhangqi.dao.GmUserDAO;
import org.zhangqi.proto.Gm.GmUserData;
import org.zhangqi.proto.Gm.GmUserInfo;
import org.zhangqi.utils.DateTimeUtil;

@Service(value = "gmUserService")
public class GmUserService {

	@Autowired
	private GmUserDAO gmUserDAO;

	public int addAndGetNextAvailableGmUserId() {
		return gmUserDAO.addAndGetNextAvailableGmUserId();
	}

	public int getMaxGmUserId() {
		return gmUserDAO.getMaxGmUserId();
	}

	public void setGmUserData(int gmUserId, GmUserData gmUserData) {
		gmUserDAO.setGmUserData(gmUserId, gmUserData);
	}

	public boolean hasGmUserId(int gmUserId) {
		return gmUserDAO.hasGmUserId(gmUserId);
	}

	public GmUserData getGmUserData(int gmUserId) throws Exception {
		return gmUserDAO.getGmUserData(gmUserId);
	}

	public void setGmUsernameToId(String gmUsername, int gmUserId) {
		gmUserDAO.setGmUsernameToId(gmUsername, gmUserId);
	}

	public Integer getGmUserIdByGmUsername(String gmUsername) {
		return gmUserDAO.getGmUserIdByGmUsername(gmUsername);
	}

	public boolean hasGmUsername(String gmUsername) {
		return gmUserDAO.hasGmUsername(gmUsername);
	}

	public GmUserData createNewGmUser(String username, String passwordMD5) {
		int gmUserId = gmUserDAO.addAndGetNextAvailableGmUserId();

		// 存储gmUsername与gmUserId的对应关系
		gmUserDAO.setGmUsernameToId(username, gmUserId);

		GmUserData.Builder gmUserDataBuilder = GmUserData.newBuilder();
		gmUserDataBuilder.setUserId(gmUserId);
		gmUserDataBuilder.setUsername(username);
		gmUserDataBuilder.setPasswordMD5(passwordMD5);
		gmUserDataBuilder.setRegistTimestamp(DateTimeUtil.getCurrentTimestamp());
		GmUserData gmUserData = gmUserDataBuilder.build();

		gmUserDAO.setGmUserData(gmUserId, gmUserData);
		return gmUserData;
	}

	public GmUserInfo.Builder getGmUserInfo(int gmUserId) throws Exception {
		GmUserData gmUserData = getGmUserData(gmUserId);
		if (gmUserData == null) {
			return null;
		} else {
			GmUserInfo.Builder builder = GmUserInfo.newBuilder();
			builder.setUserId(gmUserId);
			builder.setUsername(gmUserData.getUsername());
			return builder;
		}
	}
}
