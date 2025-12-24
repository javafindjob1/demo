package com.dj;

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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.common.ini.Ability;
import com.common.ini.IniRead;
import com.common.ini.Item;
import com.common.ini.Unit;
import com.common.j.FunctionRead;
import com.common.parse.Function;
import com.common.util.MapUtil;
import com.dj.parse.AbilityDetail;
import com.dj.parse.AbilityParse;
import com.dj.parse.ExcelImageInsert;
import com.dj.parse.ItemDetail;
import com.dj.parse.ItemParse;
import com.dj.parse.UnitDetail;
import com.dj.parse.UnitParse;

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
            new FileOutputStream("D:\\Code\\demo\\demo\\src\\main\\java\\com\\0dj\\custom\\testout"),
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
    String assetPath = w3xliniPath + "0dj\\2C6CD3ABB0C45C38A14486A059CFECFA\\";
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

    List<Function> funList = new FunctionRead().read(assetPath + "map\\war3map.j");

    handleJ(funList, idUnitMap, abilityMap);
  }

  public static void handleJ(List<Function> funList, Map<String, UnitDetail> idUnitMap,
      Map<String, AbilityDetail> abilityMap) throws IOException {
    List<String> ultiList = handleUlti(funList, idUnitMap);
    List<String> intros = handleIntro(funList, idUnitMap, abilityMap);

    try (
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
            new FileOutputStream("D:\\Code\\demo\\demo\\src\\main\\java\\com\\dj\\custom\\hero0.ini"), "utf8"))) {
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

  private static Pattern ultiPat = Pattern.compile("call UnitAddAbility\\(gg_unit_(\\w{4})_\\w+,'(\\w{4})'\\)");

  public static List<String> handleUlti(List<Function> funList, Map<String, UnitDetail> idUnitMap) {
    List<String> ultiList = new ArrayList<>();
    ultiList.add("[ultis]");
    Map<String, Set<String>> ultiMap = new HashMap<>();
    for (Function fun : funList) {
      List<String> rows = fun.getRows();
      for (String row : rows) {
        if (row.startsWith("call UnitAddAbility(gg_unit")) {
          Matcher matcher = ultiPat.matcher(row);
          if (matcher.find()) {
            String id1 = matcher.group(1);
            String ulti = matcher.group(2);

            Set<String> list = MapUtil.getNotNull(ultiMap, id1, HashSet::new);
            list.add(ulti);
          } else {
            System.out.println("匹配大招但是解析失败！");
            System.out.println(row);
          }
        }
      }
    }

    for (Entry<String, Set<String>> entry : ultiMap.entrySet()) {
      String id = entry.getKey();
      Set<String> list = entry.getValue();

      UnitDetail u = idUnitMap.get(id);
      if (u != null) {
        ultiList.add("-- " + u.getName());
      }

      StringBuilder buf = new StringBuilder();
      for (String abi : list) {
        buf.append(abi).append(",");
      }
      buf.setLength(buf.length() - 1);
      ultiList.add(id + " = \"" + buf + "\"");
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
