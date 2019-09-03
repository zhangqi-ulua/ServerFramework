package org.zhangqi.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MD5Util {

	private static final Logger logger = LoggerFactory.getLogger(MD5Util.class);

	private static MessageDigest md5;

	private static char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e',
			'f' };
	static {
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			logger.error("init fail, error = ", e);
		}
	}

	private static String md5(String str, String charset) {
		StringBuilder sb = new StringBuilder();
		try {
			byte[] bb = md5.digest(str.getBytes(charset));
			for (byte b : bb) {
				sb.append(hexDigits[b >>> 4 & 0xf]);
				sb.append(hexDigits[b & 0xf]);
			}
		} catch (UnsupportedEncodingException e) {
			logger.error("md5 error, error = ", e);
		}

		return sb.toString();
	}

	public static String md5(byte[] data) {
		StringBuilder sb = new StringBuilder();
		byte[] bb = md5.digest(data);
		for (byte b : bb) {
			sb.append(hexDigits[b >>> 4 & 0xf]);
			sb.append(hexDigits[b & 0xf]);
		}
		return sb.toString().toUpperCase();
	}

	public static String md5(String str) {
		return md5(str, "UTF-8").toUpperCase();
	}
}
