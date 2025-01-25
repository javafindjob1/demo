package com.abd.function;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.abd.ExcelImageInsert;
import com.alibaba.fastjson.annotation.JSONField;

import lombok.Data;

@Data
public class HeroData {
  private String unitId;
  /** 英雄面板 */
  private String name;
  private String propernames;
  /** 攻击力攻击范围攻击间隔 */
  private String attack;
  /** 护甲移动速度 */
  private String armor;
  /** 主要力量敏捷智力 */
  private String prop;
  /** 生命值蓝量 */
  private String hp;

  /** 皮 */
  private String pi;

  private String baseUnitId;

  /** 英雄介绍 */
  private String intro;

  private ViewData D2;
  private ViewData T2;

  private ViewData Q;
  private ViewData W;
  private ViewData E;
  private ViewData R;
  /** 天赋 或简介 */
  private ViewData D;
  private ViewData core;

  /** 大招 */
  private ViewData T;

  private ViewData p1;
  private ViewData p2;

  /** 物品 */
  private List<ViewData> items = new ArrayList<>();

  public void setIntro(String intro) {
    this.intro = intro;
  }

  public static String getIcon(ViewData v) {
    return v == null ? null : v.getIcon();
  }

  public String getItem(int i) {
    if (i < items.size()) {
      return getIcon(items.get(i));
    }
    return null;
  }

  public String[][] parseIconPath() {
    String[][] arr = new String[][] {
        { getIcon(p1), null, null, null, getItem(0), getItem(1) },
        { getIcon(core), getIcon(D2), null, getIcon(T2), getItem(2), getItem(3) },
        { getIcon(Q), getIcon(W), getIcon(E), getIcon(R), getItem(4), getItem(5) }
    };

    return arr;
  }

  @Data
  public static class ViewData {
    /** 图标 */
    @JSONField(serialize = false)
    private String icon;
    private String name;
    /** 描述 */
    private String desc;

    /** 归属与谁 */
    private String unitId;

    public void setIcon(String icon) {
      try {
        String newName = icon.replace(".blp", ".webp");
        this.icon = newName;
        File outFile = new File("html\\javafindjob1.github.io\\mp\\mp-imgs\\" + newName);
        if (!outFile.exists()) {
          ExcelImageInsert.convertImageToPng(icon, outFile);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    public void setDesc(String desc) {
      this.desc = this.name + "|n" + desc;
      this.name = null;
    }

    public static void initIcon() {
      String[] arr = new String[] {
          "ATTshuxing-str.blp",
          "ATTshuxing-agi.blp",
          "ATTshuxing-int.blp",
          "TYPElightdef.blp",
          "TYPEdarkdef.blp",
          "TYPEwaterdef.blp",
          "TYPEfiredef.blp",
          "TYPEstonedef.blp",
          "TYPEwinddef.blp",
          "TYPEwateratk.blp",
          "TYPEdarkatk.blp",
          "TYPElightatk.blp",
          "TYPEstoneatk.blp",
          "TYPEwindatk.blp",
          "TYPEfireatk.blp"
      };

      for (String icon : arr) {
        try {
          String newName = icon.replace(".blp", ".webp");
          File outFile = new File("html\\javafindjob1.github.io\\mp\\mp-imgs\\" + newName);
          if (!outFile.exists()) {
            ExcelImageInsert.convertImageToPng(icon, outFile);
          }
        } catch (Exception e) {
        }
      }
    }

    public static void main(String[] args) {
      String w3xliniPath = "D:\\war5-jass\\jass_plugin\\w3x2lni_zhCN_v2.5.2\\w3x2lni_zhCN_v2.5.2\\";
      String assetPath = w3xliniPath + "0mp\\4FFD4CA60115240BEFBD7D6278E38E2F\\";
      ExcelImageInsert.set(assetPath);
      initIcon();
    }
  }
}
