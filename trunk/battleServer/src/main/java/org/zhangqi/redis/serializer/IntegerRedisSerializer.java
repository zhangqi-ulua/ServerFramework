package org.zhangqi.redis.serializer;

import java.nio.charset.Charset;

import org.springframework.data.redis.serializer.RedisSerializer;

public class IntegerRedisSerializer implements RedisSerializer<Integer> {

	private final Charset charset;

	public IntegerRedisSerializer() {
		this(Charset.forName("UTF8"));
	}

	public IntegerRedisSerializer(Charset charset) {
		this.charset = charset;
	}

	@Override
	public byte[] serialize(Integer t) {
		return String.valueOf(t).getBytes(charset);
	}

	@Override
	public Integer deserialize(byte[] bytes) {
		return (bytes == null ? null : Integer.parseInt(new String(bytes, charset)));
	}
}
