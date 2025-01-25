package com.abd;

import java.text.Collator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.abd.UnitDetail.Primary;
import com.abd.function.HeroData;
import com.abd.function.HeroData.ViewData;
import com.abd.function.hero.FunctionHeroDaZhaoAndCore;
import com.abd.function.hero.FunctionHeroJianjieOrDaZhao;
import com.abd.function.hero.FunctionHeroMainProp;
import com.abd.function.hero.FunctionHeroName;
import com.abd.function.hero.Hero;

public class HeroParse extends AbstractParse {
  private Map<String, Hero[]> res;

  public void wrapHero(Map<String, AbilityDetail> abilityMap, Map<String, UnitDetail> unitMap,
      Map<String, ItemDetail> idItemMap, List<Function> funList) {
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
    Map<String, List<Map<String, String>>> udgHeroDazhaoAndCoreMap = FunctionHeroDaZhaoAndCore.parse(funMap);

    Map<String, List<ItemDetail>> heroItemMap = new HashMap<>();
    for (ItemDetail item : idItemMap.values()) {
      if (item.getHero() != null) {
        List<ItemDetail> items = MapUtil.getNotNull(heroItemMap, item.getHero(), ArrayList::new);
        items.add(item);
      }
    }

    Map<String, String[]> udgHeroTeam = new HashMap<>();
    Map<String, Hero> heroMap = new HashMap<>();

    for (Entry<String, UnitDetail> entry : unitMap.entrySet()) {
      UnitDetail unit = entry.getValue();
      if ("giant".equals(unit.getType()) && ("111".equals(unit.getPoints()) || "222".equals(unit.getPoints()))) {

        Hero hero = new Hero();
        hero.setAbilityMap(abilityMap);
        hero.setId(unit.getId());
        hero.setUnit(unit);
        heroMap.put(hero.getId(), hero);

        // 主要属性
        String udgheroname = udgHeroNameMap.get(unit.getId());
        String mainprop = udgHeroMainPropMap.get(udgheroname);
        hero.setMainPropDesc(mainprop);

        List<String> jianjieOrDazhaoList = udgHeroJianjieOrDaZhaoMap.get(unit.getId());
        if (jianjieOrDazhaoList == null) {
          // N06U
          for (Entry<String, UnitDetail> e : unitMap.entrySet()) {
            UnitDetail unitDetail = e.getValue();
            if (!unit.getId().equals(unitDetail.getId()) && unit.getName().equals(unitDetail.getName())) {
              jianjieOrDazhaoList = udgHeroJianjieOrDaZhaoMap.get(e.getKey());
              hero.setPi("皮");
              String[] team = new String[2];
              team[0] = unitDetail.getId();
              team[1] = unit.getId();
              udgHeroTeam.put(unitDetail.getId(), team);
              break;
            }
          }
        } else {
          // 单独没有第二英雄
          if (null == udgHeroTeam.get(unit.getId())) {
            String[] team = new String[2];
            team[0] = unit.getId();
            udgHeroTeam.put(unit.getId(), team);
          }

        }

        if (jianjieOrDazhaoList == null) {

          for (Entry<String, String> e : udgHeroNameMap.entrySet()) {
            if (!e.getKey().equals(unit.getId()) && udgheroname.equals(e.getValue())) {
              jianjieOrDazhaoList = udgHeroJianjieOrDaZhaoMap.get(e.getKey());

              String[] team = new String[2];
              team[0] = e.getKey();
              team[1] = unit.getId();
              udgHeroTeam.put(e.getKey(), team);
              break;
            }
          }

        }
        if (jianjieOrDazhaoList == null) {
          System.out.println(unit.getId() + "没有找到简介");
        }

        for (String jianjieOrDazhao : jianjieOrDazhaoList) {
          AbilityDetail abilityDetail = abilityMap.get(jianjieOrDazhao);

          if (abilityDetail.getName().contains("旅行者简介")) {
            hero.setIntro(abilityDetail);
          } else {
            switch (abilityDetail.getHotkey()) {
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
              case "T":
                hero.setT(abilityDetail);
                break;
              default:
                break;
            }
          }
        }

        for (String ability : unit.getHeroAbilList().split(",")) {
          AbilityDetail abilityDetail = abilityMap.get(ability);
          if (abilityDetail == null) {
            break;
          }

          if (abilityDetail.getName().contains("旅行者简介")) {
            hero.setIntro(abilityDetail);
          } else {
            switch (abilityDetail.getHotkey()) {
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
              case "T":
                hero.setT(abilityDetail);
                break;
              default:
                break;
            }
          }
        }

        // 大招和魔核
        // <111|222, List<Map<udg_ROCK, ability>> <111|222, List<Map<O004, ability>>
        List<Map<String, String>> listDazhoa = udgHeroDazhaoAndCoreMap.get(unit.getPoints());
        Map<String, String> dazhaoList = listDazhoa.get(0);
        Map<String, String> moheList = listDazhoa.get(1);

        String dazhao = dazhaoList.getOrDefault(unit.getId(), dazhaoList.get(udgheroname));
        if (dazhao != null) {
          AbilityDetail abilityDetail = abilityMap.get(dazhao);
          abilityDetail.setHotkey("T");
          hero.setT(abilityDetail);
        }
        String mohe = moheList.getOrDefault(unit.getId(), moheList.get(udgheroname));
        if (mohe != null) {
          hero.setCore(abilityMap.get(mohe));
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
    Map<String, Function> funMap = funList.stream()
        .collect(Collectors.toMap(Function::getName, java.util.function.Function.identity()));

    FunctionHeroMainProp FunctionHeroMainProp = new FunctionHeroMainProp();

    // <udg_ROCK, "力量">
    Map<String, String> udgHeroMainPropMap = FunctionHeroMainProp.parse(funMap);

    FunctionHeroName FunctionHeroName = new FunctionHeroName();
    // <O004 , udg_ROCK>
    Map<String, String> udgHeroNameMap = FunctionHeroName.parse(funMap);
    //手动补录工程大师皮肤
    udgHeroNameMap.put("N00E", "udg_ONE");

    FunctionHeroJianjieOrDaZhao FunctionHeroJianjieOrDaZhao = new FunctionHeroJianjieOrDaZhao();
    // <O004 , List<String>>
    Map<String, List<String>> udgHeroJianjieOrDaZhaoMap = FunctionHeroJianjieOrDaZhao.parse(funMap);

    FunctionHeroDaZhaoAndCore FunctionHeroDaZhaoAndCore = new FunctionHeroDaZhaoAndCore();
    // <111|222, List<Map<udg_ROCK, ability>> <111|222, List<Map<O004, ability>>
    Map<String, List<Map<String, String>>> udgHeroDazhaoAndCoreMap = FunctionHeroDaZhaoAndCore.parse(funMap);
    udgHeroDazhaoAndCoreMap.get("111").get(1).put("udg_ONE", "A069");

    Map<String, List<ItemDetail>> heroItemMap = new HashMap<>();
    for (ItemDetail item : idItemMap.values()) {
      if (item.getHero() != null) {
        List<ItemDetail> items = MapUtil.getNotNull(heroItemMap, item.getHero(), ArrayList::new);
        items.add(item);
      }
    }

    Map<String, String[]> udgHeroTeam = new HashMap<>();
    Map<String, HeroData> heroMap = new HashMap<>();

    for (Entry<String, UnitDetail> entry : unitMap.entrySet()) {
      UnitDetail unit = entry.getValue();
      if ("giant".equals(unit.getType()) && ("111".equals(unit.getPoints()) || "222".equals(unit.getPoints()))) {

        HeroData hero = new HeroData();
        // 会被重置
        String baseUserId = unit.getId();
        hero.setUnitId(unit.getId());

        hero.setName(unit.getPropernames());
        hero.setPropernames(unit.getNameReal());
        hero.setAttack(unit.getAtkType1Ori()+"," +unit.getAttack()+"," + unit.getRangeN1()+","+unit.getCool1());
        hero.setArmor(unit.getDefTypeOri()+"," +unit.getDefReal()+","+unit.getSpd());
        hero.setProp(unit.getPrimaryInt()+","+unit.getSTR()+","+unit.getAGI()+","+unit.getINT());
        hero.setHp(unit.getHpReal()+"," + unit.getManaNReal());

        heroMap.put(unit.getId(), hero);

        // 主要属性
        String udgheroname = udgHeroNameMap.get(unit.getId());
        String mainprop = udgHeroMainPropMap.get(udgheroname);

        List<String> jianjieOrDazhaoList = udgHeroJianjieOrDaZhaoMap.get(unit.getId());
        if (jianjieOrDazhaoList == null) {
          // N06U 针对隐暗射手
          for (Entry<String, UnitDetail> e : unitMap.entrySet()) {
            UnitDetail unitDetail = e.getValue();
            if (!unit.getId().equals(unitDetail.getId()) && unit.getName().equals(unitDetail.getName())) {
              jianjieOrDazhaoList = udgHeroJianjieOrDaZhaoMap.get(e.getKey());
              hero.setPi("皮");
              String[] team = new String[2];
              team[0] = unitDetail.getId();
              team[1] = unit.getId();

              // 重置baseUnitId
              baseUserId = team[0];
              udgHeroTeam.put(unitDetail.getId(), team);
              break;
            }
          }
        } else {
          // 单独没有第二英雄
          if (null == udgHeroTeam.get(unit.getId())) {
            String[] team = new String[2];
            team[0] = unit.getId();
            udgHeroTeam.put(unit.getId(), team);
          }

        }

        if(unit.getId().equals("N02D")){
          System.out.println("找到工程大师");
        }
        if (jianjieOrDazhaoList == null) {

          for (Entry<String, String> e : udgHeroNameMap.entrySet()) {
            if (!e.getKey().equals(unit.getId()) && udgheroname.equals(e.getValue())) {
              jianjieOrDazhaoList = udgHeroJianjieOrDaZhaoMap.get(e.getKey());

              String[] team = new String[2];
              team[0] = e.getKey();
              team[1] = unit.getId();
              // 重置baseUnitId
              baseUserId = team[0];
              udgHeroTeam.put(e.getKey(), team);
              break;
            }
          }

        }
        if (jianjieOrDazhaoList == null) {
          System.out.println(unit.getId() + "没有找到简介");
        }
        
        hero.setBaseUnitId(baseUserId);


        for (String jianjieOrDazhao : jianjieOrDazhaoList) {
          AbilityDetail abilityDetail = abilityMap.get(jianjieOrDazhao);

          ViewData viewData = new ViewData();
          viewData.setName(abilityDetail.getName());
          viewData.setIcon(abilityDetail.getArt());
          viewData.setDesc(abilityDetail.getUbertip());

          if (abilityDetail.getName().contains("旅行者简介")) {
            hero.setD2(viewData);
          } else {
            switch (abilityDetail.getHotkey()) {
              case "Q":
                hero.setQ(viewData);
                break;
              case "W":
                hero.setW(viewData);
                break;
              case "E":
                hero.setE(viewData);
                break;
              case "R":
                hero.setR(viewData);
                break;
              case "T":
                hero.setT2(viewData);
                break;
              default:
                break;
            }
          }
        }

        for (String ability : unit.getHeroAbilList().split(",")) {
          AbilityDetail abilityDetail = abilityMap.get(ability);
          if (abilityDetail == null) {
            break;
          }
          ViewData viewData = new ViewData();
          viewData.setName(abilityDetail.getName());
          viewData.setIcon(abilityDetail.getArt());
          viewData.setDesc(abilityDetail.getUbertip());

          if (abilityDetail.getName().contains("旅行者简介")) {
            hero.setD2(viewData);
          } else {
            switch (abilityDetail.getHotkey()) {
              case "Q":
                hero.setQ(viewData);
                break;
              case "W":
                hero.setW(viewData);
                break;
              case "E":
                hero.setE(viewData);
                break;
              case "R":
                hero.setR(viewData);
                break;
              case "T":
                hero.setT2(viewData);
                break;
              default:
                break;
            }
          }
        }

        // 大招和魔核
        // <111|222, List<Map<udg_ROCK, ability>> <111|222, List<Map<O004, ability>>
        List<Map<String, String>> listDazhoa = udgHeroDazhaoAndCoreMap.get(unit.getPoints());
        Map<String, String> dazhaoList = listDazhoa.get(0);
        Map<String, String> moheList = listDazhoa.get(1);

        String dazhao = dazhaoList.getOrDefault(unit.getId(), dazhaoList.get(udgheroname));
        if (dazhao != null) {
          AbilityDetail abilityDetail = abilityMap.get(dazhao);
          abilityDetail.setHotkey("T");
          ViewData viewData = new ViewData();
          viewData.setName(abilityDetail.getName());
          viewData.setIcon(abilityDetail.getArt());
          viewData.setDesc(abilityDetail.getUbertip());

          hero.setT2(viewData);
        }
        String mohe = moheList.getOrDefault(unit.getId(), moheList.get(udgheroname));
        if (mohe != null) {
          AbilityDetail abilityDetail = abilityMap.get(mohe);
          if (abilityDetail != null) {
            ViewData viewData = new ViewData();
            viewData.setName(abilityDetail.getName());
            viewData.setIcon(abilityDetail.getArt());
            viewData.setDesc(abilityDetail.getUbertip());
            hero.setCore(viewData);
          }
        }

        // 补充物品
        UnitDetail baseUnit = unitMap.get(baseUserId);
        String name = UnitParse.trimName(baseUnit.getPropernames());
        List<ItemDetail> itemList = heroItemMap.get(name);
        if (itemList != null) {
          Collator collator = Collator.getInstance(new Locale("zh", "CN"));
          itemList.sort((o1, o2) -> {
            int v1 = o1.getLevelInt() - o2.getLevelInt();
            return v1 != 0 ? v1 : collator.compare(o1.getName(), o2.getName());
          });

          for (ItemDetail item : itemList) {
            ViewData viewData = new ViewData();
            viewData.setName(item.getName());
            viewData.setIcon(item.getIcon());
            viewData.setDesc(item.getDescription());
            hero.getItems().add(viewData);
          }
        }
        continue;
      }
    }
    Map<String, String> pMap = new HashMap<>();

    pMap.put("E008", "O00Q");
    pMap.put("O000", "O00S");
    pMap.put("H006", "O00E");
    pMap.put("N06G", "O00G");
    pMap.put("O001", "O00I");
    pMap.put("O004", "O00K");
    pMap.put("H007", "O00L");
    pMap.put("N02D", "O00T");

   

    for (Entry<String, String[]> e : udgHeroTeam.entrySet()) {
      String[] team = e.getValue();
      HeroData[] heroTeam = new HeroData[2];
      heroTeam[0] = heroMap.get(team[0]);
      heroTeam[1] = heroMap.get(team[1]);

      String baseUnitId = team[0];
      HeroData heroData = heroMap.get(baseUnitId);
      System.out.println(heroData.getName() + ":" + team[0] + "," + team[1]);
      String p1 = pMap.get(baseUnitId);
      UnitDetail pfu = unitMap.get(p1);
      if (pfu != null) {
        {
          // 原皮内看到的皮肤信息
          ViewData viewData = new ViewData();
          viewData.setName(heroData.getName() + "皮肤");
          viewData.setIcon(pfu.getArt());
          viewData.setDesc(pfu.getUbertip());
          viewData.setUnitId(team[1]);
          heroTeam[0].setP1(viewData);

          // 原皮移除一件专属装备，
          heroTeam[0].getItems().remove(heroTeam[0].getItems().size() - 1);
          // "E008" 多移除一件
          if(team[0].equals("E008")){
            heroTeam[0].getItems().remove(heroTeam[0].getItems().size() - 1);
          }
        }

        {
          // 皮内看到的皮肤信息
          ViewData viewData = new ViewData();
          viewData.setName(heroData.getName() + "皮肤");
          viewData.setIcon(pfu.getArt());
          viewData.setDesc(pfu.getUbertip());
          viewData.setUnitId(team[1]);
          heroTeam[1].setP1(viewData);
        }

      }

      // 缪斯单独处理
      if(heroData.getUnitId().equals("H00U")){
        {
          // 原皮内看到的皮肤信息
          ViewData viewData = new ViewData();
          viewData.setName(heroData.getName() + "进化");
          UnitDetail ms = unitMap.get("H00U");
          AbilityDetail tianlai = abilityMap.get("A11Z");
          viewData.setIcon(tianlai.getArt());
          viewData.setDesc(tianlai.getUbertip());
          viewData.setUnitId(heroData.getUnitId());
          heroTeam[0].setP1(viewData); 

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
