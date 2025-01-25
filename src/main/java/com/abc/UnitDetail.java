package com.abc;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import lombok.Data;

@Data
public class UnitDetail {
  private String id;
  /** 金色魔王 */
  private String name;
  private String nameReal;
  private String level;

  /** 称谓 "路西昂" */
  private String propernames;
  /** 生命最大值 120000 */
  private String hp;
  /** 计算后的生命值 */
  private String hpReal;
  /** 魔法最大值 2000 */
  private String manaN;
  /** 计算后的魔法值 */
  private String manaNReal;
  /** 英雄 - 主要属性 "STR" */
  private String primary;
  /** 英雄 - 初始力量 25 */
  private String STR;
  /** 英雄 - 初始敏捷 */
  private String AGI;
  /** 英雄 - 初始智力 25 */
  private String INT;

  /** 护甲类型 hero "small" */
  private String defType;
  private String defTypeOri;
  /** 基础护甲 20.0 */
  private String def;
  /** 基础速度 420 */
  private String spd;
  /** 视野范围(白天) 1800 */
  private String sight;
  /** 视野范围(夜晚) 1800 */
  private String nsight;

  /** 计算后减伤百分比 20.0% */
  private String damageReduce;

  /** 基础攻击力 基础攻击=dmgplus1+dice1+STR*2 - sides1*dice1 */
  private String attack;
  /** 攻击 1 - 攻击类型 "hero" */
  private String atkType1;
  private String atkType1Ori;
  /** 攻击 1 - 基础伤害 3290 */
  private String dmgplus1;
  /** 伤害骰子 3 */
  private String dice1;
  /** 12面 */
  private String sides1;
  /** 攻击 1 - 攻击间隔 1.3 */
  private String cool1;

  /** 攻击 1 - 攻击范围 450 */
  private String rangeN1;
  /** 主动攻击范围 850.0 */
  private String acquire;

  /** 图标 - 游戏界面 "ReplaceableTextures\\CommandButtons\\BTNEvilIllidan.blp" */
  private String art;
  /** 图标 - 计分屏 "UI\\Glues\\ScoreScreen\\scorescreen-hero-demonhunter.blp" */
  private String scoreScreenIcon;
  /** 单位类别 精英怪 "giant" */
  private String type;

  /** 模型文件相对路径 */
  private String file;

  /** 普通 "AInv,A02Z" */
  private String abilList;
  /** 碰撞体积 */
  private String collision;
  /** 动画 - 魔法施放回复 0.51 */
  private String castbsw;
  /** 动画 - 魔法施放点 0.3 */
  private String castpt;
  /** 可以逃跑 1 */
  private String canFlee;
  /** 攻击 1 - 动画伤害点 0.3 */
  private String dmgpt1;
  /** 攻击 1 - 动画回复点 0.6 */
  private String backSw1;

  /** 种族 */
  private String race;
  /** 英雄技能 */
  private String heroAbilList;

  /** 物品掉落 */
  private List<FunctionDetail.Item> dropString;

  /** 物品被单位掉落时的概率 */
  private Double dropRate;

  public UnitDetail() {
  }

  public UnitDetail(String id) {
    this.id = id;
  }

  public String getAttack() {
    String baseProp;
    switch (primary) {
      case "STR":
        baseProp = STR;
        break;
      case "AGI":
        baseProp = AGI;
        break;
      case "INT":
        baseProp = INT;
        break;
      default:
        baseProp = "0";
        break;
    }
    int low = Integer.parseInt(dmgplus1) + Integer.parseInt(baseProp) * 2 + Integer.parseInt(dice1);
    int high = Integer.parseInt(dmgplus1) + Integer.parseInt(baseProp) * 2
        + Integer.parseInt(dice1) * Integer.parseInt(sides1);
    return low + " - " + high;
  }

  public String getHpReal() {
    String baseProp;
    switch (primary) {
      case "STR":
        baseProp = STR;
        break;
      case "AGI":
        baseProp = AGI;
        break;
      case "INT":
        baseProp = INT;
        break;
      default:
        baseProp = "0";
        break;
    }
    int value = Integer.parseInt(hp) + Integer.parseInt(baseProp) * 17;
    return value + "";
  }

  public int getPrimaryInt() {
    int baseProp;
    switch (primary) {
      case "STR":
        baseProp = 0;
        break;
      case "AGI":
        baseProp = 1;
        break;
      case "INT":
        baseProp = 2;
        break;
      default:
        baseProp = 0;
        break;
    }
    return baseProp;
  }

  public String getManaNReal() {
    String baseProp;
    switch (primary) {
      case "STR":
        baseProp = STR;
        break;
      case "AGI":
        baseProp = AGI;
        break;
      case "INT":
        baseProp = INT;
        break;
      default:
        baseProp = "0";
        break;
    }

    int value = Integer.parseInt(manaN) + Integer.parseInt(baseProp) * 11;
    return value + "";
  }

  enum Primary {
    STR("STR"),
    AGI("AGI"),
    INT("INT"),
    ;

    private String value;

    private Primary(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }
  }

  @Override
  public int hashCode() {
    return this.id.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof UnitDetail) {
      UnitDetail item = (UnitDetail) o;
      return this.id.equals(item.id);
    }
    return false;
  }

  public String getDefReal() {
    BigDecimal decimal = new BigDecimal(Integer.parseInt(getAGI()) * 0.05);
    BigDecimal decimalNew = decimal.setScale(0, RoundingMode.HALF_UP);
    int a = decimalNew.intValueExact();
    return (Integer.parseInt(def) + a) + "";
  }

}
