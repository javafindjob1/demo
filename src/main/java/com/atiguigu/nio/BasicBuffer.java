package com.atiguigu.nio;

import java.nio.IntBuffer;

public class BasicBuffer {
  public static void main(String[] args) {
    // 创建一个Buffer
    // 1. 分配一个指定大小的缓冲区
    // 2. 创建一个
    IntBuffer intBuffer = IntBuffer.allocate(5);
    intBuffer.put(10);
    intBuffer.put(11);
    intBuffer.put(12);
    intBuffer.put(13);
    intBuffer.put(14);
    // 读写切换
    intBuffer.flip();
    while (intBuffer.hasRemaining()) {
      System.out.println(intBuffer.get());
    }
  }

}
