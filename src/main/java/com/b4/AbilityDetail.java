package com.b4;

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
  /** 技能图标 */
  private String art;

  /** CD */
  private String cool;

}
