package com.atiguigu.netty.http;

import java.net.URI;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

public class HttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
    if (msg instanceof HttpRequest) {
      System.out.println("msg = " + msg.getClass());
      System.out.println("channelRead0 = " + ctx.channel().remoteAddress());

      HttpRequest httpRequest = (HttpRequest) msg;
      URI uri = new URI(httpRequest.uri());
      if ("/favicon.ico".equals(uri.getPath())) {
        return;
      }
      ByteBuf content = Unpooled.copiedBuffer("我是服务器", CharsetUtil.UTF_8);

      FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);

      response.headers().set("content-type", "text/plain;charset=utf-8");
      response.headers().set("Content-Encoding", "utf8");
      response.headers().set("content-length", content.readableBytes());

      ctx.writeAndFlush(response);
    }
  }

}