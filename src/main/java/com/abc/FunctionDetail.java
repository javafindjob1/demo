package com.abc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class FunctionDetail {
  private List<String> rows;
  private String name;

  // 商店是否可售
  private boolean RemoveItemFromStockBJ;
  private boolean GetRandomInt;
  private boolean AddItemToStockBJ;

  private boolean itemArr;
  /** 商店售出 */
  private List<Item> items = new ArrayList<>();
  private List<Item> specialItems = new ArrayList<>();
  private Double specialRate;

  // 固定爆
  private boolean GetUnitTypeId;
  private boolean CreateItemLoc;
  /** 固定爆 Map<unitid,List<Item>> */
  private Map<String, List<Item>> itemMap = new HashMap<>();
  
  /** 随机爆 Map<unitid,List<Item>> */
  private Map<String, List<Item>> itemLevelClassMap = new HashMap<>();

  @Data
  public static class Item {
    private String name;
    private String id;
    private Double rate;
    /** 难度描述 */
    private String desc;

    public Item(String id) {
      this.id = id;
    }

    @Override
    public int hashCode(){
      return this.id.hashCode();
    }

    @Override
    public boolean equals(Object o){
      if(o instanceof Item){
        Item item = (Item)o;
        return this.id.equals(item.id);
      }
      return false;
    }
  }

}
