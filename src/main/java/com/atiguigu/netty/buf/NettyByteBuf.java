package com.atiguigu.netty.buf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

public class NettyByteBuf {
  public static void main(String[] args) {

    // 0 - readIndex - writeIndex  - capacity
    ByteBuf byteBuf  = Unpooled.buffer(10);
    for (int i = 0; i < byteBuf.capacity(); i++) {
      byteBuf.writeByte(i);
    }
    System.out.println(12222);

    for (int i = 0; i < byteBuf.capacity(); i++) {
      byte b = byteBuf.readByte();
      System.out.println(b);
    }

    byteBuf.clear();
    byteBuf.setIndex(0, 0);

    ByteBuf buf = Unpooled.copiedBuffer("hello", CharsetUtil.UTF_8);
    if(buf.hasArray()){
      byte[] content = buf.array();
      System.out.println(new String(content, CharsetUtil.UTF_8));
    }
  }
}
