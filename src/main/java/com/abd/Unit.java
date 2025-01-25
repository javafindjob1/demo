package com.abd;

import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "id", callSuper = false) // 添加此注解以明确指示不调用超类方法
public class Unit extends Ini {
  /** [Eidm] */
  private String id;
  private String _parent;
  private String level;// 120000

  /** 名字 */
  private String Name;// "黑暗舞者（恶魔形态）"
  /** 称谓 */
  private String Propernames;// "路西昂"
  /** 生命最大值 */
  private String HP;// 120000
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

  /** 攻击 1 - 攻击类型 */
  private String atkType1;// = "hero"
  /** 攻击 1 - 基础伤害 */
  private String dmgplus1;// 3290
  /** 伤害骰子3 基础攻击=dmgplus1+3 - dmgplus1+36 */
  private String dice1;
  /** 12面 */
  private String sides1;
  /** 攻击 1 - 攻击范围 450 */
  private String rangeN1;
  /** 主动攻击范围 850.0 */
  private String acquire;
  /** 攻击 1 - 攻击间隔 */
  private String cool1;// 1.3

  /** 护甲类型 hero */
  private String defType;
  /** 基础护甲 */
  private String def;// 20.0
  /** 基础速度 420 */
  private String spd;
  /** 视野范围(白天) 1800 */
  private String sight;

  /** 碰撞体积 */
  private String collision;

  /** 普通 */
  private String abilList;// "ACrk,ACsi"
  /** 英雄 */
  private String heroAbilList;// ""

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
  /** 图标 - 计分屏 */
  private String ScoreScreenIcon;// = "UI\\Glues\\ScoreScreen\\scorescreen-hero-demonhunter.blp"
  /** 单位类别 精英怪 */
  private String type;// "giant"

  /** 英雄 - 每等级提升敏捷 */
  private String _parentAGIplus;// 0.0
  /** 名字 - 编辑器后缀 */
  private String EditorSuffix;// ""

  /** 攻击 1 - 中伤害参数 */
  private String Hfact1;// 0.9
  /** 英雄 - 每等级提升智力 */
  private String INTplus;// 0.0
  /** 攻击 1 - 射弹速率 */
  private String Missilespeed_1;// 1100
  /** 攻击 1 - 小伤害范围 */
  private String Qarea1;// 200
  /** 攻击 1 - 小伤害参数 */
  private String Qfact1;// 0.75
  /** 英雄 - 每等级提升力量 */
  private String STRplus;// 0.0

  /** 修理黄金消耗 */
  private String goldRep;// 15
  /** 魔法初始数量 */
  private String mana0;// 2000
  /** 模型缩放 */
  private String modelScale;// 1.38
  /** 视野范围(夜晚) */
  private String nsight;// 1800
  /** 生命回复 */
  private String regenHP;// 600.0
  /** 生命回复类型 */
  private String regenType;// "always"
  /** 攻击 1 - 目标允许 */
  private String targs1;// "debris,ground,structure,air,ward"
  /** 攻击 1 - 武器类型 */
  private String weapTp1;// "msplash"

  /** 单位附加值 111:男 222:女 */
  private String points;

  /** 装甲类型 */
  private String armor;// = "Flesh"
  /** 英雄皮肤 - 描述 */
  private String Ubertip;
  /** 单位售出 */
  private String Sellitems;// = "I00B,I093,I00E,I00C,I00H,I00I,I08Z,I08Y"

}
