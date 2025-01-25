package com.atiguigu.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NIOClient {
  public static void main(String[] args) throws IOException {
    SocketChannel socketChannel = SocketChannel.open();
    socketChannel.configureBlocking(false);
    InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 6666);
    
    System.out.println("连接中1-"+System.currentTimeMillis());
    socketChannel.connect(inetSocketAddress);
    while (!socketChannel.finishConnect()) {
      System.out.println("连接中2-"+System.currentTimeMillis());
    }
    System.out.println("连接成功2-"+System.currentTimeMillis());

    String str = "hello";
    ByteBuffer buffer = ByteBuffer.wrap(str.getBytes());

    socketChannel.write(buffer);
    System.in.read();
  }
}
