package com.abd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.abd.function.FunctionTaozhuang;
import com.abd.function.hero.Hero;

import lombok.Data;

@Data
public class FunctionDetail {
  private List<String> rows;
  private String name;

  private Map<String, List<DropInfo>> itemMap = new HashMap<>();
  private List<DropInfo> waJueList = new ArrayList<>();
  private Map<String, List<DropInfo>> juanzhouMap = new HashMap<>();
  private List<FunctionTaozhuang> taozhuangList = new ArrayList<>();
  private Map<String, List<ItemAccessories>> newItemFormulaMap = new HashMap<>();

  private List<Hero> heroList = new ArrayList<>();

  @Data
  public static class DropInfo {
    private String itemName;
    private String itemId;
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

  /**
   * 物品合成
   */
  @Data
  public static class ItemAccessories {
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
      if(o instanceof DropInfo){
        DropInfo item = (DropInfo)o;
        return this.itemId.equals(item.itemId);
      }
      return false;
    }
  }

}
