package com.chess;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.chess.busi.DeskService;
import com.chess.busi.DeskServiceImpl;
import com.chess.busi.User;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.concurrent.EventExecutorGroup;

/**
 * 代码清单 12-3 初始化 ChannelPipeline
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
// 扩展了 ChannelInitializer
public class ChatServerInitializer extends ChannelInitializer<Channel> {
    private final ChannelGroup group;
    private final EventExecutorGroup businessGroup;

    private final TextWebSocketFrameHandler textWebSocketFrameHandler;
    private final CloseWebSocketFrameHandler closeWebSocketFrameHandler;

    private final Map<String, User> userMap;
    private final DeskService deskService;

    public ChatServerInitializer(ChannelGroup group, EventExecutorGroup businessGroup) {
        this.group = group;
        this.businessGroup = businessGroup;
        this.userMap = new ConcurrentHashMap<>();
        this.deskService = new DeskServiceImpl(group, businessGroup, userMap);

        this.textWebSocketFrameHandler = new TextWebSocketFrameHandler(group, userMap, deskService);
        this.closeWebSocketFrameHandler = new CloseWebSocketFrameHandler(group, userMap, deskService);
    }

    @Override
    // 将所有需要的 ChannelHandler 添加到 ChannelPipeline 中
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new ChunkedWriteHandler());
        pipeline.addLast(new HttpObjectAggregator(10 * 1024 * 1024));
        pipeline.addLast(new HttpRequestHandler("/ws", userMap));
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws", null, true, 10 * 1024 * 1024));
        pipeline.addLast(textWebSocketFrameHandler);
        pipeline.addLast(closeWebSocketFrameHandler);
    }
}
