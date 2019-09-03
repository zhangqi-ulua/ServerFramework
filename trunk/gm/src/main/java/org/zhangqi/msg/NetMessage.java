package org.zhangqi.msg;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zhangqi.network.session.ISession;
import org.zhangqi.proto.RemoteServer.RemoteRpcErrorCodeEnum;

import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageLite.Builder;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

public class NetMessage extends AbstractMessage {

	private static final Logger logger = LoggerFactory.getLogger(NetMessage.class);

	private byte[] data;
	private int errorCode;

	private ISession session;

	private int userId;
	private String userIp;

	public NetMessage() {
	}

	public NetMessage(int RpcNum, MessageLite lite) {
		this.rpcNum = RpcNum;
		this.errorCode = RemoteRpcErrorCodeEnum.RemoteRpcOk_VALUE;
		this.data = lite.toByteArray();
	}

	public NetMessage(int RpcNum, Builder builder) {
		this.rpcNum = RpcNum;
		this.errorCode = RemoteRpcErrorCodeEnum.RemoteRpcOk_VALUE;
		this.data = builder.build().toByteArray();
	}

	public NetMessage(int RpcNum, byte[] data) {
		this.rpcNum = RpcNum;
		this.errorCode = RemoteRpcErrorCodeEnum.RemoteRpcOk_VALUE;
		this.data = data;
	}

	public NetMessage(int RpcNum, int errorCode) {
		this.rpcNum = RpcNum;
		this.errorCode = errorCode;
		this.data = null;
	}

	private static class MessageLiteCache {
		static final Map<String, MessageLite> cache = new HashMap<String, MessageLite>();

		private MessageLiteCache() {
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T getLite(Class<T> clz) {
		try {
			MessageLite prototype = MessageLiteCache.cache.get(clz.getName());
			if (prototype == null) {
				Method method = clz.getMethod("getDefaultInstance");
				prototype = (MessageLite) method.invoke(null);
				MessageLiteCache.cache.put(clz.getName(), prototype);
			}
			if (null != prototype) {
				return (T) prototype.newBuilderForType().mergeFrom(data).buildPartial();
			}
		} catch (Throwable t) {
			logger.error("getLite error = ", t);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public <T extends MessageOrBuilder> String getProtobufText(Class<T> clz) {
		try {
			MessageLite prototype = MessageLiteCache.cache.get(clz.getName());
			if (prototype == null) {
				Method method = clz.getMethod("getDefaultInstance");
				prototype = (MessageLite) method.invoke(null);
				MessageLiteCache.cache.put(clz.getName(), prototype);
			}
			if (prototype != null) {
				return TextFormat.printToUnicodeString((T) prototype.newBuilderForType().mergeFrom(data));
			}
		} catch (Throwable t) {
			logger.error("getProtobufText error = ", t);
		}

		return null;
	}

	public int getDataLength() {
		return data == null ? 0 : data.length;
	}

	// 获取要发送的二进制的总字节数
	// 包含总字节数、协议名数字、errorCode（前3部分每部分都是4字节）、protobuf二进制部分
	public int getTotalLength() {
		return 12 + getDataLength();
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public ISession getSession() {
		return session;
	}

	public void setSession(ISession session) {
		this.session = session;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUserIp() {
		return userIp;
	}

	public void setUserIp(String userIp) {
		this.userIp = userIp;
	}

	// 转为要传输的二进制
	@Override
	public BinaryWebSocketFrame toBinaryWebSocketFrame() {
		int totalLength = this.getTotalLength();
		ByteBuf out = Unpooled.directBuffer(totalLength);
		out.writeInt(totalLength);
		out.writeInt(this.rpcNum);
		out.writeInt(this.errorCode);
		if (data != null) {
			out.writeBytes(data);
		}

		return new BinaryWebSocketFrame(out);
	}
}
