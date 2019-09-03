package org.zhangqi.actor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zhangqi.annotation.MessageMethodMapping;
import org.zhangqi.manager.OnlineClientManager;
import org.zhangqi.msg.IMessage;
import org.zhangqi.msg.NetMessage;
import org.zhangqi.proto.Rpc.RpcErrorCodeEnum;

import akka.actor.ActorRef;

public class UserManagerActor extends BaseMessageActor {

	private static final Logger logger = LoggerFactory.getLogger(UserManagerActor.class);

	public UserManagerActor() {
		super();
	}

	public UserManagerActor(String actionPackageName) {
		super(actionPackageName);
	}

	// 玩家相关的请求，都转到UserManagerActor，然后通过它转发到玩家对应的UserActor中处理
	@MessageMethodMapping(value = {}, isNet = true)
	public void proxyNetMessageInvoke(IMessage message) throws Exception {
		NetMessage netMessage = (NetMessage) message;
		int sessionId = netMessage.getSessionId();
		ActorRef userActor = OnlineClientManager.getInstance().getUserActor(sessionId);
		if (userActor == null) {
			logger.error("proxyNetMessageInvoke error, can't find userActor for userId = {}", netMessage.getUserId());
			NetMessage errorNetMsg = new NetMessage(message.getRpcNum(), RpcErrorCodeEnum.ServerError_VALUE);
			sender().tell(errorNetMsg, ActorRef.noSender());
		} else {
			userActor.tell(message, sender());
		}
	}
}
