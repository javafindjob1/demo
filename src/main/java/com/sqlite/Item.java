package com.sqlite;

import lombok.Data;

@Data
public class Item {
  /** 'id' */
  private String id; 
  /** 名称 */
  private String name; 
  /** '类型' */
  private String type; 
  /** '属性' */
  private String prop;
   /** '获取途径 */
  private String dropplace; 
  /** '神秘商店是否出售' */
  private String shop;
   /** '备注' */
  private String remark; 
}
