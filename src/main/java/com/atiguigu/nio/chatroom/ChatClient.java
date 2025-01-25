package com.atiguigu.nio.chatroom;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;

import io.netty.util.CharsetUtil;


public class ChatClient {

  private SocketChannel socketChannel;
  private Selector selector;

  public ChatClient() throws IOException {
    selector = Selector.open();

  }

  public static void main(String[] args) throws IOException {
    ChatClient chatClient = new ChatClient();
    chatClient.connect();
    chatClient.sendMsg("hello");
    System.in.read();
    System.out.println("over");
  }

  private void connect() throws IOException {
    socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 6666));
    socketChannel.configureBlocking(false);
    socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));

    new Thread() {
      @Override
      public void run() {
        listen();
      }
    }.start();
  }

  private void listen() {

    while (true) {
      try {

        if (selector.select(2000) == 0) {
          continue;
        }
      } catch (Exception e) {
        e.printStackTrace();
        System.out.println("消息接收器异常");
        throw new RuntimeException("");
      }

      Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
      while (iterator.hasNext()) {
        SelectionKey key = iterator.next();
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer byteBuffer = (ByteBuffer) key.attachment();
        try {
          if (key.isReadable()) {
            channel.read(byteBuffer);
          }
          System.out.println(new String(byteBuffer.array(), CharsetUtil.UTF_8));
          byteBuffer.clear();
        } catch (Exception e) {
          key.cancel();
          try {
            key.channel().close();
          } catch (Exception e2) {

          }
          throw new RuntimeException("接收消息异常");
        }
        iterator.remove();
      }
    }
  }

  private void sendMsg(String msg) {
    try {
      socketChannel.write(ByteBuffer.wrap(msg.getBytes()));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
