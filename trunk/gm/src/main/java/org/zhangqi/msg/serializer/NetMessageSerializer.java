package org.zhangqi.msg.serializer;

import java.io.NotSerializableException;
import java.nio.ByteBuffer;

import org.zhangqi.msg.NetMessage;

import akka.serialization.Serializer;
import scala.Option;

// 编解码规则：4字节协议名数字、4字节errorCode，4字节userId，4字节userIp的长度，之后如果发userIp，则是string转为的二进制，最后是protobuf二进制数据
public class NetMessageSerializer implements Serializer {

	public NetMessageSerializer() {
	}

	private NetMessage toMsg(byte[] array) {
		NetMessage msg = new NetMessage();
		int totalLength = array.length;
		ByteBuffer bb = ByteBuffer.wrap(array);
		msg.setRpcNum(bb.getInt());
		msg.setErrorCode(bb.getInt());
		msg.setUserId(bb.getInt());
		int ipLength = bb.getInt();
		if (ipLength > 0) {
			byte[] ipBytes = new byte[ipLength];
			bb.get(ipBytes);
			msg.setUserIp(new String(ipBytes));
		}
		byte[] bytes = new byte[totalLength - ipLength - 16];
		bb.get(bytes);
		msg.setData(bytes);
		return msg;
	}

	@Override
	public byte[] toBinary(Object arg0) {
		NetMessage msg = (NetMessage) arg0;

		int ipLength = 0;
		byte[] ipBytes = null;
		if (msg.getUserIp() != null) {
			ipBytes = msg.getUserIp().getBytes();
			ipLength = ipBytes.length;
		}

		int totalLength = 16 + ipLength + msg.getDataLength();
		ByteBuffer bb = ByteBuffer.allocate(totalLength);
		bb.putInt(msg.getRpcNum());
		bb.putInt(msg.getErrorCode());
		bb.putInt(msg.getUserId());
		bb.putInt(ipLength);
		if (ipBytes != null) {
			bb.put(ipBytes);
		}
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
		return 77;
	}

	@Override
	public boolean includeManifest() {
		return false;
	}
}
