package com.atiguigu.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NIOServer {
  public static void main(String[] args) throws IOException {
    ServerSocketChannel ssc = ServerSocketChannel.open();

    Selector selector = Selector.open();

    ssc.socket().bind(new InetSocketAddress(6666));
    ssc.configureBlocking(false);

    ssc.register(selector, SelectionKey.OP_ACCEPT);

    while (true) {
      if (selector.select(1000) == 0) {
        continue;
      }

      Set<SelectionKey> selectedKeys = selector.selectedKeys();
      Iterator<SelectionKey> iterator = selectedKeys.iterator();
      while (iterator.hasNext()) {
        SelectionKey key = iterator.next();
        if (key.isAcceptable()) {
          System.out.println("有客户端连接"+System.currentTimeMillis());
          SocketChannel accept = ssc.accept();
          System.out.println(accept.hashCode()+"-客户端连接成功,"+System.currentTimeMillis());
          accept.configureBlocking(false);
          accept.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));
        }
        if (key.isReadable()) {
          System.out.println("读取数据");
          SocketChannel channel = (SocketChannel) key.channel();
          ByteBuffer buffer = (ByteBuffer) key.attachment();
          channel.read(buffer);
          System.out.println(new String(buffer.array()));
        }
        selectedKeys.remove(key);
      }
    }
  }


}
