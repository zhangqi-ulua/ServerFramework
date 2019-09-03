package org.zhangqi.msg.serializer;

import java.io.NotSerializableException;
import java.nio.ByteBuffer;

import org.zhangqi.msg.RemoteMessage;

import akka.serialization.Serializer;
import scala.Option;

// 编解码规则：4字节协议名数字、4字节errorCode，剩下的是protobuf二进制数据
public class RemoteMessageSerializer implements Serializer {

	public RemoteMessageSerializer() {
	}

	private RemoteMessage toMsg(byte[] array) {
		RemoteMessage msg = new RemoteMessage();
		int totalLength = array.length;
		ByteBuffer bb = ByteBuffer.wrap(array);
		msg.setRpcNum(bb.getInt());
		msg.setErrorCode(bb.getInt());
		byte[] bytes = new byte[totalLength - 8];
		bb.get(bytes);
		msg.setData(bytes);
		return msg;
	}

	@Override
	public byte[] toBinary(Object arg0) {
		RemoteMessage msg = (RemoteMessage) arg0;
		ByteBuffer bb = ByteBuffer.allocate(8 + msg.getDataLength());
		bb.putInt(msg.getRpcNum());
		bb.putInt(msg.getErrorCode());
		if (msg.getData() != null) {
			bb.put(msg.getData());
		}
		return bb.array();
	}

	@Override
	public Object fromBinary(byte[] arg0) {
		return toMsg(arg0);
	}

	@Override
	public Object fromBinary(byte[] arg0, Option<Class<?>> arg1) throws NotSerializableException {
		return toMsg(arg0);
	}

	@Override
	public Object fromBinary(byte[] arg0, Class<?> arg1) throws NotSerializableException {
		return toMsg(arg0);
	}

	// 0-40被akka占用
	@Override
	public int identifier() {
		return 78;
	}

	@Override
	public boolean includeManifest() {
		return false;
	}
}
