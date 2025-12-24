package com.xi4.function;

import static org.junit.Assert.assertNotNull;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.xi4.parse.AbilityDetail;
import com.xi4.parse.ItemDetail;
import com.xi4.parse.UnitDetail;
import com.xi4.parse.UnitParse;

import lombok.Data;

@Data
public class Hero {
  /** 单位ID */
  private String id;

  // 111=男 222=女
  private String point;

  /** 主要属性 */
  private String mainPropDesc;

  /** 单位信息补充 */
  private UnitDetail unit;
  /** 是否是皮 */
  private String pi = "";

  /** 旅行者简介 */
  private AbilityDetail intro;
  /** 专属剧情 */
  private AbilityDetail story;
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

  /** 显示跳转位置 */
  private String sheetName;
  private int rowNum;

  public Hero() {
  }

  public Hero(UnitDetail unit, Map<String, AbilityDetail> abilityMap, Map<String, List<ItemDetail>> heroItemMap) {
    this.unit = unit;
    this.id = unit.getId();
    this.point = unit.getPoints();
    this.abilityMap = abilityMap;

    // 补充技能
    addAbilitys(unit.getHeroAbilList().split(","));

    // 补充物品
    addItems(heroItemMap);
  }

  public void addItems(Map<String, List<ItemDetail>> heroItemMap) {
    String name = UnitParse.trimName(unit.getPropernames());
    List<ItemDetail> itemList = heroItemMap.get(name);
    if (itemList == null) {
      itemList = heroItemMap.get(UnitParse.trimName(unit.getName()));
    }
    if (itemList != null) {
      Collator collator = Collator.getInstance(new Locale("zh", "CN"));
      itemList.sort((o1, o2) -> {
        int v1 = o1.getLevelInt() - o2.getLevelInt();
        return v1 != 0 ? v1 : collator.compare(o1.getName(), o2.getName());
      });
      this.setItemList(itemList);
    }
  }

  public void addAbilitys(String... abilityStrings) {
    addAbilitys(Arrays.asList(abilityStrings));
  }

  public void addAbilitys(List<String> abilityStrings) {
    for (String ability : abilityStrings) {
      addAbility(ability, null);
    }
  }

  public void addAbility(String ability, String hotKey) {
    AbilityDetail abilityDetail = abilityMap.get(ability);
    if (abilityDetail == null) {
      return;
    }
    if (hotKey != null) {
      abilityDetail.setHotkey(hotKey);
    }
    if (abilityDetail.getName().contains("旅行者简介")) {
      this.setIntro(abilityDetail);
    } else {
      assertNotNull("技能热键不能为空:" + ability, abilityDetail.getHotkey());
      switch (abilityDetail.getHotkey()) {
        case "Q":
          this.setQ(abilityDetail);
          break;
        case "W":
          this.setW(abilityDetail);
          break;
        case "E":
          this.setE(abilityDetail);
          break;
        case "R":
          this.setR(abilityDetail);
          break;
        case "T":
          this.setT(abilityDetail);
          break;
        default:
          break;
      }
    }

  }

  public List<AbilityDetail> getAbilList() {
    List<AbilityDetail> abilList = new ArrayList<>();
    if (intro != null)
      abilList.add(intro);

    if (Q != null)
      abilList.add(Q);
    if (W != null)
      abilList.add(W);
    if (E != null)
      abilList.add(E);
    if (R != null)
      abilList.add(R);
    // 缪斯大招进化3技能
    if (id.equals("H00U")) {
      abilList.add(abilityMap.get("A11Z"));
    }
    if (T != null)
      abilList.add(T);
    if (core != null)
      abilList.add(core);

    /** 专属剧情 */
    if (story != null)
      abilList.add(story);
    return abilList;
  }

}
