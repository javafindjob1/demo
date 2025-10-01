package com.common.parse;

import lombok.Data;

/**
 * 物品合成
 */
@Data
public class ItemAccessories {
  private String itemId;
  private Integer num;

  public ItemAccessories(String id, Integer num) {
    this.itemId = id;
    this.num = num;
  }

  @Override
  public int hashCode(){
    return this.itemId.hashCode();
  }

  @Override
  public boolean equals(Object o){
    if(o instanceof ItemAccessories){
      ItemAccessories item = (ItemAccessories)o;
      return this.itemId.equals(item.getItemId());
    }
    return false;
  }
}