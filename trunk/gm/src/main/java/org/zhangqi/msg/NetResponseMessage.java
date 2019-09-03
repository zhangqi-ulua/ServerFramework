package org.zhangqi.msg;

public class NetResponseMessage {

	private NetMessage netMessage;

	public NetResponseMessage(NetMessage netMessage) {
		this.netMessage = netMessage;
	}

	public NetMessage getNetMessage() {
		return netMessage;
	}

	public void setNetMessage(NetMessage netMessage) {
		this.netMessage = netMessage;
	}
}
