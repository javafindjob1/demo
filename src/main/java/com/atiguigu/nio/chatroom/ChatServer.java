package com.atiguigu.nio.chatroom;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class ChatServer {
  private Selector selector;

  public static void main(String[] args) throws IOException {
    new ChatServer().listen();
  }

  public ChatServer() throws IOException {
    selector = Selector.open();
    ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
    serverSocketChannel.socket().bind(new InetSocketAddress(6666));
    serverSocketChannel.configureBlocking(false);
    serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

  }

  private void listen() {
    while (true) {
      try {
        if (selector.select(2000) == 0) {
          continue;
        }
      } catch (Exception e) {
        e.printStackTrace();
        throw new RuntimeException("selector 轮询事件异常");
      }

      Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
      while (iterator.hasNext()) {
        SelectionKey key = iterator.next();
        if (key.isAcceptable()) {
          try {
            handleConnect(key);
          } catch (Exception e) {
            e.printStackTrace();
            System.out.println("处理新连接异常");
          }
        }

        if (key.isReadable()) {
          SocketChannel socketChannel = ((SocketChannel) key.channel());
          String msg = null;
          String clientName = null;
          try {
            clientName = socketChannel.getRemoteAddress().toString();
            msg = handleRead(key);
          } catch (Exception e) {
            e.printStackTrace();
            System.out.println(clientName + "客户端离线");
            key.cancel();
            try {
              socketChannel.close();
            } catch (Exception e2) {
            }
            msg = null;
          }

          if (msg != null) {
            sendOther(clientName, msg, socketChannel);
          }

        }

        iterator.remove();
      }
    }
  }

  private String handleRead(SelectionKey key) throws IOException {
    SocketChannel socketChannel = (SocketChannel) key.channel();
    ByteBuffer byteBuffer = (ByteBuffer) key.attachment();
    if (socketChannel.read(byteBuffer) > 0) {
      String msg = new String(byteBuffer.array());
      System.out.println("收到消息," + socketChannel.getRemoteAddress() + "->" + msg);
      byteBuffer.clear();
      return msg;
    }
    return null;
  }

  public void handleConnect(SelectionKey key) throws IOException {
    ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
    SocketChannel socketChannel = ssc.accept();
    socketChannel.configureBlocking(false);
    socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));
    System.out.println("新连接进来:" + socketChannel.getRemoteAddress() + ", 总数:" + selector.keys().size());
  }

  private void sendOther(String client, String msg, SocketChannel excludChannel) {
    Iterator<SelectionKey> iterator = selector.keys().iterator();
    while (iterator.hasNext()) {
      SelectionKey key = iterator.next();
      if (!key.isValid() || key.channel() instanceof ServerSocketChannel || key.channel() == excludChannel) {
        continue;
      }
      SocketChannel socketChannel = (SocketChannel) key.channel();
      String clientName = null;
      try {
        clientName = socketChannel.getRemoteAddress().toString();
        socketChannel.write(ByteBuffer.wrap((client+"->"+msg).getBytes()));
      } catch (Exception e) {
        e.printStackTrace();
        System.out.println("向" + clientName + "客户端转发消息失败");
      }
    }
  }
}
