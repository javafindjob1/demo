package com.abc;

import lombok.Data;

@Data
public class ItemDetail {
  public ItemDetail(String name) {
    this.name = name;
  }

  /** 物品ID非合成卷轴 */
  private String id;
  /** 物品名称 */
  private String name;
  /** 物品等级 */
  private String level;

  private int levelInt;

  /** 可作为随机物品 1 */
  private String pickRandom;
  /** 物品等级分类 random物品用到 */
  private String levelClass;

  /** 物品类型 */
  private String type;

  /** 商店是否出售 */
  private String shop;

  /** 物品图标 */
  private String icon;
  /** 描述 */
  private String description;

  /** 掉落地点 */
  private String dropPlace;
  /** 锻造材料 */
  private String synthesisFormula;
  /** 卷轴ID */
  private String juanzhouId;
  /** only for hero exclusive items */
  private String heroExclusive;
  /** 英雄名称 */
  private String hero;

  private String mark;

  
  /** 对比文件差异时用到 */
  private String shop2;
  /** 更新记录里设置跳转链接时用到 */
  private int rowNum;

  @Data
  public static class UnitDrop{
    private UnitDetail unitDetail;
    private Double dropRate;
    private String desc;
  }

  @Data
  public static class ShopDrop{
    private FunctionDetail.Item item;
    private Double dropRate;
  }
}
