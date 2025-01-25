package com.abc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.Buffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class B {
  public static void main(String[] args) throws IOException {

    // int s2 = 240;
    // System.out.println("无皮概率:20%");
    // shangcangyiji(s2, 10000);
    // shangcangyiji(s2, 10000 * 100);

    // System.out.println("\n--------------------------------------------------\n");

    // int s1 = 273;
    // System.out.println("皮肤概率:27.3%");
    // shangcangyiji(s1, 10000);
    // shangcangyiji(s1, 10000 * 100);

    // System.out.println("S|n|nS".split("\\|n")[1]);

    Map<String,String[]> map = new TreeMap<>();
    try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("d:\\hero.txt"),"utf8"))){
      String line = null;
      while((line=br.readLine())!=null){
        System.out.println(line);
        String[] ids = line.split(":")[1].split(",");
        System.out.println(ids[1]+","+ids[3]+",");
        map.put(ids[0],ids);
      }
    }

    File dir = new File("C:\\Users\\76769\\Desktop\\新建文件夹");
    File[] listFiles = dir.listFiles();
    Pattern pa = Pattern.compile("^(\\d+).*");
    Map<String, String> fileFullPathMap = new HashMap<>();
    for(File file : listFiles){
      Matcher matcher = pa.matcher(file.getName());
      if(matcher.find()){
        String loc = matcher.group(1);
        System.out.println(loc);
        fileFullPathMap.put(loc, file.getAbsolutePath());
      }
    }
  }

  private static void shangcangyiji(int s, int len) {
    Random random = new Random();

    int num = 0;
    int tmp = 0;
    Map<Integer, Integer> map = new TreeMap<>();
    map.put(6,0);
    for (int i = 0; i < len; i++) {
      int j = random.nextInt(1000);
      if (j < s) {
        tmp++;
        if (tmp > 6) {
          tmp = 6;
        }
        num++;

        Integer v = map.get(tmp);
        if (v == null) {
          map.put(tmp, 1);
        } else {
          map.put(tmp, v + 1);
        }
      } else {
        tmp = 0;
      }
    }
    BigDecimal u = new BigDecimal((len / 3.8) / 60).setScale(0, BigDecimal.ROUND_HALF_UP);
    String time = "";
    if (u.intValue() > 120) {
      time = u.intValue() / 60 + "小时";
    } else {
      time = u.intValue() % 60 + "分钟";
    }
    System.out.println();
    System.out.println("站撸埃文斯:" + time + ",总共平A:" + len / 10000 + "万次");
    System.out.println("上苍次数:" + num);

    map.forEach((k, v) -> {
      System.out.println(String.format("%-14s", k + "连击:" + v) + "概率:"
          + new BigDecimal(v / (double) len).setScale(5, BigDecimal.ROUND_HALF_UP));
    });
  }

  public static String leftPad(String str, int size, char padChar) {
    StringBuilder sb = new StringBuilder(size);
    while (sb.length() < size) {
      sb.append(padChar);
    }
    sb.setLength(size);
    sb.insert(0, str);
    return sb.toString();
  }
}
