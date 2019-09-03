package org.zhangqi.msg;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zhangqi.proto.RemoteServer.RemoteRpcErrorCodeEnum;

import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageLite.Builder;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;

public class RemoteMessage extends AbstractMessage {

	private static final Logger logger = LoggerFactory.getLogger(RemoteMessage.class);

	private byte[] data;
	private int errorCode;

	public RemoteMessage() {
	}

	public RemoteMessage(int RpcNum, MessageLite lite) {
		this.rpcNum = RpcNum;
		this.errorCode = RemoteRpcErrorCodeEnum.RemoteRpcOk_VALUE;
		this.data = lite.toByteArray();
	}

	public RemoteMessage(int RpcNum, Builder builder) {
		this.rpcNum = RpcNum;
		this.errorCode = RemoteRpcErrorCodeEnum.RemoteRpcOk_VALUE;
		this.data = builder.build().toByteArray();
	}

	public RemoteMessage(int RpcNum, byte[] data) {
		this.rpcNum = RpcNum;
		this.errorCode = RemoteRpcErrorCodeEnum.RemoteRpcOk_VALUE;
		this.data = data;
	}

	public RemoteMessage(int RpcNum, int errorCode) {
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
}
