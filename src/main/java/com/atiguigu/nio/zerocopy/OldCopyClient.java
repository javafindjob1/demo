package com.atiguigu.nio.zerocopy;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class OldCopyClient {
  public static void main(String[] args) throws UnknownHostException, IOException {
    // send("image.png");
    String filename = "w3x2lni_zhCN_v2.5.2.zip";
    // send(filename);
    // send2(filename);
    send3(filename);
   }

  private static void send(String filename) throws IOException, UnknownHostException, FileNotFoundException {
    try (Socket socket = new Socket("localhost", 7001);
        InputStream inputStream = new FileInputStream(filename);
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());) {

      byte[] buffer = new byte[1024];
      long total =0;
      int readCount = 0;
      long start = System.currentTimeMillis();
      while ((readCount = inputStream.read(buffer)) != -1) {
        dataOutputStream.write(buffer,0 ,readCount);
        total += readCount;
      }
      System.out.println("发送总字节数：" + total + "耗时：" + (System.currentTimeMillis() - start));

    }
  }

  private static void send2(String filename) throws IOException, UnknownHostException, FileNotFoundException {
    try (Socket socket = new Socket("localhost", 7001);
        BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(filename), 1024);
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());) {

      byte[] buffer = new byte[1024];
      long total =0;
      int readCount = 0;
      long start = System.currentTimeMillis();
      while ((readCount = inputStream.read(buffer)) != -1) {
        dataOutputStream.write(buffer,0 ,readCount);
        total += readCount;
      }
      System.out.println("发送总字节数：" + total + "耗时：" + (System.currentTimeMillis() - start));

    }
  }

  private static void send3(String filename) throws IOException, UnknownHostException, FileNotFoundException {
    try (Socket socket = new Socket("localhost", 7001);
        BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(filename));
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());) {

      byte[] buffer = new byte[1024];
      long total =0;
      int readCount = 0;
      long start = System.currentTimeMillis();
      while ((readCount = inputStream.read(buffer)) != -1) {
        dataOutputStream.write(buffer, 0, readCount);
        total += readCount;
      }
      System.out.println("发送总字节数：" + total + "耗时：" + (System.currentTimeMillis() - start));

    }
  }
}
