package com.xi4.parse;

import java.util.*;
import java.util.Map.Entry;

import com.common.ini.HeroGroup;
import com.common.parse.AbstractParse;
import com.common.parse.Function;
import com.common.util.MapUtil;
import com.common.util.ObjectUtil;
import com.xi4.function.Hero;
import com.xi4.function.HeroData;
import com.xi4.function.HeroData.ViewData;
import com.xi4.parse.UnitDetail.Primary;

import lombok.Data;

@Data
public class HeroParse extends AbstractParse {
  private Map<String, Hero[]> res;

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
      String heroAbStrings = unit.getHeroAbilList();
      String[] heroAbArr = heroAbStrings.split(",");
      if (heroAbArr.length == 4) {
        unitHeroMap.put(unit.getId(), unit);
      }
    }

    Map<String, Hero> heroMap = new HashMap<>();
    for (UnitDetail unit : unitHeroMap.values()) {
      Hero hero = new Hero(unit, abilityMap, heroItemMap);
      heroMap.put(hero.getId(), hero);
    }

    if (heroGroup == null) {
      Map<String, Hero[]> res = new LinkedHashMap<>();
      for (Entry<String, Hero> e : heroMap.entrySet()) {
        Hero[] heroTeam = new Hero[2];
        heroTeam[0] = e.getValue();
        res.put(e.getKey(), heroTeam);
      }
      this.res = res;
      return;
    }

    for (Hero hero : heroMap.values()) {

      String heroId = hero.getId();
      String[] ultis = heroGroup.getUlti(heroId);
      if (ultis != null) {
        hero.addAbilitys(ultis);
      }

      String intro = heroGroup.getIntro(heroId);
      if (intro != null) {
        AbilityDetail introAbi = new AbilityDetail();
        introAbi.setUbertip(intro);
        introAbi.setName("简介");
        hero.setIntro(introAbi);

        String teamId = heroGroup.getTeamId(heroId);
        if(!teamId.equals(heroId)){
          // 这是个皮肤，可以添加增强内容
          String heroName = hero.getUnit().getName();
          for(AbilityDetail abi : abilityMap.values()){
            String abiZengqiang = abi.getName();
            if(abiZengqiang.contains(heroName) && abiZengqiang.contains("的皮肤")){
              hero.setIntro(abi);
            }
          }
        }
      }

      String story = heroGroup.getStory(heroId);
      if (story != null) {
        AbilityDetail storyAbi = new AbilityDetail();
        storyAbi.setUbertip(story);
        storyAbi.setName("剧情");
        hero.setStory(storyAbi);
      }
    }

    Map<String, Hero[]> res = new LinkedHashMap<>();
    Map<String, String[]> udgHeroTeam = heroGroup.getHeros();

    Set<String> existHero = new HashSet<>();
    for (Entry<String, String[]> e : udgHeroTeam.entrySet()) {
      String teamId = e.getKey();
      String[] team = e.getValue();
      Hero[] heroTeam = new Hero[2];

      for (int i = 0; i < team.length; i++) {
        heroTeam[i] = heroMap.get(team[i]);
        existHero.add(team[i]);
      }
      res.put(teamId, heroTeam);
    }

    for (Hero hero : heroMap.values()) {
      if (existHero.contains(hero.getId())) {
        continue;
      }
      String teamId = hero.getId();
      Hero[] heroTeam = new Hero[2];
      heroTeam[0] = hero;
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
      Hero[] heroArr = e.getValue();
      if (heroArr[0] != null) {
        if (heroArr[0].getUnit().getPrimary().equals(primary.getValue())) {
          res2.put(e.getKey(), e.getValue());
        }
      }
    }
    return res2;
  }

  public static void main(String[] args) {
    String a = "";
    System.out.println(a.split(",").length);
  }
}
