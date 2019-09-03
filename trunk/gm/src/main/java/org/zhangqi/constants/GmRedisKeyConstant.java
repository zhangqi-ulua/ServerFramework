package org.zhangqi.constants;

public class GmRedisKeyConstant {

	// GM用户自增的gmUserId（value类型，自增的userId）
	public static final String GM_USER_ID_AUTO_INCREASE = "gmUserIdAutoIncrease";
	// GM用户信息（key:gmUserId, value:protobuf定义的GmUserData）
	public static final String GM_USER_DATA = "gmUserData";
	// GM用户名与gmUserId的对应map（map类型，key:gmUsername, value:gmUserId）
	public static final String GM_USERNAME_TO_ID = "gmUsernameToId";
}
