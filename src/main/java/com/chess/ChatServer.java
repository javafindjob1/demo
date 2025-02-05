package com.chess;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.ImmediateEventExecutor;

import java.net.InetSocketAddress;

/**
 * 代码清单 12-4 引导服务器
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
public class ChatServer {
    // 创建 DefaultChannelGroup，其将保存所有已经连接的 WebSocket Channel
    private final ChannelGroup channelGroup = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
    private final EventLoopGroup group = new NioEventLoopGroup();
    private final EventExecutorGroup businessGroup = new DefaultEventExecutorGroup(10); // 创建业务线程池

    private Channel channel;

    public ChannelFuture start(InetSocketAddress address) {
        // 引导服务器
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(group)
                .channel(NioServerSocketChannel.class)
                .childHandler(createInitializer(channelGroup, businessGroup))
                .childOption(ChannelOption.SO_SNDBUF, 256 * 1024)
                .childOption(ChannelOption.SO_RCVBUF, 256 * 1024);
        ChannelFuture future = bootstrap.bind(address);
        future.syncUninterruptibly();
        channel = future.channel();
        return future;
    }

    // 创建 ChatServerInitializer
    protected ChannelInitializer<Channel> createInitializer(ChannelGroup group, EventExecutorGroup businessGroup) {
        return new ChatServerInitializer(group, businessGroup);
    }

    // 处理服务器关闭，并释放所有的资源
    public void destroy() {
        if (channel != null) {
            channel.close();
        }
        channelGroup.close();
        group.shutdownGracefully();
        businessGroup.shutdownGracefully();
    }

    public static void main(String[] args) throws Exception {
        System.out.println(111111111);
        if (args == null || args.length == 0)
            args = new String[] { "8888" };
        if (args.length != 1) {
            System.err.println("Please give port as argument");
            System.exit(1);
        }
        int port = Integer.parseInt(args[0]);
        final ChatServer endpoint = new ChatServer();
        ChannelFuture future = endpoint.start(
                new InetSocketAddress(port));
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                endpoint.destroy();
            }
        });
        future.channel().closeFuture().syncUninterruptibly();
    }
}
