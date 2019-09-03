package org.zhangqi.network.session;

import org.zhangqi.msg.IMessage;

public interface ISession {

	void write(IMessage message);

	void close();

	String getRemotePath();

	long getSessionId();

	Object getData(String key);

	void putData(String key, Object value);

	boolean hasData(String key);
}
