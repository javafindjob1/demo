package com.b4;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class UnitDetail implements IDropTrigger,Serializable{
  private String id;
  private String name;
  private String level;

  /** 称谓 */
  private String propernames;// "路西昂"
  /** 生命最大值 */
  private String hp;// 120000
  /** 装甲类型 */
  private String armor;// = "Flesh"
  /** 护甲类型 hero */
  private String defType;
  /** 基础护甲 */
  private String def;// 20.0

  /** 减伤百分比 */
  private String damageReduce;// 20.0%

  /** 攻击 1 - 攻击类型 */
  private String atkType1;// = "hero"
  /** 攻击 1 - 基础伤害 */
  private String dmgplus1;// 3290
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
 
  /** 英雄 - 主要属性 INT AGI STR*/
  private String primary; // "INT"
  
  /** 单位售出 */
  private String sellitems;// = "I00B,I093,I00E,I00C,I00H,I00I,I08Z,I08Y"
  
  /** 物品掉落 */
  private List<FunctionDetail.DropInfo> dropString;

  private String mark;

  enum Primary {
    INT("INT"),
    AGI("AGI"),
    STR("STR");
    private String value;
    private Primary(String value){
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

}
