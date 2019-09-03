package org.zhangqi.actor;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zhangqi.manager.MessageManager;
import org.zhangqi.manager.OnlineClientManager;
import org.zhangqi.manager.SpringManager;
import org.zhangqi.msg.IMessage;
import org.zhangqi.msg.NetMessage;
import org.zhangqi.msg.NetResponseMessage;
import org.zhangqi.proto.Rpc.RpcErrorCodeEnum;
import org.zhangqi.proto.Rpc.RpcNameEnum;
import org.zhangqi.service.LoadBalanceService;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedAbstractActor;
import io.netty.channel.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

public class ChannelActor extends UntypedAbstractActor {

	private static final Logger logger = LoggerFactory.getLogger(ChannelActor.class);

	private static final AttributeKey<ActorRef> NETTY_CHANNEL_TO_ACTOR_KEY = AttributeKey
			.valueOf("nettyChannelToActorKey");
	private static final AttributeKey<Integer> NETTY_CHANNEL_TO_SESSION_ID_KEY = AttributeKey
			.valueOf("nettyChannelToSessionIdKey");

	private Channel channel;
	private int userId;
	private String userIp;

	private ActorRef responseActor;

	private static LoadBalanceService loadBalanceService = SpringManager.getInstance()
			.getBean(LoadBalanceService.class);

	public ChannelActor(Channel channel) {
		this.channel = channel;
		InetSocketAddress insocket = (InetSocketAddress) channel.remoteAddress();
		userIp = insocket.getAddress().getHostAddress();
	}

	public static ActorRef attachChannelActor(Channel channel) {
		Attribute<ActorRef> actorAttr = channel.attr(NETTY_CHANNEL_TO_ACTOR_KEY);
		ActorRef actor = actorAttr.get();
		if (actor == null) {
			int sessionId = loadBalanceService.addAndGetNextAvailableSessionId();
			Attribute<Integer> sessionIdAttr = channel.attr(NETTY_CHANNEL_TO_SESSION_ID_KEY);
			sessionIdAttr.setIfAbsent(sessionId);

			ActorRef newActor = MessageManager.getInstance().createChannelActor(channel);
			// 注意：只有当attr中已经存在对应值时直接返回值，否则返回null。所以后面需要判断actor是否为空并赋值
			actor = actorAttr.setIfAbsent(newActor);
			if (actor == null) {
				actor = newActor;
			}

			OnlineClientManager.getInstance().addSession(sessionId, channel);
		}
		return actor;
	}

	public static ActorRef getChannelActor(Channel channel) {
		Attribute<ActorRef> actorAttr = channel.attr(NETTY_CHANNEL_TO_ACTOR_KEY);
		return actorAttr.get();
	}

	public static int getSessionId(Channel channel) {
		Attribute<Integer> sessionIdAttr = channel.attr(NETTY_CHANNEL_TO_SESSION_ID_KEY);
		return sessionIdAttr.get();
	}

	@Override
	public void preStart() {
		// 每个ChannelActor都需要创建一个附属的ResponseActor用于接收远程服务器返回的消息
		// 因为客户端向gateway发送的NetMessage在这里处理，而远程服务器收到gateway转发的NetMessage，处理后也是以NetMessage形式回复的
		// 如果都在ChannelActor中处理，因为都是NetMessage就无法区分
		responseActor = context().actorOf(Props.create(ResponseActor.class, channel));
	}

	@Override
	public void postStop() {
		int sessionId = getSessionId(channel);
		OnlineClientManager.getInstance().removeSession(sessionId);
	}

	@Override
	public void onReceive(Object arg0) throws Throwable {
		if (arg0 instanceof NetMessage) {
			NetMessage msg = (NetMessage) arg0;
			int sessionId = getSessionId(channel);
			msg.setUserId(userId);
			msg.setSessionId(sessionId);

			switch (msg.getRpcNum()) {
			case RpcNameEnum.Regist_VALUE: {
				if (msg.getUserId() > 0) {
					logger.error("onReceive error, user has login, but request regist, userId = {}", msg.getUserId());
					sendErrorToClient(msg, RpcErrorCodeEnum.ServerError_VALUE);
					break;
				}
				msg.setUserIp(userIp);
				// 注册需要到主逻辑服务器
				if (MessageManager.getInstance().sendNetMsgToMainLogicServer(msg, responseActor) == false) {
					sendErrorToClient(msg, RpcErrorCodeEnum.ServerNotAvailable_VALUE);
				}
				break;
			}
			case RpcNameEnum.Login_VALUE: {
				if (msg.getUserId() > 0) {
					logger.error("onReceive error, user has login, but request login, userId = {}", msg.getUserId());
					sendErrorToClient(msg, RpcErrorCodeEnum.ServerError_VALUE);
					break;
				}
				msg.setUserIp(userIp);
				if (MessageManager.getInstance().sendNetMsgToLogicServer(msg, responseActor) == false) {
					sendErrorToClient(msg, RpcErrorCodeEnum.ServerNotAvailable_VALUE);
				}
				break;
			}
			case RpcNameEnum.Match_VALUE:
			case RpcNameEnum.CancelMatch_VALUE: {
				if (userId > 0) {
					if (MessageManager.getInstance().sendNetMsgToMainLogicServer(msg, responseActor) == false) {
						sendErrorToClient(msg, RpcErrorCodeEnum.ServerNotAvailable_VALUE);
					}
				} else {
					channel.close();
				}
				break;
			}
			// 转发到battleServer的请求
			case RpcNameEnum.GetBattleInfo_VALUE:
			case RpcNameEnum.Concede_VALUE:
			case RpcNameEnum.PlacePieces_VALUE:
			case RpcNameEnum.ForceEndTurn_VALUE:
			case RpcNameEnum.ReadyToStartGame_VALUE:
			case RpcNameEnum.ForceReadyToStartGame_VALUE: {
				if (userId > 0) {
					String battleId = loadBalanceService.getBattleUserIdToBattleId(userId);
					if (battleId != null) {
						if (MessageManager.getInstance().sendNetMsgToBattleServer(msg, responseActor) == false) {
							sendErrorToClient(msg, RpcErrorCodeEnum.ServerNotAvailable_VALUE);
						}
					} else {
						sendErrorToClient(msg, RpcErrorCodeEnum.UserNotInBattle_VALUE);
					}
				} else {
					channel.close();
				}
				break;
			}
			default: {
				if (userId > 0) {
					if (MessageManager.getInstance().sendNetMsgToLogicServer(msg, responseActor) == false) {
						sendErrorToClient(msg, RpcErrorCodeEnum.ServerNotAvailable_VALUE);
					}
				} else {
					channel.close();
				}
				break;
			}
			}
		} else if (arg0 instanceof NetResponseMessage) {
			NetResponseMessage netResponseMessage = (NetResponseMessage) arg0;
			NetMessage msg = netResponseMessage.getNetMessage();
			int errorCode = msg.getErrorCode();
			switch (msg.getRpcNum()) {
			case RpcNameEnum.Login_VALUE: {
				if (errorCode == RpcErrorCodeEnum.Ok_VALUE) {
					userId = msg.getUserId();
				}
				write(msg);
				break;
			}
			default: {
				logger.error("onReceive netResponseMessage error, unsupprt netResponseMessage type = {}",
						arg0.getClass().getName());
				break;
			}
			}
		} else {
			logger.error("onReceive error, unsupprt message type = {}", arg0.getClass().getName());
		}
	}

	private void sendErrorToClient(NetMessage netMessage, int errorCode) {
		NetMessage resp = new NetMessage(netMessage.getRpcNum(), errorCode);
		sender().tell(resp, ActorRef.noSender());
	}

	private void write(IMessage msg) {
		if (this.channel != null && this.channel.isActive() && this.channel.isWritable()) {
			channel.writeAndFlush(msg.toBinaryWebSocketFrame());
		}
	}
}
