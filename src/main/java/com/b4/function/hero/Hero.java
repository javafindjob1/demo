package com.b4.function.hero;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.b4.AbilityDetail;
import com.b4.ItemDetail;
import com.b4.UnitDetail;

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
  /** Z技能补充 */
  private AbilityDetail Z;
  /** D技能补充 */
  private AbilityDetail D;
  /** F技能补充 */
  private AbilityDetail F;
  /** G技能补充 */
  private AbilityDetail G;

  
  /** 专属物品补充 */
  private List<ItemDetail> itemList = new ArrayList<>();

  private Map<String, AbilityDetail> abilityMap;

  public void addAblity(String ability){
    this.abilitys.add(ability);
  }

  public List<AbilityDetail> getAbilList(){
    List<AbilityDetail> abilList = new ArrayList<>();
    abilList.add(Z);
    abilList.add(D);
    abilList.add(F);
    abilList.add(G);
    abilList.add(Q);
    abilList.add(W);
    abilList.add(E);
    abilList.add(R);
    return abilList;
  }

  
}
