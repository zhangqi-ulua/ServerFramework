package org.zhangqi.network.session;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.zhangqi.constants.GmSessionDataKeyConstant;
import org.zhangqi.msg.IMessage;

import io.netty.channel.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

public class Netty4Session implements ISession {

	private static final AttributeKey<Map<String, Object>> ATTRIBUTE_KEY = AttributeKey.newInstance("data");
	private static AtomicLong idGenerator = new AtomicLong();

	private Channel channel;
	private long sessionId;

	public static ISession getSession(Channel channel) {
		Attribute<Map<String, Object>> attr = channel.attr(ATTRIBUTE_KEY);
		Map<String, Object> map = attr.get();
		if (map == null) {
			map = new HashMap<String, Object>();
			attr.set(map);
		}
		ISession session = (ISession) map.get(GmSessionDataKeyConstant.GM_SESSION_KEY);
		if (session == null) {
			session = new Netty4Session(channel);
			map.put(GmSessionDataKeyConstant.GM_SESSION_KEY, session);
		}

		return session;
	}

	private Netty4Session(Channel channel) {
		this.channel = channel;
		this.sessionId = idGenerator.incrementAndGet();
	}

	@Override
	public void write(IMessage message) {
		if (this.channel != null && this.channel.isActive() && this.channel.isWritable()) {
			this.channel.writeAndFlush(message.toBinaryWebSocketFrame());
		}
	}

	@Override
	public void close() {
		this.channel.close();
	}

	@Override
	public String getRemotePath() {
		return this.channel.remoteAddress().toString();
	}

	@Override
	public long getSessionId() {
		return this.sessionId;
	}

	@Override
	public Object getData(String key) {
		Attribute<Map<String, Object>> attr = this.channel.attr(ATTRIBUTE_KEY);
		return attr.get().get(key);
	}

	@Override
	public void putData(String key, Object value) {
		Attribute<Map<String, Object>> attr = this.channel.attr(ATTRIBUTE_KEY);
		attr.get().put(key, value);
	}

	@Override
	public boolean hasData(String key) {
		Attribute<Map<String, Object>> attr = this.channel.attr(ATTRIBUTE_KEY);
		return attr.get().containsKey(key);
	}
}
