package com.abc.fun;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import com.abc.ExcelImageInsert;
import com.alibaba.fastjson.annotation.JSONField;

import lombok.Data;

@Data
public class Hero {
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

  private String baseUnitId;

  /** 英雄介绍 */
  private String intro;
  private ViewData Q;
  private ViewData W;
  private ViewData E;
  private ViewData R;
  /** 天赋 */
  private ViewData D;
  /** 大招 */
  private ViewData T;

  private ViewData p1;
  private ViewData p2;

  /** 物品 */
  private List<ViewData> items = new ArrayList<>();

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
        { getIcon(p1), getIcon(p2), null, null, getItem(0), getItem(1) },
        { null, null, getIcon(T), getIcon(D), getItem(2), getItem(3) },
        { getIcon(Q), getIcon(W), getIcon(E), getIcon(R), getItem(4), getItem(5) }
    };

    return arr;
  }

  public void setIntro(String intro) {
    this.intro = intro;
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
        File outFile = new File("html\\javafindjob1.github.io\\x7\\x7-imgs\\" + newName);
        if (!outFile.exists()) {
          ExcelImageInsert.convertBlpToWebp(icon, outFile);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    public static void initSkin() {
      String[] arr = new String[] {
          // "war3mapImported\\tYPEDeFend_Light.blp",
          // "war3mapImported\\tYPEDefDark.blp",
          // "war3mapImported\\tYPEDefend_Land.blp",
          // "war3mapImported\\tYPESystemPic_Defend_Wind.blp",
          // "war3mapImported\\tYPEDeFend_Water.blp",
          // "war3mapImported\\tYPEDeFend_Fire.blp",
          // "war3mapImported\\tYPEAttack_Fire.blp",
          // "war3mapImported\\tYPESystemPic_Attack_Dark.blp",
          // "war3mapImported\\tYPEAttack_Light.blp",
          // "war3mapImported\\tYPEAttack_Water.blp",
          // "war3mapImported\\tYPEAttack2_Feng.blp",
          // "war3mapImported\\tYPEAttackLand.blp",

          "UI\\Widgets\\Console\\Human\\infocard-heroattributes-str.blp",
          "UI\\Widgets\\Console\\Human\\infocard-heroattributes-agi.blp",
          "UI\\Widgets\\Console\\Human\\infocard-heroattributes-int.blp",
      };

      for (String icon : arr) {
        try {
          String newName = icon.replace(".blp", ".webp");
          File outFile = new File("html\\javafindjob1.github.io\\x7\\x7-imgs\\" + newName);
          if (!outFile.exists()) {
            com.abd.ExcelImageInsert.convertImageToPng(icon, outFile);
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }

    public static void main(String[] args) {
      String assetPath = "D:\\war5-jass\\jass_plugin\\w3x2lni_zhCN_v2.5.2\\w3x2lni_zhCN_v2.5.2\\";
      assetPath += "0x7\\15DDBC59336E305090A69142D57D2C0C\\";
      ExcelImageInsert.set(assetPath);
      initSkin();
    }

    public void setDesc(String desc) {
      this.desc = this.name + "|n" + desc;
      this.name = null;
    }

  }

}
