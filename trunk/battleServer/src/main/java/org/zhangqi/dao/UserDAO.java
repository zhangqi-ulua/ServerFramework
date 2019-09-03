package org.zhangqi.dao;

import javax.annotation.Resource;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;
import org.zhangqi.constants.RedisKeyHelper;
import org.zhangqi.proto.Common.UserData;
import org.zhangqi.proto.Common.UserState;

@Repository(value = "userDAO")
public class UserDAO {

	@Resource(name = "integerTemplate")
	// 玩家自增的userId（value类型，自增的userId）
	private ValueOperations<String, Integer> userIdAutoIncreaseOps;
	@Resource(name = "byteTemplate")
	// 玩家信息（map类型，key:userId, value:protobuf定义的UserData）
	private HashOperations<String, String, byte[]> userDataOps;
	@Resource(name = "integerTemplate")
	// 玩家名与userId的对应关系（map类型，key:username, value:userId）
	private HashOperations<String, String, Integer> usernameToIdOps;
	@Resource(name = "byteTemplate")
	// 玩家状态（map类型，key:userId, value:protobuf定义的UserState）
	private HashOperations<String, String, byte[]> userStateOps;

	public int addAndGetNextAvailableUserId() {
		return userIdAutoIncreaseOps.increment(RedisKeyHelper.getUserIdAutoIncreaseRedisKey(), 1).intValue();
	}

	public int getMaxUserId() {
		Integer result = userIdAutoIncreaseOps.get(RedisKeyHelper.getUserIdAutoIncreaseRedisKey());
		return result == null ? 0 : result;
	}

	public void setUserData(int userId, UserData userData) {
		userDataOps.put(RedisKeyHelper.getUserDataRedisKey(), String.valueOf(userId), userData.toByteArray());
	}

	public boolean hasUserId(int userId) {
		return userDataOps.hasKey(RedisKeyHelper.getUserDataRedisKey(), String.valueOf(userId));
	}

	public UserData getUserData(int userId) throws Exception {
		byte[] byteArray = userDataOps.get(RedisKeyHelper.getUserDataRedisKey(), String.valueOf(userId));
		return byteArray == null ? null : UserData.parseFrom(byteArray);
	}

	public void setUsernameToId(String username, int userId) {
		usernameToIdOps.put(RedisKeyHelper.getUsernameToIdRedisKey(), username, userId);
	}

	public Integer getUserIdByUsername(String username) {
		return usernameToIdOps.get(RedisKeyHelper.getUsernameToIdRedisKey(), username);
	}

	public boolean hasUsername(String username) {
		return usernameToIdOps.hasKey(RedisKeyHelper.getUsernameToIdRedisKey(), username);
	}

	public void setUserState(int userId, UserState userState) {
		userStateOps.put(RedisKeyHelper.getUserStateRedisKey(), String.valueOf(userId), userState.toByteArray());
	}

	public UserState getUserState(int userId) throws Exception {
		byte[] byteArray = userStateOps.get(RedisKeyHelper.getUserStateRedisKey(), String.valueOf(userId));
		return byteArray == null ? null : UserState.parseFrom(byteArray);
	}
}
