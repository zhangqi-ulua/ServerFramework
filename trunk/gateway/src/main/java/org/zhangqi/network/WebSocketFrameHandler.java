package org.zhangqi.network;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zhangqi.actor.ChannelActor;
import org.zhangqi.manager.MessageManager;
import org.zhangqi.msg.IMessage;
import org.zhangqi.msg.NetMessage;
import org.zhangqi.proto.Common.ForceOfflinePush;
import org.zhangqi.proto.Common.ForceOfflineReasonEnum;
import org.zhangqi.proto.Rpc.RpcNameEnum;

import akka.actor.ActorRef;
import akka.actor.PoisonPill;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.timeout.IdleStateEvent;

public class WebSocketFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

	private static final Logger logger = LoggerFactory.getLogger(WebSocketFrameHandler.class);

	private ByteBuf tempByteBuf;

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
		try {
			if (frame instanceof TextWebSocketFrame) {
				ctx.close();
			} else if (frame instanceof PingWebSocketFrame) {
				ctx.channel().write(new PongWebSocketFrame(frame.content()));
			} else if (frame instanceof BinaryWebSocketFrame) {
				ByteBuf in = frame.content();
				if (frame.isFinalFragment() == false) {
					if (tempByteBuf == null) {
						tempByteBuf = ctx.alloc().heapBuffer();
					}
					tempByteBuf.writeBytes(in);
				} else {
					handleMessage(in, ctx.channel());
				}
			} else if (frame instanceof ContinuationWebSocketFrame) {
				tempByteBuf.writeBytes(frame.content());
				if (frame.isFinalFragment() == true) {
					handleMessage(tempByteBuf, ctx.channel());
					tempByteBuf.clear();
				}
			} else {
				logger.error("channelRead0 error, unsupport webSocketFrame type = {}", frame.getClass().getName());
				ctx.close();
			}
		} catch (Exception e) {
			InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
			logger.error("channelRead0 error, ip = {}, exception = ", insocket.getAddress().getHostAddress(), e);
		}
	}

	public void handleMessage(ByteBuf byteBuf, Channel channel) {
		if (MessageManager.getInstance().isAvailableForClient() == false) {
			ForceOfflinePush.Builder builder = ForceOfflinePush.newBuilder();
			builder.setForceOfflineReason(ForceOfflineReasonEnum.ForceOfflineServerNotAvailable);
			NetMessage message = new NetMessage(RpcNameEnum.ForceOfflinePush_VALUE, builder);
			write(message, channel);
			channel.close();
		} else {
			ActorRef actor = ChannelActor.attachChannelActor(channel);
			int totalLength = byteBuf.readInt();
			int rpcNum = byteBuf.readInt();
			// errorCode
			byteBuf.readInt();
			byte[] bytes = new byte[totalLength - 12];
			byteBuf.readBytes(bytes);
			NetMessage message = new NetMessage(rpcNum, bytes);
			actor.tell(message, ActorRef.noSender());
		}
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		if (MessageManager.getInstance().isAvailableForClient() == false) {
			ForceOfflinePush.Builder builder = ForceOfflinePush.newBuilder();
			builder.setForceOfflineReason(ForceOfflineReasonEnum.ForceOfflineServerNotAvailable);
			NetMessage message = new NetMessage(RpcNameEnum.ForceOfflinePush_VALUE, builder);
			Channel channel = ctx.channel();
			write(message, channel);
			ctx.close();
		}
	}

	// 当channel失效时（比如客户端断线或者服务器主动调用ctx.close），关闭channel对应的channelActor
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		ActorRef actor = ChannelActor.getChannelActor(ctx.channel());
		if (actor != null) {
			actor.tell(PoisonPill.getInstance(), ActorRef.noSender());
		}
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			ctx.close();
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable throwable) throws Exception {
		ctx.close();

		if (throwable.getMessage().startsWith("远程主机强迫关闭了一个现有的连接") == false) {
			InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
			logger.error("exceptionCaught, ip = {}, exception = ", insocket.getAddress().getHostAddress(), throwable);
		}
	}

	private void write(IMessage message, Channel channel) {
		if (channel != null && channel.isActive() && channel.isWritable()) {
			channel.writeAndFlush(message.toBinaryWebSocketFrame());
		}
	}
}
