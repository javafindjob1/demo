package com.x4;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;

public class Test {
  public static void main(String[] args) throws Exception {
    // 将分数转为百分比
    String path = Test.class.getResource("p.txt").getFile();
    try (
        BufferedReader bufferedReader = new BufferedReader(
            new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8))) {

      String line;
      while ((line = bufferedReader.readLine()) != null) {
        if (line.length() == 0 || line.startsWith("-- ")) {
          continue;
        }
        String[] arr = line.split("/");
        double d1 = Double.parseDouble(arr[0]);
        double d2 = Double.parseDouble(arr[1]);
        double v = d1 / d2;
        DecimalFormat df = new DecimalFormat("#.##%");
        String value = df.format(v);
        System.out.println(value);
      }
    }
  }
}
