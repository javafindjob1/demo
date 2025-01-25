package com.b4;


import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "id", callSuper = false) // 添加此注解以明确指示不调用超类方法
public class Ability extends Ini {
  /** id */
  private String id;
  /** 效果 - 施法动作 */
  private String Animnames; // "attack,slam"
  /** 影响区域 */
  private String Area; // 150.0
  /** 图标 - 普通 */
  private String Art; // "ReplaceableTextures\\CommandButtons\\BTNShockWave.blp"
  /** 魔法效果 */
  private String BuffID; // "BOsh"
  /** 按钮位置 - 普通 (X) */
  private String Buttonpos_1; // 0
  /** 按钮位置 - 普通 (Y) */
  private String Buttonpos_2; // 2
  /** 魔法施放时间 */
  private String Cast; // 0.0
  /** 效果 - 施法者 - 附加数量 */
  private String Casterattachcount; // 0
  /** 魔法施放时间间隔 */
  private String Cool; // 8.0
  /** 魔法消耗 */
  private String Cost; // 100
  /** 伤害 */
  private String DataA; // 75.0
  /** 最大伤害 */
  private String DataB; // 99999.0
  /** 距离 */
  private String DataC; // 800.0
  /** 最终区域范围 */
  private String DataD; // 150.0
  /** 持续时间 - 普通 */
  private String Dur; // 0.0
  /** 编辑器后缀 */
  private String EditorSuffix; // "(中立但是带有敌意)"
  /** 区域持续效果 */
  private String EfctID; // ""
  /** 持续时间 - 英雄 */
  private String HeroDur; // 0.0
  /** 热键 - 普通 */
  private String Hotkey; // "W"
  /** 效果 - 射弹自导允许 */
  private String MissileHoming; // 0
  /** 效果 - 射弹弧度 */
  private String Missilearc; // 0.0
  /** 效果 - 投射物图像 */
  private String Missileart; // "Abilities\\Spells\\Orc\\Shockwave\\ShockwaveMissile.mdl"
  /** 效果 - 射弹速度 */
  private String Missilespeed; // 1050
  /** 名字 */
  private String Name; // "震荡波"
  /** 命令串 - 使用/打开 */
  private String Order; // "shockwave"
  /** 按钮位置 - 研究 (X) */
  private String Researchbuttonpos_1; // 0
  /** 按钮位置 - 研究 (Y) */
  private String Researchbuttonpos_2; // 0
  /** 施法距离 */
  private String Rng; // 700.0
  /** 效果 - 目标 - 附加数量 */
  private String Targetattachcount; // 0
  /** 提示工具 - 普通 */
  private String Tip; // "震荡波(|cffffcc00W|r)"
  /** 提示工具 - 普通 - 扩展 有可能是json格式字符串 */
  private String Ubertip; // "一道强劲的震荡波能对一直线上的敌人造成<ACsh,DataA1>点的伤害。"
  /** 按钮位置 - 关闭 (X) */
  private String UnButtonpos_1; // 0
  /** 按钮位置 - 关闭 (Y) */
  private String UnButtonpos_2; // 0
  /** 检查等价所属 */
  private String checkDep; // 0
  /** 英雄技能 */
  private String hero; // 0
  /** 物品技能 */
  private String item; // 0
  /** 跳级要求 */
  private String levelSkip; // 0
  /** 等级 */
  private String levels; // 1
  /** 魔法偷取优先权 */
  private String priority; // 0
  /** 种族 */
  private String race; // "creeps"
  /** 等级要求 */
  private String reqLevel; // 0
  /** 目标允许 */
  private String targs; // "ground,structure,enemy"
  
}
