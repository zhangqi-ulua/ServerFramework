package org.zhangqi.msg;

import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

public abstract class AbstractMessage implements IMessage {

	protected int rpcNum;
	protected Object lite;

	@Override
	public int getRpcNum() {
		return rpcNum;
	}

	@Override
	public void setRpcNum(int rpcNum) {
		this.rpcNum = rpcNum;
	}

	@Override
	public Object getLite() {
		return lite;
	}

	@Override
	public void setLite(Object lite) {
		this.lite = lite;
	}

	@Override
	public BinaryWebSocketFrame toBinaryWebSocketFrame() {
		return null;
	}
}
