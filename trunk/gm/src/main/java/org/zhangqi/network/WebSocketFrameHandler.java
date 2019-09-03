package org.zhangqi.network;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zhangqi.constants.GmSessionDataKeyConstant;
import org.zhangqi.manager.MessageManager;
import org.zhangqi.manager.OnlineClientManager;
import org.zhangqi.msg.NetMessage;
import org.zhangqi.network.session.ISession;
import org.zhangqi.network.session.Netty4Session;
import org.zhangqi.proto.Gm.GmRpcNameEnum;

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
		int totalLength = byteBuf.readInt();
		int rpcNum = byteBuf.readInt();
		// errorCode
		byteBuf.readInt();
		byte[] bytes = new byte[totalLength - 12];
		byteBuf.readBytes(bytes);
		NetMessage message = new NetMessage(rpcNum, bytes);
		ISession session = Netty4Session.getSession(channel);
		message.setSession(session);

		switch (rpcNum) {
		case GmRpcNameEnum.GmRpcLogin_VALUE: {
			InetSocketAddress insocket = (InetSocketAddress) channel.remoteAddress();
			String userIp = insocket.getAddress().getHostAddress();
			message.setUserIp(userIp);
			break;
		}
		default: {
			if (session.hasData(GmSessionDataKeyConstant.GM_USER_ID_KEY) == false) {
				session.close();
				return;
			}
			break;
		}
		}
		MessageManager.getInstance().handleGmRequest(message);
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		ISession session = Netty4Session.getSession(ctx.channel());
		if (session.getData(GmSessionDataKeyConstant.GM_USER_ID_KEY) != null) {
			OnlineClientManager.getInstance().removeOnlineGmUser(session);
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
}
