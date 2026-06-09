package com.mp.wiki;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import com.mp.function.hero.Hero;
import com.mp.parse.AbilityDetail;
import com.mp.parse.ExcelImageInsert;

public class CsvAbil {
  // 禁止出现| 防止wiki语法冲突
  public static String format(String str) {
    return str == null ? "" : str.replaceAll("\\|", "\\\\");
  }

  public static void insert(String mapName, String subname, Map<String, Hero[]> map)
      throws UnsupportedEncodingException, FileNotFoundException, IOException {
    // 文件 梦想愿景-物品-猩红杖.png
    // ==简介==
    // {{技能
    // |角色=狂战神
    // |ID=I06F
    // |键位=Q
    // |名称=无畏跳斩
    // |图片格式=png
    // |挥舞着武器，猛地跳起对地面敌人发动重击。
    // 主动效果：

    // 跳向800码范围内的指定敌方单位，对目标及周围300码范围内的敌方单位造成（240+攻击力×30%）×技能等级的火属性物理伤害，并眩晕其1+0.1×技能等级秒。
    // 冷却时间9秒。
    // 跳跃过程中无敌。
    // }}

    StringBuilder buf = new StringBuilder();
    for (Hero[] list : map.values()) {
      String role = list[0].getUnit().getPropernames();
      role = role.replaceAll("\\|cff\\w{6}", "").replaceAll("\\|r", "");
      for (int i = 0; i < list.length; i++) {
        Hero detail = list[i];
        if (detail == null) {
          continue;
        }
        if (i > 0) {
          role += i;
        }

        wrapAbil(detail.getQ(), mapName, role, buf);
        wrapAbil(detail.getW(), mapName, role, buf);
        wrapAbil(detail.getE(), mapName, role, buf);
        wrapAbil(detail.getR(), mapName, role, buf);
        wrapAbil(detail.getT(), mapName, role, buf);
      }

    }

    boolean append = true;
    try (BufferedWriter wr = new BufferedWriter(
        new OutputStreamWriter(new FileOutputStream(mapName + "-abils.txt", append), "UTF-8"))) {
      wr.write(buf.toString());
    }
    System.out.println("写入完成");
  }

  public static void wrapAbil(AbilityDetail d, String mapName, String role, StringBuilder buf)
      throws IOException {
    if (d == null) {
      throw new RuntimeException("未找到技能");
    }
    buf.append(format(role)).append("/").append(d.getHotkey()).append(d.getName())
        .append("=");
    buf.append("==简介==").append("\\n");
    buf.append("{{技能").append("\\n");
    buf.append("|角色=").append(format(role)).append("\\n");
    buf.append("|ID=").append(d.getId()).append("\\n");
    buf.append("|键位=").append(format(d.getHotkey())).append("\\n");
    buf.append("|名称=").append(format(d.getName())).append("\\n");
    buf.append("|图片格式=").append("png").append("\\n");
    int sort = 0;
    switch (d.getHotkey()) {
      case "Q":
        sort = 1;
        break;
      case "W":
        sort = 2;
        break;
      case "E":
        sort = 3;
        break;
      case "R":
        sort = 4;
        break;
      case "T":
        sort = 5;
        break;
      default:
        break;
    }
    buf.append("|排序值=").append(sort).append("\\n");
    String desc = d.getUbertip();
    buf.append("|基本属性=").append(format(desc)).append("\\n");
    buf.append("}}\\n");

    buf.append("==备注==").append("\\n");
    if (d.getMark() != null && d.getMark().trim().length() > 0) {
      buf.append(format(format(d.getMark()))).append("\\n");
    }
    buf.append("\n");

    String fileName = "技能-" + role + "-" + d.getHotkey() + d.getName() + ".png";
    File f = new File("wikiimageabils\\" + fileName);
    if (!f.exists()) {
      ExcelImageInsert.convertImageToPng(d.getArt().replace(".tga", ".blp"), f);
    }
  }
}
