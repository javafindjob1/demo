package com.abc;

import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "id", callSuper = false) // 添加此注解以明确指示不调用超类方法
public class Item extends Ini {
  private String id;// "frgd"
  /** 界面图标 */
  private String Art;// "war3mapImported\\BTNItem_ShenShengGuangHuiLiFu.blp"
  /** 按钮位置(X) */
  private String Buttonpos_1;// 1
  /** 说明 */
  private String Description;// "|cffccffcc锻造材料|r|cffff9900|n|r|cffffcc00天心法袍|n能量宝石|n幻森之羽 x
                             // 10|n|r|cff99ccff|n|n护甲+120|n生命上限+5000|n智力+200|n降低英雄技能6%冷却时间|n被暗属性敌人攻击时，反弹智力x10的伤害|n|r|cff999999东正主教专属：|n智力+175|r|cff99ccff|n|r|cff99cc00等级：B+|n|r|cffff9900类型：衣服|n|n|r索多曼尼斯教堂大主教的华丽礼服，在光辉的外衣下藏着奇异的符文。"
  /** 生命值 */
  private String HP;// 5000
  /** 等级 */
  private String Level;// 6
  // 名字 */
  private String Name;// "|cffcc99ff神圣光辉礼服|r|cffccffcc合成卷轴|r"
  /** 提示工具 - 基础 */
  private String Tip;// "|cffcc99ff神圣光辉礼服|r|cffccffcc合成卷轴|r"
  /** 提示工具 - 扩展 */
  private String Ubertip;// "|cffccffcc锻造材料|r|cffff9900|n|r|cffffcc00天心法袍|n能量宝石|n幻森之羽 x
                         // 10|n|r|cff99ccff|n|n护甲+120|n生命上限+5000|n智力+200|n降低英雄技能6%冷却时间|n被暗属性敌人攻击时，反弹智力x10的伤害|n|r|cff999999东正主教专属：|n智力+175|r|cff99ccff|n|r|cff99cc00等级：B+|n|r|cffff9900类型：衣服|n|n|r索多曼尼斯教堂大主教的华丽礼服，在光辉的外衣下藏着奇异的符文。"
  /** 技能 */
  private String abilList;// "A0GJ"
  /** CD间隔组 */
  private String cooldownID;// "A0GJ"
  /** 使用模型 */
  private String file;// ""
  /** 黄金消耗 */
  private String goldcost;// 0
  /** 等级(旧版) */
  private String oldLevel;// 6
  /** 使用完会消失 */
  private String perishable;// 1
  /** 可作为随机物品 */
  private String pickRandom;// 1
  /** 捡取时自动使用 */
  private String powerup;// 1
  /** 优先权 */
  private String prio;// 365
  /** 购买时间间隔 */
  private String stockRegen;// 1
  /** 主动使用 */
  private String usable;// 1
  /** 使用次数 */
  private String uses;// 1
  /** 模型缩放 */
  private String scale;
  /** 购买开始时间 */
  private String stockStart;
  /** 可以被抵押 */
  private String pawnable;
  private String sellable;
  private String stockMax;
  private String drop;
  private String selSize;
  private String ignoreCD;
  private String droppable;
  private String colorR;
  private String colorG;
  private String colorB;
  private String Requires;
  private String lumbercost;
  private String morph;
  private String Buttonpos_2;
  private String Hotkey;


}