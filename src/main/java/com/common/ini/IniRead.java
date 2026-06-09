package com.common.ini;

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

public class IniRead {

  public static <T extends Ini> List<T> read(String basePath, String path, Class<T> clazz) throws Exception {
    // template/Custom/item.ini
    // C:\Users\76769\Downloads\w3x2lni_zhCN_v2.5.2\w3x2lni_zhCN_v2.5.2\w3x2lni_zhCN_v2.5.2\959D65B18EA9E93F2F0205DB268039F8\959D65B18EA9E93F2F0205DB268039F8\table\item.ini
    Map<String, Map<String, String>> baseItemMap = new HashMap<>();
    if (basePath != null) {
      baseItemMap.putAll(read2(basePath));
    }
    Map<String, Map<String, String>> itemMap = read2(path);

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

    // 构建List<Item>
    List<T> list = new ArrayList<>();
    for (Map<String, String> it : itemMap.values()) {
      T t = clazz.newInstance();
      it.forEach(t::set);
      list.add(t);
    }
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
    try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {

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

  private static Pattern propPattern = Pattern.compile("^\\s*(\\w+)\\s*=\\s*(.*?)(\\s*-- .*)?$");
  private static Pattern valuePattern = Pattern.compile("^(\\s*\\w+\\s*=)?\\s*\"?(.*?)\"?,?(\\s*-- .*)?$");

  public static String[] parseProp(String propStrings) {
    if (propStrings == null) {
      return new String[]{null};
    }
    Matcher matcher = propPattern.matcher(propStrings);
    if (!matcher.find()) {
      return null;
    }

    String key = matcher.group(1);
    String value = matcher.group(2);
    value = trimString(value);
    return new String[]{key, value};
  }

  private static String trimString(String str) {
    return str.replaceAll("^\"|\"$", "");
  }

  private static void parseBoby(String line, Map<String, String> tmpItem, BufferedReader bufferedReader) throws IllegalAccessException, IOException {
    if (tmpItem == null) return;

    String[] kv = parseProp(line);
    if (kv == null && kv.length != 2) return;

    String key = kv[0];
    String value = kv[1];

    StringBuilder formatValue = new StringBuilder();
    if (value.endsWith("[=[")) {
      // 长字符串
      /*
        a=[=[
         体力*1
         ]=]
       */
      // 针对多行的情况
      if (value.endsWith("]=]")) {
        // 单行还要用[=[???????]=]，不解释
        throw new RuntimeException("单行字符串，不能用[=[ ]=]");
      } else {
        String multiLine = longString(bufferedReader);
        formatValue.append(multiLine);
      }
    } else if (value.endsWith("}")) {
      // 针对value是{0,1,2}这种情况 化为一整行
      String[] arr = value.substring(1, value.length() - 1).split(",");
      for (int i = 0; i < arr.length; i++) {
        formatValue.append(arr[i].trim());
        if (i != arr.length - 1)
          formatValue.append("@,@");
      }
    } else if (!value.endsWith("{")) {
      // a=xxxx
      formatValue.append(value);
    } else {
      /*
      a={
        0=xx,
        1=yy
      }
       */
      // 向下连续读
      while ((line = bufferedReader.readLine()) != null) {

        if (line.endsWith("}")) { //遍历到数组尾部
          if (formatValue.indexOf("@,@") > 0) {
            formatValue.setLength(formatValue.length() - 3);
          }
          break;
        } else {
          // 针对长字符串处理，向下读
          if (line.endsWith("[=[")) {
            /*
              Ubertip = {
              0=[=[
               体力*1
              ]=],
              2=[=[
               体力*2
              ]=],
              }
            */
            String multiLine = longString(bufferedReader);
            formatValue.append(multiLine).append("@,@");
          } else {
            /*
              Ubertip = {
              0=体力*1,
              2=体力*2,
            }
            */
            formatValue.append(parseValue(line)).append("@,@");
          }
        }
      }//while
    }//else

    tmpItem.put(key, formatValue.toString());
  }

  public static String longString(BufferedReader bufferedReader) throws IOException {
    StringBuilder multiLine = new StringBuilder();
    String line;
    while ((line = bufferedReader.readLine()) != null) {
      if (line.contains("]=]")) {
        break;
      } else {
        multiLine.append(line).append("|n");
      }
    }
    if (multiLine.length() > 0) {
      // 删掉末尾的|n
      multiLine.setLength(multiLine.length() - 2);
    }
    return multiLine.toString();
  }

  public static String parseValue(String line) {
    Matcher matcher = valuePattern.matcher(line);
    if (matcher.find()) {
      return matcher.group(2);
    } else {
      // 失败！
      throw new RuntimeException("解析 30 = sdfsdfsdf 失败");
    }
  }

  public static void testParseProp() {
    String propStrings = "name=";
    String[] strings = parseProp(propStrings);
    System.out.println(strings[0] + "=" + strings[1]);
  }

  public static void main(String[] args) throws Exception {
    testParseProp();

    List<Ability> list = read(null, "D:\\war5-jass\\jass_plugin\\w3x2lni_zhCN_v2.5.2\\w3x2lni_zhCN_v2.5.2\\0x5\\BFE34A27828074CDD3AFD5F01D0A987B\\table\\ability.ini", Ability.class);

    System.out.println(list);

  }

}
