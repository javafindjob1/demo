package com.atiguigu.nio.zerocopy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class NIOCoopyServer {
  public static void main(String[] args) throws IOException {
    ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
    serverSocketChannel.socket().bind(new InetSocketAddress("127.0.0.1",7001));

    ServerSocketChannel serverSocketChannel2 = ServerSocketChannel.open();
    serverSocketChannel2.socket().bind(new InetSocketAddress("192.168.2.226",7001));

    while (true) {
      System.out.println("等待连接...");
      SocketChannel socketChannel = serverSocketChannel.accept();
      System.out.println("连接成功...");
      System.out.println("socketChannel=" + socketChannel.getRemoteAddress());

      // 创建一个缓冲区
      ByteBuffer byteBuffer = ByteBuffer.allocate(4096);

      try {
        long size = 0;
        while (true) {
          int readCount = socketChannel.read(byteBuffer);
          if (readCount != -1) {
            size += readCount;
            byteBuffer.rewind();
          } else {
            System.out.println("接收文件总大小" + size);
            break;
          }
        }
      } catch (Exception e) {
        System.out.println("客户端断开连接");
      }
    }
  }
}