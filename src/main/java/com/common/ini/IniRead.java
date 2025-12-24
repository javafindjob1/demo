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
        BufferedReader bufferedReader = new BufferedReader(
            new InputStreamReader(in, StandardCharsets.UTF_8))) {

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
  private static Pattern valuePattern = Pattern.compile("^(\\s*\\w+\\s*=)?\\s*\"?(.*?)\"?,?(\\s*--.*)?$");

  public static String[] parseProp(String propStrings) {
    if (propStrings == null) {
      return new String[] { null };
    }
    Matcher matcher = propPattern.matcher(propStrings);
    if (false == matcher.find()) {
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

  private static void parseBoby(String line, Map<String, String> tmpItem, BufferedReader bufferedReader)
      throws IllegalAccessException, IOException {
    if (tmpItem == null)
      return;

    String[] kAndV = parseProp(line);
    if (kAndV.length == 2) {
      String key = kAndV[0];
      String value = kAndV[1];

      String lastValue = "";
      if (value.endsWith("{")) {
        // 数组
        // 针对value是{}这种情况 化为一整行
        if (value.endsWith("}")) {
          lastValue = value.substring(1, value.length() - 1);
        } else {
          // 向下连续读
          while ((line = bufferedReader.readLine()) != null) {

            if (line.endsWith("}")) {
              break;
            } else {
              // 针对长字符串处理，向下读
              if (line.endsWith("[=[")) {
                StringBuilder multiLine = new StringBuilder();
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
                lastValue = multiLine.toString();
              } else {
                lastValue = line;
              }
            }
          }
        }
      } else if (value.endsWith("[=[")) {
        // 长字符串
        // 针对多行的情况
        if (value.endsWith("]=]")) {
          // 单行还要用[=[???????]=]，不解释
          throw new RuntimeException("单行字符串，不能用[=[ ]=]");
        } else {
          StringBuilder multiLine = new StringBuilder();
          while ((line = bufferedReader.readLine()) != null) {
            if (line.endsWith("]=]")) {
              break;
            } else {
              multiLine.append(line).append("|n");
            }
          }
          if (multiLine.length() > 0) {
            // 删掉末尾的|n
            multiLine.setLength(multiLine.length() - 2);
          }
          lastValue = multiLine.toString();
        }
      }else{
        lastValue = value;
      }
      // 处理 30 = sdfsdfsdf
      Matcher matcher = valuePattern.matcher(lastValue);
      if (matcher.find()) {
        value = matcher.group(2);
      } else {
        // 失败！
        throw new RuntimeException("解析 30 = sdfsdfsdf 失败");
      }
      tmpItem.put(key, value);
    } else {
      throw new RuntimeException("解析失败");
    }
  }

  private static Pattern pArr = Pattern.compile("^\\{\\s*\\d+\\s*=\\s*");
  // 前向匹配
  private static Pattern pArr2 = Pattern.compile("\\s*\\d+\\s*=\\s*(.*?)(?=,\\s*\\d+\\s*=\\s*)");

  /**
   * 
   * {1.0,2,3}
   * {"a,=","b","c"}
   * {1=1.0,2=2,3=3}
   * {1="a,=",2="b",3="c"}
   * {1=[=[中华]=],2=[=[中国]=]}
   * 
   * @param list
   * @return
   */
  public static String parseArr(String value) {
    StringBuilder buf = new StringBuilder();
    Matcher matcher = pArr.matcher(value);
    if (matcher.find()) {
      value = value.substring(1, value.length() - 1);
      if (!value.endsWith(",")) {
        value += ",";
      }
      value += " 0=";
      // 标准模式
      Matcher matcher2 = pArr2.matcher(value);
      while (matcher2.find()) {
        String txt = matcher2.group(1);
        if (txt.startsWith("\"")) {
          txt = txt.substring(1, txt.length() - 1);
          buf.append(txt);
        } else if (txt.startsWith("[=[")) {
          int startIndex = txt.indexOf("[=[") + 3;
          int endIndex = txt.indexOf("]=]", startIndex);
          txt = txt.substring(startIndex, endIndex);
          buf.append(txt);
        } else {
          buf.append(txt);
        }
        buf.append("@,@");
      }
      if (buf.length() == 0) {
        System.out.println("解析失败：");
        System.out.println(value);
        throw new RuntimeException("解析失败");
      }

      // 删掉末尾的@,@
      buf.setLength(buf.length() - 3);

    } else {
      if (value.length() > 2) {
        value = value.substring(1, value.length() - 1);

        if (value.startsWith("\"")) {
          // 字符串
          int startIndex = 1;
          int endIndex = value.length() - 1;
          if (value.endsWith(",")) {
            endIndex--;
          }

          value = value.substring(startIndex, endIndex);
          buf.append(String.join("@,@", value.split("\",\"")));
        } else {
          // 数字
          if (value.endsWith(",")) {
            value = value.substring(0, value.length() - 1);
          }
          buf.append(String.join("@,@", value.split(",")));
        }

      } else {
        // body空{}
      }

    }
    value = buf.toString();
    return value;
  }

  public static void testParseProp() {
    String propStrings = "name=";
    String[] strings = parseProp(propStrings);
    System.out.println(strings[0] + "=" + strings[1]);
  }

  public static void main(String[] args) throws Exception {
    testParseProp();

    List<Ability> list = new IniRead().read(null, "D:\\war5-jass\\jass_plugin\\w3x2lni_zhCN_v2.5.2\\w3x2lni_zhCN_v2.5.2\\0mp\\1A8B6AE6B4A86096D2DA43AD9C96B3D8\\table\\ability.ini", Ability.class);

    System.out.println(list);

  }

}
