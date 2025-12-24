package com.dj;

import java.io.*;
import java.util.*;

import com.common.ini.IniRead;
import com.common.util.FileUtil;

public class Test {
  public static void main(String[] args) throws Exception, FileNotFoundException, IOException {
    getDest3();
  }
  private static void getDest3() throws Exception {
    String file = "D:\\war5-jass\\jass_plugin\\w3x2lni_zhCN_v2.5.2\\w3x2lni_zhCN_v2.5.2\\0dj\\2C6CD3ABB0C45C38A14486A059CFECFA\\table\\ability.ini";
    try (
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf8"))) {
      String line = null;
      int i = 0;
      Map<String, String> map = new LinkedHashMap<>();
      while ((line = br.readLine()) != null) {
        if (line.startsWith("[") && line.length()==6) {
          i++;
          map.put(line, line);
        }
      }
      System.out.println(map.get("[A0UE]"));
      System.out.println(i);

      Map<String, Map<String, String>> read2 = IniRead.read2(file);
      for(String key : map.keySet()){
        key = key.substring(1, key.length()-1);
        Map<String, String> map2 = read2.get(key);
        if(map2 == null){
          System.out.println("id: " + key + " 不存在");
        }
      }
    }
  }

  private static void getDest2() throws IOException, UnsupportedEncodingException, FileNotFoundException {
    String file = "D:\\war5-jass\\jass_plugin\\w3x2lni_zhCN_v2.5.2\\w3x2lni_zhCN_v2.5.2\\template\\Custom\\ability.ini";
    try (
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf8"))) {
      String line = null;
      int i = 0;
      Map<String, String> map = new HashMap<>();
      while ((line = br.readLine()) != null) {
        if (line.startsWith("[")) {
          i++;
          map.put(line, line);
        }
      }
      System.out.println(map.get("[A0UE]"));
      System.out.println(i);
    }
  }

  private static void getDest() throws IOException, UnsupportedEncodingException, FileNotFoundException {
    String file = "D:\\war5-jass\\jass_plugin\\w3x2lni_zhCN_v2.5.2\\w3x2lni_zhCN_v2.5.2\\0dj\\2C6CD3ABB0C45C38A14486A059CFECFA\\map\\war3mapskin.txt";
    String path = "D:\\war5-jass\\jass_plugin\\w3x2lni_zhCN_v2.5.2\\w3x2lni_zhCN_v2.5.2\\0dj\\2C6CD3ABB0C45C38A14486A059CFECFA\\resource\\";
    String dest = "D:\\Code\\demo\\demo\\src\\main\\java\\com\\dj\\";
    try (
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf8"))) {
      String line = null;
      while ((line = br.readLine()) != null) {
        if (line.contains(".blp")) {
          String name = line.split("=")[1];
          String name2 = name.substring(name.lastIndexOf("\\") + 1);
          String filename = path + name;

          try {
            FileUtil.copyWithTransferTo(filename, dest + name2);
          } catch (Exception e) {

          }

        }
      }
    }
  }
}
