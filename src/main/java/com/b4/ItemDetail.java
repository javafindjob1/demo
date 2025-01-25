package com.b4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class ItemDetail {
  public ItemDetail(String name) {
    this.name = name;
  }

  public static Map<String,Integer> typeMap = new HashMap<>();
  public static Map<String,Integer> levelMap = new HashMap<>();
  public static Map<String,Integer> nengliMap = new HashMap<>();

  /** 物品ID非合成卷轴 */
  private String id;
  /** 物品名称 */
  private String name;
  /** 物品等级 */
  private String level;

  private int levelInt;
  /** 攻击力:12000|n筋力:300|n敏捷:300|n生命:20000 */
  private int attack;
  private int str;
  private int agi;
  private int itn;
  private int hp;
  private int def;
  private int miss;
  private String nengli;
  /** 掉落物品 */
  private List<String> dropList = new ArrayList<>();

  /** 可作为随机物品 1 */
  private String pickRandom;
  /** 物品等级分类 random物品用到 */
  private String levelClass;

  /** 物品类型 */
  private String type;

  /** 物品图标 */
  private String icon;
  /** 描述 */
  private String description;

  /** 掉落地点 */
  private String dropPlace;
  
  /** 锻造材料 */
  private String synthesisFormula;
  /** 合成卷轴 */
  private String juanzhouId;
  /** 卷轴是谁合的 */
  private String unitId;

  /** 英雄专属效果 */
  private String heroExclusive;
  /** 英雄名称 */
  private String hero;
  
  private String mark; 
  
  /** 更新记录里设置跳转链接时用到 */
  private int rowNum;

  @Data
  public static class UnitDrop{
    private IDropTrigger dropTrigger;
    private FunctionDetail.DropInfo dropInfo;
  }

  @Data
  public static class ShopDrop{
    private FunctionDetail.DropInfo item;
    private Double dropRate;
  }
}
