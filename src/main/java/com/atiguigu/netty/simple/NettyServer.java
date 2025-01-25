package com.atiguigu.netty.simple;

import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.CharsetUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class NettyServer {

    public static void main(String[] args) throws Exception {
        // 创建 BossGroup 和 WorkerGroup
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            // 创建 ServerBootstrap 实例
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
             .childHandler(new ChannelInitializer<SocketChannel>() {

                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            System.out.println("server channelRead..." + msg.getClass());
                            System.out.println("server ctx = " + ctx);
                            ByteBuf buf = (ByteBuf)msg;
                            System.out.println("server msg = " +  buf.toString(CharsetUtil.UTF_8));
                            // taskQueue
                            ctx.channel().eventLoop().execute(new Runnable() {
                                private Channel channel = ctx.channel();
                                @Override
                                public void run() {
                                    System.out.println("taskQueue");

                                    sendMessage("异步消息推送");
                                }

                                public void sendMessage(String message) {
                                    // 确保在 EventLoop 线程中执行发送操作
                                    if (channel.isActive() && channel.isOpen()) {
                                        channel.eventLoop().execute(() -> {
                                            channel.writeAndFlush(Unpooled.copiedBuffer(message, CharsetUtil.UTF_8));
                                        });
                                    }
                                }
                            
                                public void closeChannel() {
                                    // 确保在 EventLoop 线程中执行关闭操作
                                    if (channel.isActive() && channel.isOpen()) {
                                        channel.eventLoop().execute(() -> {
                                            channel.close();
                                        });
                                    }
                                }
                            });
                            // shedulerQueue
                            ctx.channel().eventLoop().schedule(()->{
                                System.out.println("shedulerQueue");
                            },  5, TimeUnit.SECONDS);
                        }

                        @Override
                        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
                            System.out.println("server channelReadComplete...");
                            ctx.channel().pipeline().writeAndFlush(Unpooled.copiedBuffer("hello client", CharsetUtil.UTF_8));
                        }

                    });
                }
             });

            // 绑定端口并同步等待成功
            ChannelFuture f = b.bind(6666).sync();

            // 等待服务端监听端口关闭
            f.channel().closeFuture().sync();
        } finally {
            // 优雅关闭 EventLoopGroup
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}