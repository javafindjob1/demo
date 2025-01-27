package com.chess;

import java.util.Map;


import com.alibaba.fastjson.JSON;
import com.chess.busi.ChessResponse;
import com.chess.busi.Desk;
import com.chess.busi.DeskService;
import com.chess.busi.User;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

/**
 * 代码清单 12-2 处理文本帧
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
// 扩展 SimpleChannelInboundHandler，并处理 TextWebSocketFrame 消息
@Sharable
public class CloseWebSocketFrameHandler
        extends SimpleChannelInboundHandler<CloseWebSocketFrame> {
    private final ChannelGroup group;
    private DeskService deskService;
    private Map<String, User> userMap;

    public CloseWebSocketFrameHandler(ChannelGroup group, Map<String, User> userMap, DeskService deskService) {
        System.out.println("CloseWebSocketFrameHandler" + this);
        this.group = group;
        this.userMap = userMap;
        this.deskService = deskService;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 处理通道关闭事件
        System.out.println(" CloseWebSocketFrameHandler WebSocket channel inactive" + ctx.channel().id().toString());

        String cid = ctx.channel().id().toString();
        User user = userMap.get(cid);
        if (user != null) {
            user.offline();
            userMap.remove(cid);

            String deskId = user.getDeskId();
            if (deskId != null) {
                ChessResponse result = new ChessResponse();
                result.setRetCode("0000");
                result.setRetMsg("离线");

                Map<String, Desk> deskMap = deskService.getDeskMap();
                result.setData(deskMap.get(deskId));

                // 创建新的 TextWebSocketFrame 对象
                String resultStr = JSON.toJSONString(result);
                TextWebSocketFrame modifiedFrame = new TextWebSocketFrame(resultStr);
                group.writeAndFlush(modifiedFrame);
            }

        }

        super.channelInactive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CloseWebSocketFrame msg) throws Exception {
        System.out.println(" CloseWebSocketFrameHandler channelRead0");
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'channelRead0'");
    }

}
