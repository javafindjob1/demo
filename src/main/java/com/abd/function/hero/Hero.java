package com.abd.function.hero;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.abd.Ability;
import com.abd.AbilityDetail;
import com.abd.ItemDetail;
import com.abd.UnitDetail;

import lombok.Data;

@Data
public class Hero {
  /** 单位ID */
  private String id;
  
  private Set<String> abilitys = new HashSet<>();

  // 111=男 222=女
  private String point;
  
  /** 主要属性 */
  private String mainPropDesc;
  
  /** 单位信息补充 */
  private UnitDetail unit;
  /** 是否是皮 */
  private String pi="";

  /** 旅行者简介 */
  private AbilityDetail intro;
  /** Q技能补充 */
  private AbilityDetail Q;
  /** W技能补充 */
  private AbilityDetail W;
  /** E技能补充 */
  private AbilityDetail E;
  /** R技能补充 */
  private AbilityDetail R;
  /** T技能补充 */
  private AbilityDetail T;

  /** 魔核技能补充 */
  private AbilityDetail core;
  
  /** 专属物品补充 */
  private List<ItemDetail> itemList = new ArrayList<>();

  private Map<String, AbilityDetail> abilityMap;

  public void addAblity(String ability){
    this.abilitys.add(ability);
  }

  public List<AbilityDetail> getAbilList(){
    List<AbilityDetail> abilList = new ArrayList<>();
    if (intro!=null) abilList.add(intro);
    if (Q!=null) abilList.add(Q);
    if (W!=null) abilList.add(W);
    if (E!=null) abilList.add(E);
    if (R!=null) abilList.add(R);
    // 缪斯大招进化3技能
    if(id.equals("H00U")){
      abilList.add(abilityMap.get("A11Z"));
    }
    if (T!=null) abilList.add(T);
    if (core!=null) abilList.add(core);
    return abilList;
  }

  
}
