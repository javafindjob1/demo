package com.atiguigu.bio;

import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BIOServer {
  public static void main(String[] args) throws Exception {
    ExecutorService threadpool = Executors.newCachedThreadPool();

    ServerSocket serverSocket = new ServerSocket(6666);
    System.out.println("服务器启动了");
    while(true){

      // 监听,等待客户端连接
      final Socket socket = serverSocket.accept();
      System.out.println("连接到一个客户端");

      // 通过线程池创建线程
      threadpool.execute(new Runnable() {
        @Override
        public void run() {
          // 和客户端通讯
          handler(socket);
        }
      });
    }
  }

  public static void handler(Socket socket){
    try{
      System.out.println("线程信息id="+Thread.currentThread().getId()+"名字="+Thread.currentThread().getName());
      byte[] bytes = new byte[1024];
      // 通过socket获取输入流
      InputStream inputStream = socket.getInputStream();
      while(true){
        int read = inputStream.read(bytes);
        if(read != -1){
          System.out.println(new String(bytes,0, read));
        }else{
          System.out.println("流读取结束");
          break;
        }
      }
    }catch(Exception e){
      e.printStackTrace();
    }finally{
      try{
        System.out.println("流关闭");
        socket.close();
      }catch(Exception e){
        e.printStackTrace();
      }
    }
  }
        
}
