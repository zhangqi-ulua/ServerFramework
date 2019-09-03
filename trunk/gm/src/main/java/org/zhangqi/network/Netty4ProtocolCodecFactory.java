package org.zhangqi.network;

import org.zhangqi.manager.ConfigManager;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

public class Netty4ProtocolCodecFactory extends ChannelInitializer<Channel> {

	@Override
	protected void initChannel(Channel channel) throws Exception {
		ChannelPipeline pipeline = channel.pipeline();

		// 心跳处理
		pipeline.addLast(new IdleStateHandler(ConfigManager.WEBSOCKET_READER_IDLE_TIME,
				ConfigManager.WEBSOCKET_WRITER_IDLE_TIME, ConfigManager.WEBSOCKET_ALL_IDLE_TIME));
		// 将请求和应答消息编码或解码为HTTP消息
		pipeline.addLast(new HttpServerCodec());
		// 将HTTP消息的多个部分组合成一条完整的HTTP消息
		pipeline.addLast(new HttpObjectAggregator(65536));
		pipeline.addLast(new ChunkedWriteHandler());
		pipeline.addLast(new WebSocketServerCompressionHandler());
		pipeline.addLast(new WebSocketServerProtocolHandler(ConfigManager.WEBSOCKET_PATH, null, true));
		pipeline.addLast(new WebSocketFrameHandler());
	}
}
