package com.abd;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import lombok.Data;

@Data
public class UnitDetail implements IDropTrigger, Serializable {
  private String id;
  private String name;
  private String nameReal;
  private String level;

  /** 称谓 */
  private String propernames;// "路西昂"
  /** 生命最大值 */
  private String hp;// 120000
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

  /** 减伤百分比 */
  private String damageReduce;// 20.0%

  /** 基础攻击力 基础攻击=dmgplus1+dice1+STR*2 - sides1*dice1 */
  private String attack;
  /** 攻击 1 - 攻击类型 */
  private String atkType1;// = "hero"
  private String atkType1Ori;
  /** 攻击 1 - 基础伤害 */
  private String dmgplus1;// 3290
  /** 伤害骰子 3 */
  private String dice1;
  /** 12面 */
  private String sides1;
  /** 攻击 1 - 攻击间隔 */
  private String cool1;// 1.3

  /** 攻击 1 - 攻击范围 */
  private String rangeN1;// = 450
  /** 主动攻击范围 */
  private String acquire;// 850.0

  /** 图标 - 游戏界面 */
  private String art;// = "ReplaceableTextures\\CommandButtons\\BTNEvilIllidan.blp"
  /** 图标 - 计分屏 */
  private String scoreScreenIcon;// = "UI\\Glues\\ScoreScreen\\scorescreen-hero-demonhunter.blp"
  /** 单位类别 精英怪 */
  private String type;// "giant"
  /** 单位附加值 111:男 222:女 */
  private String points;

  /** 模型 */
  private String file;

  /** 普通 */
  private String abilList;// = "AInv,A02Z"
  private String heroAbilList;// = "AInv,A02Z"
  /** 碰撞体积 */
  private String collision;
  /** 动画 - 魔法施放回复 */
  private String castbsw;// = 0.51
  /** 动画 - 魔法施放点 */
  private String castpt;// = 0.3
  /** 可以逃跑 */
  private String canFlee;// = 1
  /** 攻击 1 - 动画伤害点 */
  private String dmgpt1;// = 0.3
  /** 攻击 1 - 动画回复点 */
  private String backSw1;// = 0.6

  /** 单位售出 */
  private String sellitems;// = "I00B,I093,I00E,I00C,I00H,I00I,I08Z,I08Y"

  private String ubertip;
  /** 装甲类型 */
  private String armor;// = "Flesh"

  /** 物品掉落 */
  private List<FunctionDetail.DropInfo> dropString;

  private String mark;

  enum Primary {
    INT("INT"),
    AGI("AGI"),
    STR("STR");

    private String value;

    private Primary(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }
  }

  public UnitDetail() {
  }

  public UnitDetail(String id) {
    this.id = id;
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
    int value = Integer.parseInt(hp) + Integer.parseInt(baseProp) * 16;
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

  public String getDefReal() {
    BigDecimal decimal = new BigDecimal(Integer.parseInt(getAGI()) * 0.05);
    BigDecimal decimalNew = decimal.setScale(0, RoundingMode.HALF_UP);
    int a = decimalNew.intValueExact();
    return (Integer.parseInt(def) + a) + "";
  }

  public static void main(String[] args) {
    int i = 0;
    while (i++ < 1000) {
      // 创建 BigDecimal 对象
      BigDecimal num1 = new BigDecimal(Integer.parseInt("14") * 0.05);

      // 四舍五入
      BigDecimal roundedNum = num1.setScale(0, RoundingMode.HALF_UP);
      System.out.println("四舍五入结果: " + roundedNum.intValueExact());
      System.out.println("转换为整数: " + num1.intValue());
      System.out.println("转换为整数: " + num1);

      // 转换为双精度浮点数
      double doubleValue = num1.doubleValue();
      System.out.println("转换为双精度浮点数: " + doubleValue);
    }
  }

}
