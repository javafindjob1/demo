package com.atiguigu.nio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class NIOFileChannel03 {
  public static void main(String[] args) throws Exception{
    FileInputStream fileInputStream = new FileInputStream("d:\\file01.txt");
    FileChannel channel1 = fileInputStream.getChannel();

    FileOutputStream fileOutputStream = new FileOutputStream("d:\\file02.txt");
    FileChannel channel2 = fileOutputStream.getChannel();

    ByteBuffer byteBuffer = ByteBuffer.allocate(2);
    while(true){
      byteBuffer.clear();
      int i = channel1.read(byteBuffer);
      System.out.println("i="+i);
      if(i == -1){
        break;
      }
      byteBuffer.flip();
      channel2.write(byteBuffer);
    }

    fileInputStream.close();
    fileOutputStream.close();
  }
}
