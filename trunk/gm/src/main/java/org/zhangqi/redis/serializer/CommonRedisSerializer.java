package org.zhangqi.redis.serializer;

import java.nio.charset.Charset;
import java.util.regex.Pattern;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

public class CommonRedisSerializer implements RedisSerializer<Object> {

	private final Charset charset;

	public CommonRedisSerializer() {
		this(Charset.forName("UTF8"));
	}

	public CommonRedisSerializer(Charset charset) {
		this.charset = charset;
	}

	@Override
	public byte[] serialize(Object t) throws SerializationException {
		return String.valueOf(t).getBytes(charset);
	}

	@Override
	public Object deserialize(byte[] bytes) throws SerializationException {
		if (bytes == null)
			return null;
		String value = new String(bytes, charset);
		if (isInteger(value)) {
			return Integer.parseInt(value);
		}
		return value;
	}

	public static boolean isInteger(String str) {
		Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
		return pattern.matcher(str).matches();
	}
}
