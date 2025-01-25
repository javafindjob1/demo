package com.b4;

import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "id", callSuper = false) // 添加此注解以明确指示不调用超类方法
public class Destructable extends Ini {
  private String id;// "frgd"
  /** 父类 */
  private String _parent;// "frgd"
  /** -- 名字 */
  private String Name;// = "恶魔之门"
  /** -- 生命值 */
  private String HP;// = 500.0
  
  /** 其余尚未分类的 */
  private Map<String, String> others;
}
