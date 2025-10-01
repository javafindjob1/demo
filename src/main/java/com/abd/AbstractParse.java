package com.abd;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public abstract class AbstractParse {
  

  public static String trimName(String name) {
    return name.replaceAll("\\|cff\\w{6}", "").replaceAll("\\|r", "").replaceAll("（\\d+%）", "");
  }
  
  public static Map<String, Map<String, String>> getMark() {
    // Map<备注,Map>
    Map<String, Map<String, String>> markMap = new LinkedHashMap<>();
    try {
      InputStream file = UnitParse.class.getResourceAsStream("custom/mark.ini");
      Map<String, Map<String, String>> units = IniRead.read2(file);

      return units;
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("读取mark.ini文件异常" + e.getMessage());
    }
    return markMap;

  }

  public static Map<String, Map<String, Map<String, Integer>>> getMarkType() {
    // Map<备注,Map>
    Map<String, Map<String, Map<String, Integer>>> markMap = new LinkedHashMap<>();
    try {
      InputStream file = UnitParse.class.getResourceAsStream("custom/markType.ini");
      Map<String, Map<String, String>> units = IniRead.read2(file);

      for (Entry<String, Map<String, String>> entry : units.entrySet()) {
        String type = entry.getKey();
        String[] arr = type.split("-");
        String bigType = arr[0];
        String smallType = arr[1];

        Map<String, Map<String, Integer>> notNull = MapUtil.getNotNull(markMap, bigType, LinkedHashMap::new);
        Map<String, Integer> list2 = new LinkedHashMap<>();
        List<String> list = new ArrayList<>();
        list.addAll(entry.getValue().keySet());
        for (int i = 0; i < list.size(); i++) {
          list2.put(list.get(i), i);
        }
        notNull.put(smallType, list2);
      }
      return markMap;
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("读取markType.ini文件异常" + e.getMessage());
    }
    return markMap;

  }

}
