package org.zhangqi.utils;

import java.util.Collection;

public class StringUtil {

	public static boolean isNullOrEmpty(String str) {
		return str == null || "".equals(str);
	}

	public static boolean isDigitChar(char c) {
		return c >= '0' && c <= '9';
	}

	public static boolean isLetterChar(char c) {
		return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
	}

	public static boolean isBaseChineseChar(char c) {
		return c >= '\u4E00' && c <= '\u9FA5';
	}

	public static <T> String getCollectionMemberString(Collection<T> collection, String splitString) {
		if (collection == null || collection.size() == 0) {
			return "";
		} else {
			if (StringUtil.isNullOrEmpty(splitString)) {
				splitString = ",";
			}
			StringBuilder sb = new StringBuilder();
			for (T t : collection) {
				sb.append(t.toString()).append(splitString);
			}
			// 去掉末尾多加的一次分隔符
			sb.delete(sb.length() - splitString.length(), sb.length());
			return sb.toString();
		}
	}
}
