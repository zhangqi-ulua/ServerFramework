package org.zhangqi.action;

import org.springframework.stereotype.Controller;
import org.zhangqi.msg.IMessage;
import org.zhangqi.msg.NetMessage;

import com.google.protobuf.MessageLite.Builder;

@Controller
public abstract class BaseMessageAction {

	protected abstract void LogRequest(IMessage requestMessage) throws Exception;

	protected abstract void LogResponse(IMessage responseMessage) throws Exception;

	protected abstract IMessage doAction(IMessage requestMessage) throws Exception;

	public IMessage handleMessage(IMessage requestMessage) throws Exception {
		LogRequest(requestMessage);

		IMessage responseMessage = doAction(requestMessage);

		LogResponse(responseMessage);

		return responseMessage;
	}

	protected IMessage buildResponseNetMsg(int userId, int rpcName, Builder builder) {
		NetMessage message = new NetMessage(rpcName, builder);
		message.setUserId(userId);
		return message;
	}
}
