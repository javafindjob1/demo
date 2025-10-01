package com.mp.parse;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.Collator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.junit.Assert;

import com.common.parse.AbstractParse;
import com.common.parse.Function;
import com.common.util.MapUtil;
import com.common.util.ObjectUtil;
import com.mp.Client;
import com.mp.function.HeroData;
import com.mp.function.HeroData.ViewData;
import com.mp.function.hero.FunctionHeroDaZhaoAndCore;
import com.mp.function.hero.FunctionHeroJianjieOrDaZhao;
import com.mp.function.hero.FunctionHeroMainProp;
import com.mp.function.hero.FunctionHeroName;
import com.mp.function.hero.Hero;
import com.mp.parse.UnitDetail.Primary;

public class HeroParse extends AbstractParse {
  private Map<String, Hero[]> res;

  public void wrapHero(Map<String, AbilityDetail> abilityMap, Map<String, UnitDetail> unitMap,
      Map<String, ItemDetail> idItemMap, List<Function> funList) {

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

    Hero one = heroMap.get("N02D");
    Hero onePlus = heroMap.get("N00E");

    if (funList == null) {
      Map<String, Hero[]> res = new LinkedHashMap<>();
      for (Entry<String, Hero> e : heroMap.entrySet()) {
        Hero[] heroTeam = new Hero[2];
        heroTeam[0] = e.getValue();
        res.put(e.getKey(), heroTeam);
      }
      this.res = res;
      return;
    }

    Map<String, Function> funMap = funList.stream()
        .collect(Collectors.toMap(Function::getName, java.util.function.Function.identity()));

    FunctionHeroMainProp FunctionHeroMainProp = new FunctionHeroMainProp();

    // <udg_ROCK, "力量">
    Map<String, String> udgHeroMainPropMap = FunctionHeroMainProp.parse(funMap);

    FunctionHeroName FunctionHeroName = new FunctionHeroName();
    // <O004 , udg_ROCK>
    Map<String, String> udgHeroNameMap = FunctionHeroName.parse(funMap);

    FunctionHeroJianjieOrDaZhao FunctionHeroJianjieOrDaZhao = new FunctionHeroJianjieOrDaZhao();
    // <O004 , List<String>>
    Map<String, List<String>> udgHeroJianjieOrDaZhaoMap = FunctionHeroJianjieOrDaZhao.parse(funMap);

    FunctionHeroDaZhaoAndCore FunctionHeroDaZhaoAndCore = new FunctionHeroDaZhaoAndCore();
    // <111|222, List<Map<udg_ROCK, ability>> <111|222, List<Map<O004, ability>>
    Map<String, String> udgHeroDazhaMap = FunctionHeroDaZhaoAndCore.parse(funMap);

    Map<String, String[]> udgHeroTeam = new HashMap<>();

    for (Hero hero : heroMap.values()) {
      // 主要属性
      String udgheronameAll = udgHeroNameMap.get(hero.getId());
      if (udgheronameAll == null) {
        System.out.println("基本英雄ID不能未找到：" + hero.getId());
        continue;
      }

      String udgheroname = null;
      if (udgheronameAll.endsWith("V")) {
        // 部分皮英雄 隐暗射手 耀光射手
        udgheroname = udgheronameAll.substring(0, udgheronameAll.length() - 1);
      } else if (udgheronameAll.endsWith("PLUS")) {
        // 部分皮英雄 万
        udgheroname = udgheronameAll.substring(0, udgheronameAll.length() - 4);
      } else if (udgheronameAll.endsWith("TLFLP")) {
        // 部分皮英雄 熊猫
        udgheroname = udgheronameAll.substring(0, udgheronameAll.length() - 1);
      } else {
        udgheroname = udgheronameAll;
      }
      String baseUserId = udgHeroNameMap.get(udgheroname);
      String mainprop = udgHeroMainPropMap.get(udgheroname);
      hero.setMainPropDesc(mainprop);

      if (baseUserId.equals(hero.getId())) {
        String[] team = MapUtil.getNotNull(udgHeroTeam, baseUserId, () -> new String[2]);
        team[0] = baseUserId;
      } else {
        hero.setPi("皮");
        String[] team = MapUtil.getNotNull(udgHeroTeam, baseUserId, () -> new String[2]);
        team[1] = hero.getId();
      }

      List<String> jianjieOrDazhaoList = udgHeroJianjieOrDaZhaoMap.get(baseUserId);
      if (jianjieOrDazhaoList == null) {
        System.out.println(hero.getId() + "没有找到简介");
        continue;
      }
      hero.addAbilitys(jianjieOrDazhaoList);

      // 大招和魔核
      // <111|222, List<Map<udg_ROCK, ability>> <111|222, List<Map<O004, ability>>
      String dazhao = MapUtil.getNotNull(udgHeroDazhaMap, hero.getId(), () -> udgHeroDazhaMap.get(udgheronameAll));
      hero.addAbility(dazhao, "T");

      try (BufferedReader br = new BufferedReader(
          new InputStreamReader(Client.class.getResourceAsStream("custom/凝空光晶.txt"), StandardCharsets.UTF_8))) {
        String line = null;
        Map<String, AbilityDetail> moheMap = new HashMap<>();
        while ((line = br.readLine()) != null) {
          if (line.trim().length() == 0) {
            continue;
          }
          // |cffffffcc凝空光晶-狂战神|r
          // |cffbeedc7
          if (line.startsWith("|cffffffcc")) {
            String heroName = line.substring(15, line.length() - 2);
            line += "|n" + br.readLine();
            AbilityDetail mohe = abilityMap.get("A164");
            AbilityDetail moheCopy = new AbilityDetail();
            moheCopy.setId(mohe.getId());
            moheCopy.setName("凝空光晶");
            moheCopy.setArt(mohe.getArt());
            moheCopy.setUbertip(line);
            moheMap.put(heroName, moheCopy);
          }
        }

        UnitDetail baseHero = unitMap.get(baseUserId);
        AbilityDetail mohDetail = moheMap.get(baseHero.getName());
        if (mohDetail == null) {
          mohDetail = moheMap.get(UnitParse.trimName(baseHero.getPropernames()));
        }
        if (mohDetail == null) {
          System.out.println("凝空光晶未找到，" + UnitParse.trimName(baseHero.getPropernames()) + baseHero.getName());
        }
        hero.setCore(mohDetail);
      } catch (IOException e1) {
        System.out.println("凝空光晶文本读取失败");
        e1.printStackTrace();
      }

      continue;
    }

    Map<String, Hero[]> res = new LinkedHashMap<>();
    for (Entry<String, String[]> e : udgHeroTeam.entrySet()) {
      String[] team = e.getValue();
      Hero[] heroTeam = new Hero[2];
      heroTeam[0] = heroMap.get(team[0]);
      heroTeam[1] = heroMap.get(team[1]);
      res.put(e.getKey(), heroTeam);
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
