package com.b4;

import java.text.Collator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import com.b4.UnitDetail.Primary;
import com.b4.function.hero.Hero;

public class HeroParse {
  private List<Hero> res;

  public void wrapHero(Map<String, AbilityDetail> abilityMap, Map<String, UnitDetail> unitMap,
      Map<String, ItemDetail> idItemMap) {

    Map<String, List<ItemDetail>> heroItemMap = new HashMap<>();
    for (ItemDetail item : idItemMap.values()) {
      if (item.getHero() != null) {
        List<ItemDetail> items = MapUtil.getNotNull(heroItemMap, item.getHero(), ArrayList::new);
        items.add(item);
      }
    }

    Map<String, Hero> heroMap = new HashMap<>();
    for (Entry<String, UnitDetail> entry : unitMap.entrySet()) {
      UnitDetail unit = entry.getValue();
      if (unit.getAbilList() != null && unit.getAbilList().contains("A0YK")) {

        Hero hero = new Hero();
        hero.setAbilityMap(abilityMap);
        hero.setId(unit.getId());
        hero.setUnit(unit);
        heroMap.put(hero.getId(), hero);

        String[] abilList = unit.getAbilList().split(",");
        for (String abilId : abilList) {
          AbilityDetail abilityDetail = abilityMap.get(abilId);
          if (abilityDetail == null) {
            continue;
          }
          String hotkey = abilityDetail.getHotkey();
          if (hotkey == null) {
            continue;
          }
          switch (hotkey) {
            case "Q":
              hero.setQ(abilityDetail);
              break;
            case "W":
              hero.setW(abilityDetail);
              break;
            case "E":
              hero.setE(abilityDetail);
              break;
            case "R":
              hero.setR(abilityDetail);
              break;
            case "Z":
              hero.setZ(abilityDetail);
              break;
            case "D":
              hero.setD(abilityDetail);
              break;
            case "F":
              hero.setF(abilityDetail);
              break;
            case "G":
              hero.setG(abilityDetail);
              break;
            default:
              break;
          }
        }

        // 补充物品
        String name = UnitParse.trimName(unit.getPropernames());
        List<ItemDetail> itemList = heroItemMap.get(name);
        if (itemList != null) {
          Collator collator = Collator.getInstance(new Locale("zh", "CN"));
          itemList.sort((o1, o2) -> {
            int v1 = o1.getLevelInt() - o2.getLevelInt();
            return v1 != 0 ? v1 : collator.compare(o1.getName(), o2.getName());
          });
          hero.setItemList(itemList);
        }
        continue;
      }
    }

    List<Hero> res = new ArrayList<>();
    for (Entry<String, Hero> e : heroMap.entrySet()) {
      res.add(e.getValue());
    }

    this.res = res;
  }

  public List<Hero> getOrder(Primary primary) {
    List<Hero> res2 = new ArrayList<>();
    for (Hero e : res) {
      if (e.getUnit().getPrimary().equals(primary.getValue())) {
        res2.add(e);
      }
    }
    return res2;
  }

  public static void main(String[] args) {
    String a = "";
    System.out.println(a.split(",").length);
  }
}
