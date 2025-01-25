package com.abd;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class CopyFile {
  
  public static void main(String[] args) throws Exception{
    String a = String.format("%u", 1);
    System.out.println(a);
  }


  public static void copyFile() throws Exception{
    String srcFile = "D:\\war5-jass\\jass_plugin\\新建文件夹3\\none.png";
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 6; j++) {
        try(FileInputStream fi = new FileInputStream(srcFile);
          FileOutputStream fo = new FileOutputStream("D:\\war5-jass\\jass_plugin\\新建文件夹3\\"+(i*2+2)+(j+1)+".png")
        ){
          fi.getChannel().transferTo(0, fi.getChannel().size(), fo.getChannel());
        }
      }
    }
  }
}
