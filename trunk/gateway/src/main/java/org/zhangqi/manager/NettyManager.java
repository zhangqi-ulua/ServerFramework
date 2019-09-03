package org.zhangqi.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zhangqi.network.Netty4ProtocolCodecFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyManager implements IManager {

	private static final Logger logger = LoggerFactory.getLogger(NettyManager.class);

	private static NettyManager instance = new NettyManager();

	public static NettyManager getInstance() {
		return instance;
	}

	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;

	@Override
	public void init() {
		bossGroup = new NioEventLoopGroup();
		workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup);
			b.channel(NioServerSocketChannel.class);
			b.option(ChannelOption.SO_REUSEADDR, true);
			b.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
			b.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
			b.childHandler(new Netty4ProtocolCodecFactory());

			ChannelFuture f = b.bind(ConfigManager.CLIENT_PORT).sync();
			logger.info("------------gateway start netty, watch client port = {}------------",
					ConfigManager.CLIENT_PORT);
			f.channel().closeFuture().sync();
		} catch (Exception e) {
			logger.error("start netty error = ", e);
			System.exit(-1);
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

	@Override
	public void shutdown() {
	}
}
