package com.chess;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.util.concurrent.EventExecutorGroup;

import java.net.InetSocketAddress;

/**
 * 代码清单 12-7 向 ChatServer 添加加密
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
// SecureChatServer 扩展 ChatServer 以支持加密
public class SecureChatServer extends ChatServer {
    private final SslContext context;

    public SecureChatServer(SslContext context) {
        this.context = context;
    }

    // 这个重写是真骚
    @Override
    protected ChannelInitializer<Channel> createInitializer(
            ChannelGroup group, EventExecutorGroup businessGroup) {
        // 返回之前创建的 SecureChatServerInitializer 以启用加密
        return new SecureChatServerInitializer(group, context, businessGroup);
    }

    public static void main(String[] args) throws Exception {
        // args = new String[]{"8080"};
        if (args.length != 1) {
            System.err.println("Please give port as argument");
            System.exit(1);
        }
        int port = Integer.parseInt(args[0]);
        SelfSignedCertificate cert = new SelfSignedCertificate();
        SslContext context = SslContextBuilder.forServer(cert.certificate(), cert.privateKey())
                .build();
        final SecureChatServer endpoint = new SecureChatServer(context);
        ChannelFuture future = endpoint.start(new InetSocketAddress(port));
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                endpoint.destroy();
            }
        });
        future.channel().closeFuture().syncUninterruptibly();
    }
}
