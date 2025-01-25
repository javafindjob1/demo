package com.chess;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.CookieHeaderNames;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;

import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedNioFile;
import io.netty.util.CharsetUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.chess.busi.User;

/**
 * 代码清单 12-1 HTTPRequestHandler
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
// 扩展 SimpleChannelInboundHandler 以处理 FullHttpRequest 消息
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    // 规定协议升级的uri
    private final String wsUri;
    private final Map<String, User> userMap;
    private static final File INDEX;

    static {

        URL location = HttpRequestHandler.class.getProtectionDomain().getCodeSource().getLocation();
        System.out.println(location);

        String resourcePath = HttpRequestHandler.class.getClassLoader().getResource("index.html").getFile();
        System.out.println("index.html:"+resourcePath);
        try {

            InputStream inputStream = HttpRequestHandler.class.getClassLoader().getResourceAsStream("index.html");
            if (inputStream == null) {
                System.out.println("Resource not found!");
            }
            
            FileOutputStream outputStream = new FileOutputStream("index.html");
            byte[] temp = new byte[1024];
            int length ;
            while((length=inputStream.read(temp)) > 0){
                outputStream.write(temp, 0, length);
            }
            outputStream.close();
            inputStream.close();
                
            resourcePath = !resourcePath.contains("file:") ? resourcePath : resourcePath.substring(5);
            String path = location.toURI() + "index.html";
            path = !path.contains("file:") ? path : path.substring(5);
            System.out.println("index.html(1path):"+path);
            INDEX = new File("index.html");
            System.out.println("length:"+INDEX.length());
        } catch (Exception e) {
            System.out.println(e);
            throw new IllegalStateException("Unable to locate index.html", e);
        }
    }

    public HttpRequestHandler(String wsUri, Map<String, User> userMap) {
        this.wsUri = wsUri;
        this.userMap = userMap;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx,
            FullHttpRequest request) throws Exception {
        // (1) 如果请求了 WebSocket 协议升级，则增加引用计数（调用 retain()方法），并将它传递给下一 个
        // ChannelInboundHandler
        URL url = new URL("http://localhost" + request.uri());
        if (wsUri.equalsIgnoreCase(request.getUri())) {
            System.out.println("ws 连接请求 channelId " + ctx.channel().id());
            HttpHeaders headers = request.headers();
            // 获取 Cookie 字符串
            String cookieHeader = headers.get("Cookie");

            if (cookieHeader != null) {
                // 解析 Cookie 字符串
                Set<Cookie> cookies = ServerCookieDecoder.STRICT.decode(cookieHeader);

                User u = new User();
                // 遍历并处理每个 Cookie
                for (Cookie cookie : cookies) {
                    System.out.println("Cookie name: " + cookie.name() + " value: " + cookie.value());
                    // 其他操作...
                    if ("auth_token".equals(cookie.name())) {
                        System.out.println("Valid token: " + cookie.value());
                        u.setId(cookie.value());
                    } else if ("username".equals(cookie.name())) {
                        System.out.println("username: " + cookie.value());
                        u.setName(URLDecoder.decode(cookie.value(), "utf-8"));
                    }
                }
                if (u.getId() != null) {
                    User user = userMap.get(u.getId());
                    String cid = ctx.channel().id().toString();
                    if (user == null) {
                        user = new User();
                        user.setId(u.getId());
                        user.setName(u.getName());
                        userMap.put(user.getId(), user);
                    }else{
                        userMap.remove(user.getWsChannelId());
                    }
                    userMap.put(cid, user);
                    user.setWsChannelId(cid);
                }

            } else {
                System.out.println("No cookies found in the request.");
            }
            // 将读取到的消息（或数据）传递给 ChannelPipeline 中的下一个 ChannelHandler 进行处理。
            ctx.fireChannelRead(request.retain());
        } else if ("/login".equalsIgnoreCase(url.getPath())) {
            // 获取请求 URI
            String uri = request.uri();
            // 使用 QueryStringDecoder 解析查询参数
            QueryStringDecoder decoder = new QueryStringDecoder(uri);
            Map<String, List<String>> parameters = decoder.parameters();
            List<String> list = parameters.get("username");
            if (list == null || list.size() == 0 || list.get(0).trim().length()==0) {
                Map<String, String> result = new HashMap<>();
                result.put("retmsg", "Parameter: username is null");
                System.out.println(result);

                HttpHeaders headers = request.headers();
                String cookieHeader = headers.get("Cookie");
                if (cookieHeader != null) {
                    Set<Cookie> cookies = ServerCookieDecoder.STRICT.decode(cookieHeader);
                    for (Cookie cookie : cookies) {
                        if ("auth_token".equals(cookie.name())) {
                            System.out.println("Valid token: " + cookie.value());
                        } else if ("username".equals(cookie.name())) {
                            System.out.println("username: " + URLDecoder.decode(cookie.value(), "utf-8"));
                        }
                    }
                }
                // 处理GET请求的逻辑
                ByteBuf content = Unpooled.copiedBuffer(JSON.toJSONString(result), CharsetUtil.UTF_8);
                DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                        HttpResponseStatus.BAD_REQUEST);
                response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=UTF-8");
                response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());

                response.content().writeBytes(content);
                ChannelFuture future = ctx.writeAndFlush(response);
                boolean keepAlive = HttpUtil.isKeepAlive(request);
                // (6) 如果没有请求keep-alive，则在写操作完成后关闭 Channel
                if (!keepAlive) {
                    future.addListener(ChannelFutureListener.CLOSE);
                }
            } else {
                Map<String, String> result = new HashMap<>();
                result.put("retmsg", "Parameter: username " + list.get(0));
                System.out.println(result);
                String username = list.get(0).trim();
                username = username.length() > 20 ? username.substring(0, 20) : username;

                String token = ctx.channel().id().toString();

                int expire = 60 * 60 * 24 * 7;
                // 设置 Cookie
                DefaultCookie cookie = new DefaultCookie("auth_token", token);
                cookie.setHttpOnly(true);
                cookie.setSecure(true);
                cookie.setSameSite(CookieHeaderNames.SameSite.None);
                cookie.setPath("/");
                cookie.setMaxAge(expire); // 1 hour

                DefaultCookie cookieName = new DefaultCookie("username", URLEncoder.encode(username, "utf-8"));
                cookieName.setHttpOnly(true);
                cookieName.setSecure(true);
                cookieName.setSameSite(CookieHeaderNames.SameSite.None);
                cookieName.setPath("/");
                cookieName.setMaxAge(expire); // 1 hour

                // 创建响应
                ByteBuf content = Unpooled.copiedBuffer(JSON.toJSONString(result), CharsetUtil.UTF_8);
                FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
                response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=UTF-8");
                response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());
                response.headers().set(HttpHeaderNames.SET_COOKIE,
                        ServerCookieEncoder.STRICT.encode(cookie, cookieName));
                response.content().writeBytes(content);

                // 发送响应
                ChannelFuture future = ctx.writeAndFlush(response);
                future.addListener(ChannelFutureListener.CLOSE);
            }

        } else if ("/chess".equalsIgnoreCase(url.getPath())) {

            boolean keepAlive = HttpUtil.isKeepAlive(request);
            // 获取GET请求参数
            String decodeComponent = QueryStringDecoder.decodeComponent(request.uri());
            System.out.println("GET Parameters: " + decodeComponent);

            // 处理GET请求的逻辑
            String jsonResponse = "Hello, this is a HTTP response!";
            ByteBuf content = Unpooled.copiedBuffer(jsonResponse, CharsetUtil.UTF_8);

            DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=UTF-8");
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());

            response.content().writeBytes(content);
            ChannelFuture future = ctx.writeAndFlush(response);
            // (6) 如果没有请求keep-alive，则在写操作完成后关闭 Channel
            if (!keepAlive) {
                future.addListener(ChannelFutureListener.CLOSE);
            }
        } else {
            System.out.println(" index 连接请求channelId " + ctx.channel().id());
            // (2) 处理 100 Continue 请求以符合 HTTP 1.1 规范
            if (HttpUtil.is100ContinueExpected(request)) {
                send100Continue(ctx);
            }

            // 读取 index.html
            try (RandomAccessFile file = new RandomAccessFile(INDEX, "r")) {

                HttpResponse response = new DefaultHttpResponse(request.protocolVersion(), HttpResponseStatus.OK);
                response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
                boolean keepAlive = HttpUtil.isKeepAlive(request);
                // 如果请求了keep-alive，则添加所需要的 HTTP 头信息
                if (keepAlive) {
                    response.headers().set(HttpHeaderNames.CONTENT_LENGTH, file.length());
                    response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                }
                // (3) 将 HttpResponse 写到客户端
                ctx.write(response);
                // (4) 将 index.html 写到客户端
                if (ctx.pipeline().get(SslHandler.class) == null) {
                    ctx.write(new DefaultFileRegion(file.getChannel(), 0, file.length()));
                } else {
                    ctx.write(new ChunkedNioFile(file.getChannel()));
                }
                // (5) 写 LastHttpContent 并冲刷至客户端
                ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
                // (6) 如果没有请求keep-alive，则在写操作完成后关闭 Channel
                if (!keepAlive) {
                    future.addListener(ChannelFutureListener.CLOSE);
                }
            }
        }
    }

    private void handleCors(ChannelHandlerContext ctx, FullHttpRequest request) {
        // 允许所有来源的请求
        String origin = request.headers().get(HttpHeaderNames.ORIGIN);
        if (origin != null) {
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
            response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
            response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS, "GET, POST, PUT, DELETE, OPTIONS");
            response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS, "Content-Type, Authorization");
            response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
            response.headers().set(HttpHeaderNames.ACCESS_CONTROL_MAX_AGE, 86400); // 1 day

            if (HttpMethod.OPTIONS.equals(request.method())) {
                // 预检请求
                ctx.writeAndFlush(response);
                ctx.close();
            }
        }
    }

    private static void send100Continue(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
        ctx.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}
