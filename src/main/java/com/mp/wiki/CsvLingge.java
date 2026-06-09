package com.mp.wiki;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mp.parse.AbilityDetail;
import com.mp.parse.ExcelImageInsert;

public class CsvLingge extends Csv {
  // 禁止出现| 防止wiki语法冲突


  public static void insert(String mapName, String subname, Map<String, Map<String, String>> lingeMap,
      Map<String, AbilityDetail> abilityMap) throws IOException {
    StringBuilder buf = new StringBuilder();
    buf.append("level,name,prop,mark").append("\n");
    int sort = 1;
    for (Entry<String, Map<String, String>> entry : lingeMap.entrySet()) {
      String key = entry.getKey().split("-")[1];
      Map<String, String> value = entry.getValue();
      for (String key2 : value.keySet()) {
        AbilityDetail d = abilityMap.get(key2);
        wrapAbil(d, mapName, key, sort++, buf);
      }
    }
    boolean append = false;
    try (BufferedWriter wr = new BufferedWriter(
        new OutputStreamWriter(new FileOutputStream(mapName + "-lingges.csv", append), "GBK"))) {
      wr.write(buf.toString());
    }
    System.out.println("写入完成");
  }

  public static void wrapAbil(AbilityDetail d, String mapName, String role, int sort, StringBuilder buf)
      throws IOException {
    if (d == null) {
      throw new RuntimeException("未找到技能");
    }
    // - |cff00ffff熔岩龟|r -
    Pattern p = Pattern.compile("- (\\|cff(\\w{0,6}))?(.*?)(\\|r)? -");
    Matcher matcher = p.matcher(d.getName());
    String name = "";
    if (matcher.find()) {
      name = matcher.group(3);
    }
    buf.append(role);
    buf.append(",").append(name);
    buf.append(",").append(d.getUbertip());
    buf.append(",").append(format(d.getMark()));
    buf.append("\n");

    String fileName = "灵格-" + name + ".png";
    File f = new File("wikiimagelingges\\" + fileName);
    if (!f.exists()) {
      ExcelImageInsert.convertImageToPng(d.getArt().replace(".tga", ".blp"), f);
    }
  }
}
