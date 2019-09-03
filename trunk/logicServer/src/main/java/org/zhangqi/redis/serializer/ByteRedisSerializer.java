package org.zhangqi.redis.serializer;

import java.nio.charset.Charset;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

public class ByteRedisSerializer implements RedisSerializer<byte[]> {

	public ByteRedisSerializer() {
		this(Charset.forName("UTF8"));
	}

	public ByteRedisSerializer(Charset charset) {
	}

	@Override
	public byte[] serialize(byte[] t) throws SerializationException {
		return t;
	}

	@Override
	public byte[] deserialize(byte[] bytes) throws SerializationException {
		return bytes;
	}

}
