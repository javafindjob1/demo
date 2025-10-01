package com.common.parse;

import lombok.Data;

@Data
public class DropInfo {
  private String itemId;
  private String itemName;
  private Double rate;
  // 只爆一次还是其他
  private String desc;

  public DropInfo(String id) {
    this.itemId = id;
  }

  @Override
  public int hashCode(){
    return this.itemId.hashCode();
  }

  @Override
  public boolean equals(Object o){
    if(o instanceof DropInfo){
      DropInfo item = (DropInfo)o;
      return this.itemId.equals(item.itemId);
    }
    return false;
  }
}