package com.atiguigu.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

public class ScatteringAndGatheringTest {
  public static void main(String[] args) throws Exception {
    ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
    InetSocketAddress InetSocketAddress = new InetSocketAddress(7000);

    // 绑定端口到socket，并启动
    serverSocketChannel.socket().bind(InetSocketAddress);

    // 创建buffer数组
    ByteBuffer[] byteBuffers = new ByteBuffer[2];
    byteBuffers[0] = ByteBuffer.allocate(5);
    byteBuffers[1] = ByteBuffer.allocate(3);

    SocketChannel socketChannel = serverSocketChannel.accept();

    int messageLength = 8;
    // 循环读取
    while(true){
      int byteRead = 0;
      while(byteRead < messageLength){
        long read = socketChannel.read(byteBuffers);
        byteRead += read;
        System.out.println("byteRead:" + byteRead);

        Arrays.asList(byteBuffers).stream().map(buffer-> "position=" + buffer.position() + " limit=" + buffer.limit()).forEach(System.out::println);
      }

      // 将所有的buffer进行flip
      Arrays.asList(byteBuffers).forEach(ByteBuffer::flip);

      int byteWrite = 0;
      while(byteWrite < messageLength){
        long len = socketChannel.write(byteBuffers);
        byteWrite += len;
      }

      // 将所有的buffer进行clear
      Arrays.asList(byteBuffers).forEach(ByteBuffer::clear);
    } // ---循环读取
  }
}
