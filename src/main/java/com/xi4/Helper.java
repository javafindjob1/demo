package com.xi4;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.common.ini.Ability;
import com.common.ini.IniRead;
import com.common.ini.Item;
import com.common.ini.Unit;
import com.common.j.FunctionRead;
import com.common.parse.Function;
import com.xi4.parse.AbilityDetail;
import com.xi4.parse.AbilityParse;
import com.xi4.parse.ExcelImageInsert;
import com.xi4.parse.ItemDetail;
import com.xi4.parse.ItemParse;
import com.xi4.parse.UnitDetail;
import com.xi4.parse.UnitParse;

public class Helper {

  public static void findIdByName(List<Item> list) throws FileNotFoundException, IOException {
    ItemParse itemParse = new ItemParse();
    Map<String, ItemDetail> idItemMap = itemParse.parse(list);
    System.out.println("解析物品完成");

    Map<String, ItemDetail> nameMap = itemParse.getNameMap();
    try (
        BufferedReader br = new BufferedReader(
            new InputStreamReader(Client.class.getResourceAsStream("custom/test"), StandardCharsets.UTF_8));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
            new FileOutputStream("D:\\Code\\demo\\demo\\src\\main\\java\\com\\xi4\\custom\\testout"),
            StandardCharsets.UTF_8))) {
      String line;
      while ((line = br.readLine()) != null) {
        String name = line.trim();
        if (name.length() > 0) {
          ItemDetail item = nameMap.get(name);
          ItemDetail item2 = idItemMap.get("I04H");
          if (item == null) {
            throw new RuntimeException();
          }
          bw.write(item.getId() + " " + name);
        } else {
          bw.write("");
        }
        bw.newLine();
      }
    }
  }

  public static void main(String[] args) throws Exception {

    String w3xliniPath = "D:\\war5-jass\\jass_plugin\\w3x2lni_zhCN_v2.5.2\\w3x2lni_zhCN_v2.5.2\\";
    String assetPath = w3xliniPath + "0x4\\E2AF578809778F1821BF50DB4ECC3BAD\\";
    ExcelImageInsert.set(assetPath, Client.class);

    List<Ability> abilityList = IniRead.read("template/Custom/ability.ini", assetPath + "table\\ability.ini",
        Ability.class);
    System.out.println("读取ability.ini完成");
    Map<String, AbilityDetail> abilityMap = AbilityParse.parse(abilityList);
    System.out.println("解析技能完成");
    List<Item> list = IniRead.read("template/Custom/item.ini", assetPath + "table\\item.ini", Item.class);
    System.out.println("读item.ini完成");
    List<Unit> unitList = IniRead.read("template/Custom/unit.ini", assetPath + "table\\unit.ini", Unit.class);
    System.out.println("读unit.ini完成");
    UnitParse unitParse = new UnitParse();
    Map<String, UnitDetail> idUnitMap = unitParse.parse(unitList);
    System.out.println("解析单位信息完成");

    List<Function> funList = new FunctionRead().read(assetPath + "map\\war3map.j还原256.j");

    handleJ(funList, idUnitMap, abilityMap);
  }

  public static void handleJ(List<Function> funList, Map<String, UnitDetail> idUnitMap,
      Map<String, AbilityDetail> abilityMap) throws IOException {
    List<String> ultiList = handleUlti(funList, idUnitMap);
    List<String> intros = handleIntro(funList, idUnitMap, abilityMap);

    try (
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
            new FileOutputStream("D:\\Code\\demo\\demo\\src\\main\\java\\com\\xi4\\custom\\hero0.ini"), "utf8"))) {
      for (String line : ultiList) {
        bw.write(line);
        bw.newLine();
      }
      for (String line : intros) {
        bw.write(line);
        bw.newLine();
      }
    }

  }

  // if GetUnitTypeId(GetTriggerUnit())=='H005' or
  // GetUnitTypeId(GetTriggerUnit())=='H034' and
  // GetUnitLevel(GetTriggerUnit())>=15 and
  // GetUnitAbilityLevel(GetTriggerUnit(),'A00X')<1 then
  private static Pattern ultiPat = Pattern.compile(
      "GetUnitTypeId\\(GetTriggerUnit\\(\\)\\)=='(\\w{4})'( or GetUnitTypeId\\(GetTriggerUnit\\(\\)\\)=='(\\w{4})')? and GetUnitLevel\\(GetTriggerUnit\\(\\)\\)>=15 and GetUnitAbilityLevel\\(GetTriggerUnit\\(\\),'(\\w{4})'\\)<1 ");

  public static List<String> handleUlti(List<Function> funList, Map<String, UnitDetail> idUnitMap) {
    List<String> ultiList = new ArrayList<>();
    ultiList.add("[ultis]");
    for (Function fun : funList) {
      List<String> rows = fun.getRows();
      for (String row : rows) {
        if (row.contains("' and GetUnitLevel(GetTriggerUnit())>=15 and")) {
          Matcher matcher = ultiPat.matcher(row);
          if (matcher.find()) {
            String id1 = matcher.group(1);
            String id2 = matcher.group(3);
            String ulti = matcher.group(4);

            UnitDetail u = idUnitMap.get(id1);
            if (u != null) {
              ultiList.add("-- " + u.getName());
            }
            ultiList.add(id1 + " = \"" + ulti + "\"");
            if (id2 != null) {
              ultiList.add(id2 + " = \"" + ulti + "\"");
            }
          } else {
            System.out.println("匹配大招但是解析失败！");
            System.out.println(row);
          }
        }
      }
    }
    return ultiList;
  }

  private static Pattern unitPat = Pattern.compile(" GetUnitTypeId\\(GetTriggerUnit\\(\\)\\)=='(\\w{4})' ");

  public static List<String> handleIntro(List<Function> funList, Map<String, UnitDetail> idUnitMap,
      Map<String, AbilityDetail> abilityMap) {
    List<String> introList = new ArrayList<>();
    List<String> units = new ArrayList<>();
    Map<String, String[]> teamMap = new HashMap<>();
    for (Function fun : funList) {
      List<String> rows = fun.getRows();
      for (int i = 0; i < rows.size(); i++) {
        String row = rows.get(i);
        if (row.contains("call DisplayTimedTextToPlayer(GetTriggerPlayer(),0,0,10.,\"|cffffcc00")) {
          String preRow = rows.get(i - 1);
          Matcher matcher = unitPat.matcher(preRow);
          if (preRow.contains(" GetUnitTypeId(GetTriggerUnit())=='") && matcher.find()) {
            String id1 = matcher.group(1);
            units.add(id1);

            String[] team = new String[2];
            team[0] = id1;
            UnitDetail u = idUnitMap.get(id1);
            for (UnitDetail t : idUnitMap.values()) {
              if (!t.getId().equals(id1) && u.getName().equals(t.getName()) && t.getHeroAbilList().length() > 0) {
                // 这是个皮肤
                team[1] = t.getId();
                break;
              }
            }
            teamMap.put(id1, team);
          } else {
            System.out.println("匹配简介但是解析失败！");
            System.out.println(preRow);
          }
        }
      }
    }

    // 路西法皮肤设置
    {
      teamMap.get("E013")[1] = "E053";
      AbilityDetail abi = new AbilityDetail();
      abilityMap.put("123", abi);
      abi.setName("黑暗舞者的皮肤");
      abi.setUbertip("|cffff0000增强内容：|r|cffcc99ff|n增加攻击速度和移动速度，转为远程攻击|n1.幽夜舞冲刺原地不动，常驻减速，伤害+60%，冷却时间减少|n2.殇魂引伤害+33%|n3.大招常驻 2级大招新增被动：1技能30%概率刷新cd 3级大招新增被动：对暗属性敌人增伤25% 4级大招新增被动：33%概率免疫致死伤害并且直接满血|n**含有隐藏内容|r");
    }

    introList.add("[intros]");
    for (Function fun : funList) {
      List<String> rows = fun.getRows();
      for (int i = 0; i < rows.size(); i++) {
        String row = rows.get(i);
        if (row.contains("call DisplayTimedTextToPlayer(GetTriggerPlayer(),0,0,10.,\"|cffffcc00")) {
          String preRow = rows.get(i - 1);
          Matcher matcher = unitPat.matcher(preRow);
          if (preRow.contains(" GetUnitTypeId(GetTriggerUnit())=='") && matcher.find()) {
            String id1 = matcher.group(1);
            introList.add(id1 + " = [=[");

            String first = row.substring(row.indexOf("\"") + 1);
            introList.add(first);

            for (int j = i + 1; j < rows.size(); j++) {
              String content = rows.get(j);
              if (!content.endsWith("\")")) {
                introList.add(content);
              } else {
                String last = content.substring(0, content.length() - 2);
                introList.add(last);
                break;
              }
            }
            introList.add("]=]");

            // 对皮肤进行增强
            String[] team = teamMap.get(id1);
            if (team[1] != null) {
              UnitDetail u = idUnitMap.get(id1);
              UnitDetail t = idUnitMap.get(team[1]);
              // 这是个皮肤，可以添加增强内容
              String heroName = u.getName();
              for (AbilityDetail abi : abilityMap.values()) {
                String abiZengqiang = abi.getName();
                if (abiZengqiang.contains(heroName) && abiZengqiang.contains("的皮肤")) {
                  introList.add(t.getId() + " = \"" + abi.getUbertip() + "\"");
                }
              }
            }
          } else {
            System.out.println("单位信息未找到 " + preRow);
          }
        }

      }
    }

    // 追加英雄组
    introList.add("[heros]");
    for (String id : units) {
      StringBuilder buf = new StringBuilder();
      UnitDetail u = idUnitMap.get(id);
      if (u != null) {
        String[] team = teamMap.get(id);
        if (team[1] != null) {
          buf.append("\"").append(team[1]).append("\"");
        }
        introList.add("-- " + u.getName());
      } else {
        System.out.println("单位信息未找到 " + id);
      }
      introList.add(id + " = " + buf.toString());
    }

    // 追加story
    introList.add("[stories]");
    for (String id : units) {
      UnitDetail u = idUnitMap.get(id);
      if (u != null) {
        introList.add("-- " + u.getName());
      }
      introList.add(id + " = [=[");
      introList.add("");
      introList.add("]=]");
    }

    return introList;
  }
}
