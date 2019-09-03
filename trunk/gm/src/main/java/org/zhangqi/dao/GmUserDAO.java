package org.zhangqi.dao;

import javax.annotation.Resource;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;
import org.zhangqi.constants.GmRedisKeyHelper;
import org.zhangqi.proto.Gm.GmUserData;

@Repository(value = "gmUserDAO")
public class GmUserDAO {

	@Resource(name = "integerTemplate")
	// GM用户自增的gmUserId（value类型，自增的userId）
	private ValueOperations<String, Integer> gmUserIdAutoIncreaseOps;
	@Resource(name = "byteTemplate")
	// GM用户信息（key:gmUserId, value:protobuf定义的GmUserData）
	private HashOperations<String, String, byte[]> gmUserDataOps;
	@Resource(name = "integerTemplate")
	// GM用户名与gmUserId的对应map（map类型，key:gmUsername, value:gmUserId）
	private HashOperations<String, String, Integer> gmUsernameToIdOps;

	public int addAndGetNextAvailableGmUserId() {
		return gmUserIdAutoIncreaseOps.increment(GmRedisKeyHelper.getGmUserIdAutoIncreaseRedisKey(), 1).intValue();
	}

	public int getMaxGmUserId() {
		Integer result = gmUserIdAutoIncreaseOps.get(GmRedisKeyHelper.getGmUserIdAutoIncreaseRedisKey());
		return result == null ? 0 : result;
	}

	public void setGmUserData(int gmUserId, GmUserData gmUserData) {
		gmUserDataOps.put(GmRedisKeyHelper.getGmUserDataRedisKey(), String.valueOf(gmUserId), gmUserData.toByteArray());
	}

	public boolean hasGmUserId(int gmUserId) {
		return gmUserDataOps.hasKey(GmRedisKeyHelper.getGmUserDataRedisKey(), String.valueOf(gmUserId));
	}

	public GmUserData getGmUserData(int gmUserId) throws Exception {
		byte[] byteArray = gmUserDataOps.get(GmRedisKeyHelper.getGmUserDataRedisKey(), String.valueOf(gmUserId));
		return byteArray == null ? null : GmUserData.parseFrom(byteArray);
	}

	public void setGmUsernameToId(String gmUsername, int gmUserId) {
		gmUsernameToIdOps.put(GmRedisKeyHelper.getGmUsernameToIdRedisKey(), gmUsername, gmUserId);
	}

	public Integer getGmUserIdByGmUsername(String gmUsername) {
		return gmUsernameToIdOps.get(GmRedisKeyHelper.getGmUsernameToIdRedisKey(), gmUsername);
	}

	public boolean hasGmUsername(String gmUsername) {
		return gmUsernameToIdOps.hasKey(GmRedisKeyHelper.getGmUsernameToIdRedisKey(), gmUsername);
	}
}
