package com.abd;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import io.netty.util.CharsetUtil;

public class FunctionRead {
  public List<Function> read(String fileName) throws IOException {

    List<Function> functions = new ArrayList<>();
    try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), CharsetUtil.UTF_8))){
      String line = null;
      Function tmpFun = null;
      while((line = br.readLine()) != null){
        if(line.startsWith("function")){
          tmpFun = new Function();
          tmpFun.setName(line.split("\\s+")[1]);
          functions.add(tmpFun);
        }
        if(tmpFun !=null){
          tmpFun.addRow(line);
        }
        if(line.startsWith("endfunction")){
          tmpFun = null;
        }
      }
    }

    return functions;
  }

  public static void main(String[] args) throws IOException {
    List<Function> list = new FunctionRead().read("war3map.j还原256.j");
    System.out.println(list.size());
    System.out.println(list.get(0));
  }
}
