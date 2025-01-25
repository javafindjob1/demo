package com.b4;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class IniRead {

  public <T extends Ini> List<T> read(String basePath, String path, Class<T> clazz) throws Exception {
    // template/Custom/item.ini
    // C:\Users\76769\Downloads\w3x2lni_zhCN_v2.5.2\w3x2lni_zhCN_v2.5.2\w3x2lni_zhCN_v2.5.2\959D65B18EA9E93F2F0205DB268039F8\959D65B18EA9E93F2F0205DB268039F8\table\item.ini

    Map<String, Map<String, String>> baseItemMap = read2(basePath);
    Map<String, Map<String, String>> itemMap = read2(path);
    System.out.println(baseItemMap.get("anfg"));
    System.out.println(itemMap.get("I067"));

    // 合并
    itemMap.forEach((id, item) -> {
      String parent = item.get("_parent");
      Map<String, String> baseItem = baseItemMap.get(parent);
      if (parent == null || baseItem == null) {
        return;
      }

      Map<String, String> merge = new HashMap<>();
      merge.putAll(baseItem);
      merge.putAll(item);
      merge.put("id", id);
      itemMap.put(id, merge);
    });

    System.out.println(itemMap.get("ncg3"));
    // 构建List<Item>
    List<T> list = new ArrayList<>();
    itemMap.values().forEach(it -> {
      try {
        T t = clazz.newInstance();
        it.forEach((k, v) -> {
          t.set(k, v);
        });
        list.add(t);
      } catch (InstantiationException | IllegalAccessException e) {
        e.printStackTrace();
      }
    });

    return list;
  }

  // Map<itemid,Map<key,value>>
  public static Map<String, Map<String, String>> read2(String path) throws Exception {
    FileInputStream fileInputStream = new FileInputStream(path);
    return read2(fileInputStream);
  }

  public static Map<String, Map<String, String>> read2(InputStream in) throws Exception {
    // C:\Users\76769\Downloads\w3x2lni_zhCN_v2.5.2\w3x2lni_zhCN_v2.5.2\w3x2lni_zhCN_v2.5.2\959D65B18EA9E93F2F0205DB268039F8\959D65B18EA9E93F2F0205DB268039F8\table\item.ini

    Map<String, Map<String, String>> iteMap = new LinkedHashMap<>();
    try (
        InputStream ini = in;
        BufferedReader bufferedReader = new BufferedReader(
            new InputStreamReader(ini, StandardCharsets.UTF_8))) {

      String line;
      Map<String, String> item = null;
      while ((line = bufferedReader.readLine()) != null) {
        if (line.length() == 0 || line.startsWith("-- ")) {
          continue;
        }

        if (line.startsWith("[")) {
          String itemid = parseHead(line);
          item = new LinkedHashMap<>();
          iteMap.put(itemid, item);
        } else {
          parseBoby(line, item, bufferedReader);
        }

      }
    }

    return iteMap;
  }

  private static String parseHead(String line) {
    return line.substring(1, line.length() - 1);
  }

  private static Pattern propPattern = Pattern.compile("^\\s*(\\w+)\\s*=\\s*(.*?)(\\s*--.*)?$");
  public static String[] parseProp(String propStrings) {
    if (propStrings == null) {
      return new String[] { null };
    }
    Matcher matcher = propPattern.matcher(propStrings);
    if(false == matcher.find()){
      return new String[] { null };
    }
    
    String key = matcher.group(1);
    String value = matcher.group(2);
    value = trimString(value);
    return new String[] { key, value };
  }

  private static String trimString(String str) {
    return str.replaceAll("^\"|\"$", "");
  }
  
  private static Pattern pArr = Pattern.compile("(\\d+\\s*=\\s*)?(([0-9\\.]+)|(\".*?\")),");
  private static void parseBoby(String line, Map<String, String> tmpItem, BufferedReader bufferedReader)
      throws IllegalAccessException, IOException {
    if (tmpItem == null)
      return;

    String[] kAndV = parseProp(line);
    if (kAndV.length == 2) {
      String key = kAndV[0];
      String value = kAndV[1];

      // 针对value是{}这种情况 化为一整行
      if (value.startsWith("{") && !value.endsWith("}")) {
        while ((line = bufferedReader.readLine()) != null) {
          value += line;
          if (line.endsWith("}")) {
            break;
          }
        }
      }
      if (value.startsWith("{")) {
        StringBuilder buf = new StringBuilder();
        // {1.0,2,3}
        // {"a,=","b","c"}
        // {1=1.0,2=2,3=3}
        // {1="a,=",2="b",3="c"}
        value = value.substring(1,value.length()-1) + ",";
        Matcher matcher = pArr.matcher(value);
        while(matcher.find()){
          String v = matcher.group(2);
          v = trimString(v);
          buf.append(v).append("@,@");
        }
        buf.setLength(buf.length()-3);
        value = buf.toString();
      }
      tmpItem.put(key, value);
    }
  }

  public static void testParseProp() {
    String propStrings = "name=";
    String[] strings = parseProp(propStrings);
    System.out.println(strings[0] + "=" + strings[1]);
  }

  public static void main(String[] args) throws Exception {
    testParseProp();

    if (true)
      return;
    List<Item> list = new IniRead().read("template/Custom/item.ini", "item.ini", Item.class);

    System.out.println("\"a\"f\"".replaceAll("^\"|\"$", ""));

  }

}
