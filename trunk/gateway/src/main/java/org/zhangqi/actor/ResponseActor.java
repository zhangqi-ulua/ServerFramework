package org.zhangqi.actor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zhangqi.msg.IMessage;
import org.zhangqi.msg.NetMessage;
import org.zhangqi.msg.NetResponseMessage;
import org.zhangqi.proto.Rpc.RpcNameEnum;

import akka.actor.UntypedAbstractActor;
import io.netty.channel.Channel;

public class ResponseActor extends UntypedAbstractActor {

	private static final Logger logger = LoggerFactory.getLogger(ResponseActor.class);

	private Channel channel;

	public ResponseActor(Channel channel) {
		this.channel = channel;
	}

	@Override
	public void onReceive(Object arg0) throws Throwable {
		if (arg0 instanceof NetMessage) {
			NetMessage msg = (NetMessage) arg0;
			switch (msg.getRpcNum()) {
			case RpcNameEnum.Login_VALUE: {
				// 远程逻辑服务器返回玩家登录成功后，不仅需要返回给玩家登录成功
				// 也需要通知自己的父Actor即ChannelActor为这个玩家的ChannelActor绑定userId
				context().parent().tell(new NetResponseMessage(msg), self());
				break;
			}
			case RpcNameEnum.ForceOfflinePush_VALUE: {
				write(msg);
				channel.close();
			}
			default: {
				write(msg);
				break;
			}
			}
		} else {
			logger.error("onReceive error, unsupport msg type = {}", arg0.getClass().getName());
		}
	}

	private void write(IMessage msg) {
		if (this.channel != null && this.channel.isActive() && this.channel.isWritable()) {
			channel.writeAndFlush(msg.toBinaryWebSocketFrame());
		}
	}
}
