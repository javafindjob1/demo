package com.atiguigu.nio;

import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class MappedByteBufferTest {
  public static void main(String[] args) throws Exception {
    RandomAccessFile randomAccessFile = new RandomAccessFile("D:\\file01.txt", "rw");
    FileChannel channel = randomAccessFile.getChannel();
    MappedByteBuffer mappedByteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, 0,5);
    mappedByteBuffer.put(0, (byte)'H');
    randomAccessFile.close();
  }
}
