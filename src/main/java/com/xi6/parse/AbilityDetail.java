package com.xi6.parse;

import lombok.Data;

@Data
public class AbilityDetail {
  /** 主键 */
  private String id;
  /** 技能名称 */
  private String name;
  /** 满技能详情 */
  private String ubertip;
  /** 热键 - 普通 QWERT */
  private String hotkey;
  // 技能按钮位置 x位置0-3
  private String buttonpos1;
  // 技能按钮位置 y位置0-2
  private String buttonpos2;
  /** 技能图标 */
  private String art;

  /** 备注 */
  private String mark;

}
