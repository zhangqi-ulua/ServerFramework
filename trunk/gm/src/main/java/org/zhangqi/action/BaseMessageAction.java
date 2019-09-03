package org.zhangqi.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.zhangqi.msg.IMessage;
import org.zhangqi.msg.NetMessage;
import org.zhangqi.network.session.ISession;

import com.google.protobuf.MessageLite.Builder;

@Controller
public abstract class BaseMessageAction {

	private static final Logger logger = LoggerFactory.getLogger(BaseMessageAction.class);

	protected abstract void LogRequest(IMessage requestMessage) throws Exception;

	protected abstract void LogResponse(IMessage responseMessage) throws Exception;

	protected abstract IMessage doAction(IMessage requestMessage) throws Exception;

	public void handleMessage(IMessage requestMessage) throws Exception {
		LogRequest(requestMessage);

		IMessage responseMessage = doAction(requestMessage);
		if (responseMessage instanceof NetMessage) {
			NetMessage netMessage = (NetMessage) responseMessage;
			netMessage.getSession().write(netMessage);
		} else {
			logger.error("handlerMessage error, unsupport msg type = {}", requestMessage.getClass().getName());
		}

		LogResponse(responseMessage);
	}

	protected IMessage buildResponseNetMsg(ISession session, int rpcName, Builder builder) {
		NetMessage message = new NetMessage(rpcName, builder);
		message.setSession(session);
		return message;
	}
}
