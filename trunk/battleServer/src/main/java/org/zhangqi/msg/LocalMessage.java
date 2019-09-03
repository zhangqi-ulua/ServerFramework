package org.zhangqi.msg;

import java.io.Serializable;

public class LocalMessage extends AbstractMessage implements Serializable {

	private static final long serialVersionUID = -6314048852403909677L;

	public LocalMessage(int rpcNum) {
		this.rpcNum = rpcNum;
	}

	public LocalMessage(int rpcNum, Object lite) {
		this.rpcNum = rpcNum;
		this.lite = lite;
	}
}
