package com.atiguigu.nio.zerocopy;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class OldCopyServer {
  public static void main(String[] args) throws IOException {
    try (ServerSocket serverSocket = new ServerSocket(7001)) {
      while (true) {
        Socket socket = serverSocket.accept();
        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
        try {
          byte[] bytes = new byte[4096];
          int readCount = 0;
          long total = 0;
          while ((readCount = dataInputStream.read(bytes)) != -1) {
            total += readCount;
          }
          System.out.println("readCount = " + total);
        } catch (Exception e) {

        }
      }
    }
  }
}
