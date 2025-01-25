package com.abc;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 添加此注解以明确指示不调用超类方法
 *
 * /** [Eidm]
 */
@Data
@EqualsAndHashCode(of = "id", callSuper = false)
public class Unit extends Ini {
  private String id;
  private String level;

  /** 名字 "黑暗舞者（恶魔形态）" */
  private String Name;
  /** 称谓 "路西昂" */
  private String Propernames;
  /** 生命最大值 120000 */
  private String HP;
  /** 魔法最大值 2000 */
  private String manaN;
  /** 英雄 - 主要属性 "STR" */
  private String Primary;
  /** 英雄 - 初始力量 25 */
  private String STR;
  /** 英雄 - 初始敏捷 */
  private String AGI;
  /** 英雄 - 初始智力 25 */
  private String INT;

  /** 图标 - 游戏界面 "ReplaceableTextures\\CommandButtons\\BTNEvilIllidan.blp" */
  private String Art;
  /** 模型 */
  private String file;

  /** 攻击 1 - 攻击类型 "hero" */
  private String atkType1;
  /** 攻击 1 - 基础伤害 3290 */
  private String dmgplus1;
  /** 伤害骰子3 基础攻击=dmgplus1+3 - dmgplus1+36 */
  private String dice1;
  /** 12面 */
  private String sides1;
  /** 攻击 1 - 攻击范围 450 */
  private String rangeN1;
  /** 主动攻击范围 850.0 */
  private String acquire;
  /** 攻击 1 - 攻击间隔 1.3 */
  private String cool1;

  /** 护甲类型 hero */
  private String defType;
  /** 基础护甲 20.0 */
  private String def;
  /** 基础速度 420 */
  private String spd;
  /** 视野范围(白天) 1800 */
  private String sight;
  /** 视野范围(夜晚) 1800 */
  private String nsight;

  /** 碰撞体积 */
  private String collision;

  /** 普通 "ACrk,ACsi" */
  private String abilList;
  /** 英雄 "" */
  private String heroAbilList;

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

  /** 图标 - 计分屏 "UI\\Glues\\ScoreScreen\\scorescreen-hero-demonhunter.blp" */
  private String ScoreScreenIcon;
  /** 单位类别 精英怪 "giant" */
  private String type;

  /** 英雄 - 每等级提升敏捷 0.0 */
  private String _parentAGIplus;
  /** 名字 - 编辑器后缀 */
  private String EditorSuffix;

  /** 攻击 1 - 武器类型 "msplash" */
  private String weapTp1;
  /** 攻击 1 - 中伤害参数 0.9 */
  private String Hfact1;
  /** 英雄 - 每等级提升智力 0.0 */
  private String INTplus;
  /** 攻击 1 - 射弹速率 1100 */
  private String Missilespeed_1;
  /** 攻击 1 - 小伤害范围 200 */
  private String Qarea1;
  /** 攻击 1 - 小伤害参数 0.75 */
  private String Qfact1;
  /** 英雄 - 每等级提升力量 0.0 */
  private String STRplus;

  /** 修理黄金消耗 15 */
  private String goldRep;
  /** 魔法初始数量 2000 */
  private String mana0;
  /** 模型缩放 1.38 */
  private String modelScale;
  /** 生命回复 600.0 */
  private String regenHP;
  /** 生命回复类型 "always" */
  private String regenType;

  /** 装甲类型 "Flesh" */
  private String armor;

  /** 攻击 1 - 目标允许 "debris,ground,structure,air,ward" */
  private String targs1;

  /** 种族 */
  private String race;

}
