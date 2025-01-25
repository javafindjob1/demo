package com.atiguigu.nio;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class NIOFileChannel02 {
  public static void main(String[] args) throws Exception{
    FileInputStream fileInputStream = new FileInputStream("d:\\file01.txt");

    FileChannel channel = fileInputStream.getChannel();

    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
    channel.read(byteBuffer);

    System.out.println(new String(byteBuffer.array()));
    fileInputStream.close();
  }
}