package com.chess;

import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.chess.busi.ChessReq;
import com.chess.busi.DeskService;
import com.chess.busi.DeskServiceImpl;
import com.chess.busi.User;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

/**
 * 代码清单 12-2 处理文本帧
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
// 扩展 SimpleChannelInboundHandler，并处理 TextWebSocketFrame 消息
@Sharable
public class TextWebSocketFrameHandler
        extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private final ChannelGroup group;
    private DeskService deskService;

    public TextWebSocketFrameHandler(ChannelGroup group, Map<String, User> userMap, DeskService deskService) {
        System.out.println("TextWebSocketFrameHandler" + this);
        this.group = group;
        this.deskService = deskService;
    }

    // 重写 userEventTriggered()方法以处理自定义事件
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx,
            Object evt) throws Exception {
        // 如果该事件表示握手成功，则从该 ChannelPipeline 中移除HttpRequest-Handler，因为将不会接收到任何HTTP消息了
        if (evt == WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE) {
            ctx.pipeline().remove(HttpRequestHandler.class);
            // (1) 通知所有已经连接的 WebSocket 客户端新的客户端已经连接上了
            group.writeAndFlush(new TextWebSocketFrame(
                    "Client " + ctx.channel() + " joined"));
            // (2) 将新的 WebSocket Channel 添加到 ChannelGroup 中，以便它可以接收到所有的消息

            // (3) 返回自己的标识
            deskService.getDeskList(null, ctx);
            group.add(ctx.channel());
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 处理通道关闭事件
        System.out.println("WebSocket channel inactive");
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("WebSocket channel exceptionCaught");
        // 处理异常事件
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx,
            TextWebSocketFrame msg) throws Exception {
        // (3) 增加消息的引用计数，并将它写到 ChannelGroup 中所有已经连接的客户端
        // xx.retain() 增加xx的引用计数
        // msg.retain() 增加msg的引用计数

        // 读取原始文本数据
        String originalText = msg.text();
        try {
            int i = originalText.indexOf("\"messageType\":\"image\"");
            ChessReq req;
            if (i > -1) {
                int no = originalText.indexOf("}") + 1;
                String or = originalText.substring(0, no);
                req = JSON.parseObject(or, ChessReq.class);
                String or2 = originalText.substring(no);
                req.setMessage(or2);
            } else {
                req = JSON.parseObject(originalText, ChessReq.class);
            }
            switch (req.getType()) {
                case "addDesk":
                    deskService.addDesk(req, ctx);
                    break;
                case "pace":
                    deskService.pace(req, ctx);
                    break;
                case "getDeskList":
                    deskService.getDeskList(req, ctx);
                    break;
                case "restart":
                    deskService.restart(req, ctx);
                    break;
                case "sitDown":
                    deskService.sitDown(req, ctx);
                    break;
                case "message":
                    deskService.message(req, ctx);
                    break;
                case "regret":
                    deskService.regret(req, ctx);
                    break;
                default:
                    break;
            }

        } catch (JSONException e) {
            String tmp = originalText.length() > 100 ? originalText.substring(0, 100) : originalText;
            System.out.println("请求参数解析json失败：" + tmp + e.getMessage());
        } catch (Exception e) {
            String tmp = originalText.length() > 100 ? originalText.substring(0, 100) : originalText;
            System.out.println("运行异常：" + tmp);
            e.printStackTrace();
        }
    }
}
