package org.zhangqi.msg;

import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

public interface IMessage {

	public int getRpcNum();

	public void setRpcNum(int rpcNum);

	public Object getLite();

	public void setLite(Object lite);

	public BinaryWebSocketFrame toBinaryWebSocketFrame();
}
