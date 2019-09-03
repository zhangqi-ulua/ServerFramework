package org.zhangqi.constants;

import com.google.common.base.Joiner;

public class GmRedisKeyHelper {

	private static final String SPRITE_STRING = ":";
	private static final Joiner joiner = Joiner.on(SPRITE_STRING);

	public static String join(Object... strs) {
		return joiner.join(strs);
	}

	public static String getGmUserIdAutoIncreaseRedisKey() {
		return GmRedisKeyConstant.GM_USER_ID_AUTO_INCREASE;
	}

	public static String getGmUserDataRedisKey() {
		return GmRedisKeyConstant.GM_USER_DATA;
	}

	public static String getGmUsernameToIdRedisKey() {
		return GmRedisKeyConstant.GM_USERNAME_TO_ID;
	}
}
