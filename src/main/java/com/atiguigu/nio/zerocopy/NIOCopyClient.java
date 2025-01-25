package com.atiguigu.nio.zerocopy;

import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

public class NIOCopyClient {
  public static void main(String[] args) throws Exception {
    String fileName = "w3x2lni_zhCN_v2.5.2.zip";
    try (
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("localhost", 7001));
        FileInputStream fileInputStream = new FileInputStream(fileName);
        FileChannel channel = fileInputStream.getChannel()) {

      long start = System.currentTimeMillis();
      long i = 0;
      int m8 = 1024 * 1024 * 8;
      while (true) {
        if (channel.size() - i > m8) {
          channel.transferTo(i, m8, socketChannel);
          i += 1024 * 1024 * 8;
        } else {
          channel.transferTo(i, channel.size(), socketChannel);
          break;
        }
      }

      System.out.println("发送完成，耗时：" + (System.currentTimeMillis() - start));
    }
  }
}
