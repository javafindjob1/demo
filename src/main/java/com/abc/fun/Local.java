package com.abc.fun;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.Set;

import io.netty.util.CharsetUtil;

public class Local {

  public static void main(String[] args) throws FileNotFoundException, IOException {
    String filePath = "item.ini"; // 文件路径

    Properties properties = new Properties();

    
    try (FileInputStream fileInputStream = new FileInputStream(filePath)) {
      properties.load(fileInputStream);
      // 获取所有的键值对
      Set<Object> keys = properties.keySet();
      for (Object key : keys) {
          String keyValue = properties.getProperty((String) key);
          System.out.println(key + ": " + keyValue);
      }

      // 获取特定物品的优先级
      String moonStonePrio = properties.getProperty("moon_stone.prio");
      String sunStonePrio = properties.getProperty("sun_stone.prio");

      System.out.println("Moon Stone Priority: " + moonStonePrio);
      System.out.println("Sun Stone Priority: " + sunStonePrio);
  } catch (IOException e) {
      System.err.println("Error reading the INI file: " + e.getMessage());
  }
  }
}
