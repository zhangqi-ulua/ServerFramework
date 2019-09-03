package org.zhangqi.redis.serializer;

import java.nio.charset.Charset;

import org.springframework.data.redis.serializer.RedisSerializer;

public class LongRedisSerializer implements RedisSerializer<Long> {

	private final Charset charset;

	public LongRedisSerializer() {
		this(Charset.forName("UTF8"));
	}

	public LongRedisSerializer(Charset charset) {
		this.charset = charset;
	}

	@Override
	public byte[] serialize(Long t) {
		return String.valueOf(t).getBytes(charset);
	}

	@Override
	public Long deserialize(byte[] bytes) {
		return (bytes == null ? null : Long.parseLong(new String(bytes, charset)));
	}
}
