package com.abc.fun;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assume.assumeFalse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.abc.AbilityDetail;
import com.abc.AbstractParse;
import com.abc.Function;
import com.abc.FunctionDetail;
import com.abc.ItemDetail;
import com.abc.MapUtil;
import com.abc.UnitDetail;
import com.abc.fun.Hero.ViewData;

/*
 * 通过解析war3map.j中的函数，获得英雄介绍
 * 1.
if GetUnitTypeId(GetTriggerUnit())=='O018' and WU[GetConvertedPlayerId(GetTriggerPlayer())]==false then
call DisplayTimedTextToPlayer(GetTriggerPlayer(),0,0,10.,"|cffffcc00太古魔人【亚特兰蒂斯】|r
|cff80ff80【在平台商城购买此英雄】|r
单体打击能力|cffff0303★★★★★|r
群体打击能力|cffff0303★★★★|r
战场生存能力|cffff0303★★★★☆|r
战技战术能力|cffff0303★★★|r

|cffffcc00英雄技能|r
太古陨石，黑粒聚变，黑火禁力，妄灭一击，|cffff0000太古觉醒|r

|cffffcc00英雄介绍|r
被封印在古代亚特兰蒂斯遗迹中的炼金魔人，一旦苏醒必定引起天地灾变。")
else
endif

 * 2.
if GetTriggerUnit()==bb[23] and WU[GetConvertedPlayerId(GetTriggerPlayer())]==false then
call DisplayTimedTextToPlayer(GetTriggerPlayer(),0,0,10.,"|cffffcc00战鹿【玛维纳斯】|r

单体打击能力|cffff0303★★★★|r
群体打击能力|cffff0303★★★☆|r
战场生存能力|cffff0303★★★★|r
战技战术能力|cffff0303★★★|r

|cffffcc00英雄技能|r
万钧烈波，巨戟挑钩，压迫力，巨人体魄，|cffff0000战斗艺术|r

|cffffcc00英雄介绍|r
灭烬军团高级战斗员，体型巨大，天生神力，但性格温顺谦和，在军团高层中一直在平衡复杂的利益关系。")
else
endif
 */
public class HeroIntroParse {
  public Map<String, Hero> parse(List<Function> functions, Map<String, UnitDetail> heroMap,
      Map<String, AbilityDetail> abilityMap, Map<String, ItemDetail> idItemMap) throws Exception {

    Map<String, String> introMap = new HashMap<>();
    String arrName = "bb";
    for (Function function : functions) {
      List<String> rows = function.getRows();
      for (int i = 0; i < rows.size(); i++) {
        String row = rows.get(i);
        if (row.equals("|cffffcc00英雄介绍|r")) {
          int startIndex = i;
          while (startIndex > 0 && !rows.get(startIndex).startsWith("if Get")) {
            startIndex--;
          }

          int endIndex = i;
          while (endIndex < rows.size() && !rows.get(endIndex).startsWith("else")) {
            endIndex++;
          }

          List<String> intros = parseIntro(rows, startIndex, endIndex);
          String unitId = intros.get(0);
          if (unitId.contains("[")) {
            arrName = unitId.substring(0, unitId.indexOf("["));
          }

          StringBuilder buf = new StringBuilder();
          for (int li = 1; li < intros.size(); li++) {
            String line = intros.get(li);
            buf.append(line);
            buf.append("|n");
          }
          introMap.put(unitId, buf.toString());
        }
      }
    }

    Map<String, String> heroNameArrMap = HeroArrParse.parse(functions, arrName);
    System.out.println(heroNameArrMap);

    // 将bb[12]改成E018
    for (Entry<String, String> heroNameArrEntry : heroNameArrMap.entrySet()) {
      String key = heroNameArrEntry.getKey();
      String value = heroNameArrEntry.getValue();

      String intros = introMap.remove(key);
      if (intros != null) {
        introMap.put(value, intros);
      }
    }

    // 大招
    Map<String, List<String>> ultimateMap = HeroUltimateParse.parse(functions, heroNameArrMap);

    // 皮肤技能数据
    Map<String, Map<String, StringBuilder>>[] pifuMapArr = HeroPifuParse.parse(functions, heroNameArrMap);

    // 天赋map
    Map<String, List<String>> additionalMap = HeroAdditionalParse.parse(functions, heroNameArrMap);

    // 获取皮肤的描述
    // Name = "|cff80ff80森林少女皮肤1|r"
    Map<String, AbilityDetail> pifu1AbilityMap = new HashMap<>();
    Map<String, AbilityDetail> pifu2AbilityMap = new HashMap<>();
    for (Entry<String, AbilityDetail> entry : abilityMap.entrySet()) {
      AbilityDetail abilityDetail = entry.getValue();
      String name = AbstractParse.trimName(abilityDetail.getName());
      ;

      if (name.contains("皮肤1")) {
        name = name.substring(0, name.indexOf("皮肤1"));
        pifu1AbilityMap.put(name, abilityDetail);
      } else if (name.contains("皮肤2")) {
        name = name.substring(0, name.indexOf("皮肤2"));
        pifu2AbilityMap.put(name, abilityDetail);
      }
    }

    Map<String, UnitDetail> artHeroMap = new HashMap<>();
    for (UnitDetail u : heroMap.values()) {
      artHeroMap.put(u.getArt().toLowerCase(), u);
    }

    // 英雄归类
    Map<String, String[]> baseHeroSet = new HashMap<>();
    for (Entry<String, String> unitNameEntry : heroNameArrMap.entrySet()) {
      String uid = unitNameEntry.getValue();
      UnitDetail unitDetail = heroMap.get(uid);
      if (unitDetail == null) {
        // System.out.println("空uid" + uid);
        continue;
      }
      String[] list = MapUtil.getNotNull(baseHeroSet, uid, () -> new String[3]);
      if (list[0] != null) {
        continue;
      }
      list[0] = uid;
      String heroName = unitDetail.getName();
      for (UnitDetail vDetail : heroMap.values()) {
        if (heroName.equals(vDetail.getName()) && !vDetail.getId().equals(uid)) {
          AbilityDetail pifu1 = pifu1AbilityMap.get(heroName);
          if (pifu1 != null && pifu1.getArt().toLowerCase().equals(vDetail.getArt().toLowerCase())) {
            list[1] = vDetail.getId();
          }

          AbilityDetail pifu2 = pifu2AbilityMap.get(heroName);
          if (pifu2 != null && pifu2.getArt().toLowerCase().equals(vDetail.getArt().toLowerCase())) {
            list[2] = vDetail.getId();
          }
        }
      }
    }

    // 皮1皮2补录 巨神兵没有通过名字匹配到，那么就根据art去找
    for (Entry<String, String[]> entry : baseHeroSet.entrySet()) {
      String id = entry.getKey();
      String[] value = entry.getValue();
      UnitDetail unitDetail = heroMap.get(id);
      String heroName = unitDetail.getName();
      if (value[1] == null) {
        AbilityDetail pifu1 = pifu1AbilityMap.get(heroName);
        if (pifu1 != null) {

          UnitDetail uu = artHeroMap.get(pifu1.getArt().toLowerCase());
          if (uu != null) {
            value[1] = uu.getId();
          }
        }
      }

      if (value[2] == null) {
        AbilityDetail pifu2 = pifu2AbilityMap.get(heroName);
        if (pifu2 != null) {

          UnitDetail uu = artHeroMap.get(pifu2.getArt().toLowerCase());
          if (uu != null) {
            value[2] = uu.getId();
          }
        }
      }
      System.out.println(heroMap.get(id).getName() + ":" + id + "," + value[0] + "," + value[1] + "," + value[2]);
    }

    // 皮1 皮2 建立对这个皮肤集的映射关系
    Map<String, String[]> tmpMap = new HashMap<>();
    for (Entry<String, String[]> entry : baseHeroSet.entrySet()) {
      String[] next = entry.getValue();
      if (next[1] != null) {
        tmpMap.put(next[1], next);
      }
      if (next[2] != null) {
        tmpMap.put(next[2], next);
      }
    }
    baseHeroSet.putAll(tmpMap);

    System.out.println(idItemMap.get("H01E"));

    Map<String, String[]> juanzhouItemMap = HeroItemParse.parse(functions, heroNameArrMap, idItemMap);

    Map<String, Hero> heroResultMap = new HashMap<>();

    for (Entry<String, UnitDetail> entry : heroMap.entrySet()) {
      String uid = entry.getKey();
      UnitDetail unitDetail = entry.getValue();
      
      String[] values = baseHeroSet.get(uid);
      if(values==null){
        // unit.ini新出的英雄还未加入到地图
        continue;
      }
      String baseHeroId = values[0];
      Hero hero = new Hero();
      heroResultMap.put(uid, hero);

      String heroName = heroMap.get(baseHeroId).getName();
      hero.setUnitId(uid);
      hero.setBaseUnitId(baseHeroId);
      hero.setName(unitDetail.getNameReal());
      hero.setPropernames(unitDetail.getPropernames());
      hero.setAttack(unitDetail.getAtkType1Ori()+"," +unitDetail.getAttack()+"," + unitDetail.getRangeN1()+","+unitDetail.getCool1());
      hero.setArmor(unitDetail.getDefTypeOri()+"," +unitDetail.getDefReal()+","+unitDetail.getSpd());
      hero.setProp(unitDetail.getPrimaryInt()+","+unitDetail.getSTR()+","+unitDetail.getAGI()+","+unitDetail.getINT());
      hero.setHp(unitDetail.getHpReal()+"," + unitDetail.getManaNReal());
      // 介绍
      String intro = introMap.get(baseHeroId);
      hero.setIntro(intro);

      // 小技能 + 皮肤数据
      String heroAbili = unitDetail.getHeroAbilList();
      String[] normalAbilityArr = heroAbili.split(",");
      for (String normalAbiId : normalAbilityArr) {
        AbilityDetail normalAbi = abilityMap.get(normalAbiId);
        String icon = normalAbi.getArt();
        String hotKey = normalAbi.getResearchHotkey();
        String name = normalAbi.getName();
        String desc = normalAbi.getUbertip();

        ViewData viewData = new ViewData();
        viewData.setName(name);
        viewData.setIcon(icon);
        if ("W".equals(hotKey)) {
          String abiNo = "1";
          appendPifuAbi(pifuMapArr, uid, values, baseHeroId, desc, viewData, abiNo);
          hero.setQ(viewData);
        } else if ("E".equals(hotKey)) {
          String abiNo = "2";
          appendPifuAbi(pifuMapArr, uid, values, baseHeroId, desc, viewData, abiNo);
          hero.setW(viewData);
        } else if ("R".equals(hotKey)) {
          String abiNo = "3";
          appendPifuAbi(pifuMapArr, uid, values, baseHeroId, desc, viewData, abiNo);
          hero.setE(viewData);
        } else {
          String abiNo = "4";
          appendPifuAbi(pifuMapArr, uid, values, baseHeroId, desc, viewData, abiNo);
          hero.setR(viewData);
        }
      }

      // 天赋
      List<String> additional = additionalMap.get(baseHeroId);
      ViewData d = new ViewData();
      d.setIcon("war3mapImported\\\\PASBTNItem_JiaBaiLieGuangHui.blp");
      StringBuilder sb = new StringBuilder();
      d.setName("|cffccffcc英雄天赋|r");
      sb.append("|cffffcc00天赋1：|r").append(additional.get(0));
      sb.append("|n");
      sb.append("|cffffcc00天赋2：|r").append(additional.get(1));
      d.setDesc(sb.toString());
      hero.setD(d);

      // 大招
      ViewData t = new ViewData();
      // E003 金色魔王
      // [A00I, A0R0]
      // O005 战鹿
      // [A07F, A0L9],
      // O003 功夫猫咪
      // [A02U, A0FY],
      // H00D 灭烬将军
      // [A0EQ, A0WK]
      List<String> ultimateList = ultimateMap.get(baseHeroId);
      if (uid.equals(values[2]) && ultimateList.size() == 2) {
        String ultimate = ultimateList.get(1);
        AbilityDetail ultimateAbi = abilityMap.get(ultimate);
        t.setName(ultimateAbi.getName());
        t.setIcon(ultimateAbi.getArt());
        appendPifuAbi(pifuMapArr, uid, values, baseHeroId, ultimateAbi.getUbertip(), t, "5");
      } else {
        String ultimate = ultimateList.get(0);
        AbilityDetail ultimateAbi = abilityMap.get(ultimate);
        t.setName(ultimateAbi.getName());
        t.setIcon(ultimateAbi.getArt());
        appendPifuAbi(pifuMapArr, uid, values, baseHeroId, ultimateAbi.getUbertip(), t, "5");
      }
      hero.setT(t);

      // 2个皮肤
      AbilityDetail p1 = pifu1AbilityMap.get(heroName);
      if (p1 != null) {
        ViewData p1ViewData = new ViewData();
        p1ViewData.setUnitId(values[1]);
        p1ViewData.setName(p1.getName());
        p1ViewData.setIcon(p1.getArt());
        p1ViewData.setDesc(p1.getUbertip());
        hero.setP1(p1ViewData);
      }
      AbilityDetail p2 = pifu2AbilityMap.get(heroName);
      if (p2 != null) {
        ViewData p2ViewData = new ViewData();
        p2ViewData.setUnitId(values[2]);
        p2ViewData.setName(p2.getName());
        p2ViewData.setIcon(p2.getArt());
        p2ViewData.setDesc(p2.getUbertip());
        hero.setP2(p2ViewData);
      }

      // 专属装备
      String[] items = juanzhouItemMap.get(baseHeroId);
      if (items == null) {
        System.out.println("未找到" + baseHeroId + "的专属装备");
      } else {
        for (int i = 0; i < 2; i++) {
          String itemId = items[i];
          if (itemId != null) {
            ItemDetail item = idItemMap.get(itemId);
            ViewData viewData = new ViewData();
            viewData.setName(item.getName());
            viewData.setIcon(item.getIcon());
            viewData.setDesc(item.getDescription());
            hero.getItems().add(viewData);
          }
        }
        if ((uid.equals(values[1]) || uid.equals(values[2]))
            || baseHeroId.equals("E03O") || baseHeroId.equals("O010") || baseHeroId.equals("O018")
            || baseHeroId.equals("E006") || baseHeroId.equals("E00Y") || baseHeroId.equals("H006")) {
          String itemId = items[2];
          if (itemId != null) {
            ItemDetail item = idItemMap.get(itemId);
            ViewData viewData = new ViewData();
            viewData.setName(item.getName());
            viewData.setIcon(item.getIcon());
            viewData.setDesc(item.getDescription());
            hero.getItems().add(viewData);
          }
        }
        if (uid.equals(values[2])) {
          String itemId = items[3];
          if (itemId != null) {
            ItemDetail item = idItemMap.get(itemId);
            ViewData viewData = new ViewData();
            viewData.setName(item.getName());
            viewData.setIcon(item.getIcon());
            viewData.setDesc(item.getDescription());
            hero.getItems().add(viewData);
          }
        }
      }

    }

    System.out.println(ultimateMap);
    return heroResultMap;
  }

  /**
   * 属性强化
   * 
   * @param pifuMapArr
   * @param uid
   * @param values
   * @param baseHeroId
   * @param desc
   * @param viewData
   * @param abiNo
   */
  private void appendPifuAbi(Map<String, Map<String, StringBuilder>>[] pifuMapArr, String uid, String[] values,
      String baseHeroId, String desc, ViewData viewData, String abiNo) {
    // 续5秒|r" + "|n|r|cffcc8dc4|n皮肤强化：|n"+"|r"
    Map<String, StringBuilder> map1 = pifuMapArr[0].get(baseHeroId);
    Map<String, StringBuilder> map2 = pifuMapArr[1].get(baseHeroId);
    if (uid.equals(values[0])) {
      viewData.setDesc(desc);
    } else if (uid.equals(values[2]) && map2 != null && map2.get(abiNo) != null) {
      viewData.setDesc(desc + "|n|r|cffcc8dc4|n皮肤强化：|n" + map2.get(abiNo) + "|r");
    } else if (map1 != null && map1.get(abiNo) != null) {
      viewData.setDesc(desc + "|n|r|cffcc8dc4|n皮肤强化：|n" + map1.get(abiNo) + "|r");
    } else {
      // 皮1 皮2 未找到强化效果
      viewData.setDesc(desc);
    }

  }

  protected static List<String> parseIntro(List<String> rows, int startIndex, int endIndex) {
    List<String> introList = new ArrayList<>();

    String head = rows.get(startIndex);
    String unitName = headParse(head);
    introList.add(unitName);

    for (int i = startIndex + 1; i < endIndex; i++) {
      String row = rows.get(i);
      if (row.startsWith("call DisplayTimedTextToPlayer")) {
        int split = row.indexOf("\"");
        introList.add(row.substring(split + 1));
      } else {
        if (row.endsWith("\")")) {
          introList.add(row.substring(0, row.length() - 2));
        } else {
          introList.add(row);
        }
      }

    }

    return introList;
  }

  protected static String headParse(String headLine) {
    Pattern headPattern = Pattern.compile("^if Get.*=='?([\\w|\\[|\\]]+)'? and .*$");
    Matcher headMatcher = headPattern.matcher(headLine);
    if (headMatcher.find()) {
      String unitName = headMatcher.group(1);
      return unitName;
    } else {
      assumeFalse("无法解析的英雄介绍:" + headLine, false);
      return null;
    }
  }

  public static void main(String[] args) {
    Pattern headPattern = Pattern.compile("^if Get.*=='?([\\w|\\[|\\]]+)'? and .*$");
    Matcher headMatcher = headPattern.matcher(
        "if GetUnitTypeId(GetTriggerUnit())=='O018' and WU[GetConvertedPlayerId(GetTriggerPlayer())]==false then");
    if (headMatcher.find()) {
      String unitName = headMatcher.group(1);
      System.out.println(unitName);
    } else {
      assumeFalse("无法解析的英雄介绍:", true);

    }
  }

}
