package com.atiguigu.nio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class NIOFileChannel04 {
  public static void main(String[] args) throws Exception{
    FileInputStream fileInputStream = new FileInputStream("d:\\file01.txt");
    FileChannel channel1 = fileInputStream.getChannel();

    FileOutputStream fileOutputStream = new FileOutputStream("d:\\file02.txt");
    FileChannel channel2 = fileOutputStream.getChannel();

    channel2.transferFrom(channel1, 0, channel1.size());
    fileInputStream.close();
    fileOutputStream.close();
  }
}
