package com.abd;


import lombok.Data;

@Data
public class DestructableDetail implements IDropTrigger {
  private String id;// "frgd"
  /** -- 名字 */
  private String name;// = "恶魔之门"
  /** -- 生命值 */
  private String Hp;// = 500.0
  
}
