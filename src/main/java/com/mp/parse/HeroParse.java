package com.mp.parse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.common.ini.HeroGroup;
import com.common.parse.AbstractParse;
import com.common.parse.Function;
import com.common.util.MapUtil;
import com.common.util.ObjectUtil;
import com.mp.function.HeroData;
import com.mp.function.HeroData.ViewData;
import com.mp.function.hero.Hero;
import com.mp.parse.UnitDetail.Primary;

import lombok.Data;

@Data
public class HeroParse extends AbstractParse {
  private Map<String, Hero[]> res = new LinkedHashMap<>();

  public void wrapHero(Map<String, AbilityDetail> abilityMap, Map<String, UnitDetail> unitMap,
      Map<String, ItemDetail> idItemMap, HeroGroup heroGroup) {

    Map<String, List<ItemDetail>> heroItemMap = new HashMap<>();
    for (ItemDetail item : idItemMap.values()) {
      if (item.getHero() != null) {
        List<ItemDetail> items = MapUtil.getNotNull(heroItemMap, item.getHero(), ArrayList::new);
        items.add(item);
      }
    }

    Map<String, UnitDetail> unitHeroMap = new HashMap<>();
    for (UnitDetail unit : unitMap.values()) {
      if ("giant".equals(unit.getType()) && ("111".equals(unit.getPoints()) || "222".equals(unit.getPoints()))) {
        unitHeroMap.put(unit.getId(), unit);
      }
    }

    Map<String, Hero> heroMap = new HashMap<>();
    for (UnitDetail unit : unitHeroMap.values()) {
      Hero hero = new Hero(unit, abilityMap, heroItemMap);
      heroMap.put(hero.getId(), hero);
    }

    for (Hero hero : heroMap.values()) {
      String id = hero.getId();
      String baseUserId = heroGroup.getTeamId(id);
      if (baseUserId == null) {
        System.out.println("未找到英雄组ID " + id);
        continue;
      }
      if (!baseUserId.equals(id)) {
        hero.setPi("皮");
      }

      String[] ulti = heroGroup.getUlti(id);
      try{
        hero.addAbilitys(ulti);
      }catch(Error e){
        System.out.println("英雄组ID " + id + " 技能组错误");
      }

      String intro = heroGroup.getIntro(id);
      AbilityDetail introAbi = abilityMap.get("A0BC");
      AbilityDetail introAbiCopy = new AbilityDetail();
      introAbiCopy.setId(introAbi.getId());
      introAbiCopy.setName(introAbi.getName());
      introAbiCopy.setArt(introAbi.getArt());
      introAbiCopy.setUbertip(intro);
      hero.setIntro(introAbiCopy);

      String magiccore = heroGroup.getMagic(id);
      AbilityDetail mohe = abilityMap.get("A164");
      AbilityDetail moheCopy = new AbilityDetail();
      moheCopy.setId(mohe.getId());
      moheCopy.setName("凝空光晶");
      moheCopy.setArt(mohe.getArt());
      moheCopy.setUbertip(magiccore);
      hero.setCore(moheCopy);
    }

    Map<String, String[]> udgHeroTeam = heroGroup.getHeros();
    Map<String, Hero[]> res = new LinkedHashMap<>();
    for (Entry<String, String[]> e : udgHeroTeam.entrySet()) {
      String teamId = e.getKey();
      String[] team = e.getValue();
      Hero[] heroTeam = new Hero[2];

      for (int i = 0; i < team.length; i++) {
        heroTeam[i] = heroMap.get(team[i]);
      }
      res.put(teamId, heroTeam);
    }

    this.res = res;
  }

  public Map<String, HeroData> wrapHeroData(Map<String, AbilityDetail> abilityMap, Map<String, UnitDetail> unitMap,
      Map<String, ItemDetail> idItemMap, List<Function> funList) {
    // Map<String, Hero[]> res

    Map<String, String> pMap = new HashMap<>();
    pMap.put("E008", "O00Q");
    pMap.put("O000", "O00S");
    pMap.put("H006", "O00E");
    pMap.put("N06G", "O00G");
    pMap.put("O001", "O00I");
    pMap.put("O004", "O00K");
    pMap.put("H007", "O00L");
    pMap.put("N02D", "O00T");
    pMap.put("H005", "O010");
    pMap.put("E015", "O00Z");
    pMap.put("E016", "O00U");
    pMap.put("H00H", "O011");

    Map<String, HeroData> heroMap = new HashMap<>();
    for (Entry<String, Hero[]> herosEntry : this.res.entrySet()) {
      String baseUnitId = herosEntry.getKey();

      Hero[] heros = herosEntry.getValue();
      HeroData hero0 = new HeroData(heros[0]);
      heroMap.put(hero0.getUnitId(), hero0);
      hero0.setBaseUnitId(baseUnitId);

      if (heros[1] != null) {
        HeroData hero1 = new HeroData(heros[1]);
        heroMap.put(hero1.getUnitId(), hero1);
        hero1.setBaseUnitId(baseUnitId);

        String p1 = pMap.get(baseUnitId);
        UnitDetail pfu = unitMap.get(p1);

        if (pfu == null) {
          System.out.println("未找到皮肤说明:" + baseUnitId);
        }

        // 原皮内看到的皮肤信息
        {
          ViewData viewData = new ViewData();
          viewData.setName(hero1.getName() + "皮肤");
          viewData.setIcon(pfu.getArt());
          viewData.setDesc(pfu.getUbertip());
          viewData.setUnitId(hero1.getUnitId());
          hero0.setP1(viewData);
        }
        // 皮内看到的皮肤信息
        {
          ViewData viewData = new ViewData();
          viewData.setName(hero1.getName() + "皮肤");
          viewData.setIcon(pfu.getArt());
          viewData.setDesc(pfu.getUbertip());
          viewData.setUnitId(hero1.getUnitId());
          hero1.setP1(viewData);
          // 专属物品给到皮一份
          hero1.setItems(ObjectUtil.deepCopy(hero0.getItems()));
        }

        // 人鱼不移除装备
        if (!hero0.getUnitId().equals("H00H")) {
          // 原皮移除一件专属装备，
          hero0.getItems().remove(hero0.getItems().size() - 1);
          // "E008" 多移除一件 蓓雷德
          if (hero0.getUnitId().equals("E008")) {
            hero0.getItems().remove(hero0.getItems().size() - 1);
          }

        }

      }

      // 缪斯单独处理
      if (hero0.getUnitId().equals("H00U")) {
        {
          // 原皮内看到的皮肤信息
          ViewData viewData = new ViewData();
          viewData.setName(hero0.getName() + "进化");
          UnitDetail ms = unitMap.get("H00U");
          AbilityDetail tianlai = abilityMap.get("A11Z");
          viewData.setIcon(tianlai.getArt());
          viewData.setDesc(tianlai.getUbertip());
          viewData.setUnitId(hero0.getUnitId());
          hero0.setP1(viewData);
        }
      }

    }
    return heroMap;
  }

  public Map<String, Hero[]> getOrder(Primary primary) {
    Map<String, Hero[]> res2 = new LinkedHashMap<>();
    for (Entry<String, Hero[]> e : res.entrySet()) {
      if (e.getValue()[0].getUnit().getPrimary().equals(primary.getValue())) {
        res2.put(e.getKey(), e.getValue());
      }
    }
    return res2;
  }

  public static void main(String[] args) {
    String a = "";
    System.out.println(a.split(",").length);
  }
}
