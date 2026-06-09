package com.mp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.*;
import java.util.stream.Collectors;

import com.common.ini.Ability;
import com.common.ini.IniRead;
import com.common.ini.Unit;
import com.common.parse.Function;
import com.common.util.MapUtil;
import com.mp.function.hero.FunctionHeroDaZhaoAndCore;
import com.mp.function.hero.FunctionHeroJianjieOrDaZhao;
import com.mp.function.hero.FunctionHeroMainProp;
import com.mp.function.hero.FunctionHeroName;
import com.mp.function.hero.Hero;
import com.mp.parse.AbilityDetail;
import com.mp.parse.AbilityParse;
import com.mp.parse.ExcelImageInsert;
import com.mp.parse.FunctionRead;
import com.mp.parse.UnitDetail;
import com.mp.parse.UnitParse;
import com.mp.sqlite.SqLiteJDBC;

public class Helper {
  public static void main(String[] args) throws Exception {
    SqLiteJDBC.setVersion("v1.5.1", "v1.4.3");

    String w3xliniPath = "D:\\war5-jass\\jass_plugin\\w3x2lni_zhCN_v2.5.2\\w3x2lni_zhCN_v2.5.2\\";
    String assetPath = w3xliniPath + "0mp\\E283F124FB2723BD153989F8150EA39F\\";
    ExcelImageInsert.set(assetPath, ClientWithJ.class);

    List<Unit> unitList = IniRead.read("template/Custom/unit.ini", assetPath + "table\\unit.ini", Unit.class);
    System.out.println("读unit.ini完成");
    UnitParse unitParse = new UnitParse();
    Map<String, UnitDetail> idUnitMap = unitParse.parse(unitList);
    System.out.println("解析单位信息完成");

    List<Ability> abilityList = IniRead.read("template/Custom/ability.ini", assetPath + "table\\ability.ini",
        Ability.class);
    System.out.println("读取ability.ini完成");
    Map<String, AbilityDetail> abilityMap = AbilityParse.parse(abilityList);
    System.out.println("解析技能完成");

    List<Function> funList = new FunctionRead().read(assetPath + "map\\war3map.j还原256.j");
    System.out.println("读取j文件完成");

    handleJ(funList, idUnitMap, abilityMap);

    parse专属灵格(abilityMap);
    System.out.println();
  }

  public static void handleJ(List<Function> funList, Map<String, UnitDetail> unitMap,
      Map<String, AbilityDetail> abilityMap) throws Exception {
    List<String> lStrings = generateLingge(funList, abilityMap);
    List<String> ultiList = hero(unitMap, abilityMap, funList);
    try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
        new FileOutputStream("D:\\Code\\demo\\demo\\src\\main\\java\\com\\mp\\custom\\灵格.ini"), "utf8"))) {
      for (String row : lStrings) {
        bw.write(row);
        bw.newLine();
        ;
      }
    }

    try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
        new FileOutputStream("D:\\Code\\demo\\demo\\src\\main\\java\\com\\mp\\custom\\hero0.ini"), "utf8"))) {
      for (String row : ultiList) {
        bw.write(row);
        bw.newLine();
        ;
      }
    }
  }

  // set udg_CORE[LoadInteger(YDLOC, GetHandleId(GetTriggeringTrigger()) +
  // ydl_localvar_step * 0x80000, 0x25DAB820)]='A0DT'
  private static Pattern lingePat = Pattern.compile("set udg_CORE\\[.*\\]='(\\w{4})'");
  private static Pattern lingePatLevel = Pattern.compile("set udg_LIMITMAX\\[(\\d)\\]");

  public static List<String> generateLingge(List<Function> funList, Map<String, AbilityDetail> abilityMap) {
    List<String> lingList = new ArrayList<>();
    lingList.add("[灵格-普通]");
    for (Function fun : funList) {
      if (fun.getName().equals("Trig_1________________uActions")) {
        List<String> rows = fun.getRows();
        for (String row : rows) {
          Matcher matcher;
          boolean coreFlag = row.contains("set udg_CORE[LoadInteger");
          boolean levelFlag = row.contains("set udg_LIMITMAX");
          if (coreFlag && (matcher = lingePat.matcher(row)).find()) {
            String magicId = matcher.group(1);
            AbilityDetail magicAbi = abilityMap.get(magicId);
            if (magicAbi != null) {
              String name = "\"" + magicAbi.getName() + "\"";
              lingList.add(magicId + " = " + name);
            } else {
              System.out.println("灵格解析失败2");
            }
          } else if (levelFlag && (matcher = lingePatLevel.matcher(row)).find()) {
            String level = matcher.group(1);
            switch (level) {
              case "1":
                lingList.add("[灵格-稀有]");
                break;
              case "2":
                lingList.add("[灵格-勇者]");
                break;
              case "3":
                lingList.add("[灵格-神器]");
                break;
              case "4":
                lingList.add("[灵格-史诗]");
                break;
              default:
                break;
            }
          } else if (coreFlag || levelFlag) {
            System.out.println("灵格解析失败3");
          }

        }
        break;
      }
    }
    return lingList;
  }

  // 基础英雄：英雄选择小助手
  public static Map<String, String> pMap = new LinkedHashMap<>();
  static {
    pMap.put("O000", "O00S");
    pMap.put("O001", "O00I");
    pMap.put("E011", null);
    pMap.put("N02D", "O00T");
    pMap.put("O003", null);
    pMap.put("O004", "O00K");
    pMap.put("E01H", null);
    pMap.put("E004", "E01S");
    pMap.put("E008", "O00Q");
    pMap.put("H006", "O00E");
    pMap.put("O002", null);
    pMap.put("E015", "O00Z");
    pMap.put("N06G", "O00G");
    pMap.put("O00M", null);
    pMap.put("H007", "O00L");
    pMap.put("H00H", "O011");
    pMap.put("E014", "O01A");
    pMap.put("H005", "O010");
    pMap.put("E016", "O00U");
    pMap.put("H00S", null);
    pMap.put("H00B", null);
    pMap.put("H00U", null);
    pMap.put("O00X", null);
    pMap.put("O012", null);
    pMap.put("O014", null);
    pMap.put("O015", null);
    pMap.put("E01Q", null);
    pMap.put("E01R", null);
    pMap.put("H00Y", null);
  }

  public static List<String> hero(Map<String, UnitDetail> unitMap, Map<String, AbilityDetail> abilityMap,
      List<Function> funList) {

    unitMap.remove("O016");
    unitMap.remove("E01M");

    Map<String, Function> funMap = funList.stream()
        .collect(Collectors.toMap(Function::getName, java.util.function.Function.identity()));
    Map<String, Hero> heroMap = new HashMap<>();
    Map<String, UnitDetail> unitHeroMap = new HashMap<>();
    for (UnitDetail unit : unitMap.values()) {
      if ("giant".equals(unit.getType()) && ("111".equals(unit.getPoints()) || "222".equals(unit.getPoints()))) {
        unitHeroMap.put(unit.getId(), unit);
      }
    }

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

    readGuanjing();
    readJianjie();

    Map<String, String[]> udgHeroTeam = new HashMap<>();

    List<String> ultiList = new ArrayList<>();
    ultiList.add("[ultis]");
    // 增强内容
    List<String> introList = new ArrayList<>();
    introList.add("[intros]");
    List<String> heroList = new ArrayList<>();
    heroList.add("[heros]");

    List<String> units = new ArrayList<>();
    for (String uid : unitHeroMap.keySet()) {
      UnitDetail hero = unitHeroMap.get(uid);
      String id = hero.getId();
      // 主要属性
      String udgheronameAll = udgHeroNameMap.get(id);
      if (udgheronameAll == null) {
        System.out.println("基本英雄ID不能未找到：" + id);
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

      if (baseUserId.equals(id)) {
        units.add(baseUserId);
        String[] team = MapUtil.getNotNull(udgHeroTeam, baseUserId, () -> new String[2]);
        team[0] = id;
      } else {
        String[] team = MapUtil.getNotNull(udgHeroTeam, baseUserId, () -> new String[2]);
        team[1] = id;
      }

    }
    // 添加简介和皮肤增强
    for (String baseUserId : pMap.keySet()) {
      String[] team = udgHeroTeam.get(baseUserId);
      // 主要属性
      {
        String udgheronameAll = udgHeroNameMap.get(baseUserId);
        if (udgheronameAll == null) {
          System.out.println("基本英雄ID不能未找到：" + baseUserId);
          continue;
        }
        // 静态大招
        List<String> staticDazhaoList = MapUtil.getNotNull(udgHeroJianjieOrDaZhaoMap, baseUserId, ArrayList::new);
        // 大招 List<Map<udg_ROCK|O004, ability>>
        String dazhao = MapUtil.getNotNull(udgHeroDazhaMap, baseUserId, () -> udgHeroDazhaMap.get(udgheronameAll));
        if (dazhao != null) {
          staticDazhaoList.add(dazhao);
        }
        // heroAbilist

        UnitDetail hero = unitHeroMap.get(baseUserId);
        staticDazhaoList.addAll(Arrays.asList(hero.getHeroAbilList().split(",")));
        Hero tmp = new Hero();
        tmp.setAbilityMap(abilityMap);
        tmp.addAbilitys(staticDazhaoList);
        AbilityDetail t = tmp.getT();
        if (t == null) {
          System.out.println("未找到大招：" + baseUserId);
          throw new RuntimeException("未找到大招：" + baseUserId);
        }

        ultiList.add("-- " + hero.getPropernames());
        String ulti = "\"" + t.getId() + "\"";
        ultiList.add(baseUserId + " = " + ulti);

        // 简介
        String intro = introMap.get(baseUserId);
        introList.add("-- " + hero.getPropernames());
        introList.add(baseUserId + " = " + intro);
      }
      if (team[1] != null) {
        String pId = team[1];
        String udgheronameAll = udgHeroNameMap.get(pId);

        // 静态大招
        List<String> staticDazhaoList = MapUtil.getNotNull(udgHeroJianjieOrDaZhaoMap, pId, ArrayList::new);
        // 大招 List<Map<udg_ROCK|O004, ability>>
        String dazhao = MapUtil.getNotNull(udgHeroDazhaMap, pId, () -> udgHeroDazhaMap.get(udgheronameAll));
        if (dazhao != null) {
          staticDazhaoList.add(dazhao);
        }
        // heroAbilist

        UnitDetail hero = unitHeroMap.get(pId);
        staticDazhaoList.addAll(Arrays.asList(hero.getHeroAbilList().split(",")));
        Hero tmp = new Hero();
        tmp.setAbilityMap(abilityMap);
        tmp.addAbilitys(staticDazhaoList);
        AbilityDetail t = tmp.getT();
        if (t == null) {
          System.out.println("未找到大招：" + pId);
          throw new RuntimeException("未找到大招：" + pId);
        }

        // 大招 List<Map<udg_ROCK|O004, ability>>
        String ulti = "\"" + t.getId() + "\"";
        ultiList.add(pId + " = " + ulti);

        // 增强内容
        UnitDetail pu = unitMap.get(pMap.get(baseUserId));
        String intro = "";
        if (pu != null) {
          intro = "\"" + pu.getUbertip().replace("|n|n|cffd6d5b7点击选择该单位开始游玩。|r", "") + "\"";
        } else {
          System.out.println("未找到增强内容 " + pId);
        }
        introList.add(pId + " = " + intro);
      }
    }

    // 追加英雄组信息heros
    for (String id : pMap.keySet()) {
      String[] team = udgHeroTeam.get(id);
      UnitDetail u = unitMap.get(id);
      heroList.add("-- " + u.getPropernames());
      StringBuilder buf = new StringBuilder();
      if (team[1] != null) {
        buf.append("\"").append(team[1]).append("\"");
      }
      heroList.add(id + " = " + buf.toString());
    }

    // 追加凝空光晶
    heroList.add("[magiccores]");
    for (String id : pMap.keySet()) {
      UnitDetail baseHero = unitMap.get(id);
      String ubertip = guanjingMap.get(id);
      heroList.add("-- " + baseHero.getPropernames());
      heroList.add(id + " = \"" + ubertip + "\"");
    }

    // 追加stoies
    heroList.add("[stories]");
    for (String id : units) {
      UnitDetail u = unitMap.get(id);
      if (u != null) {
        heroList.add("-- " + u.getPropernames());
      }
      heroList.add(id + " = [=[");
      heroList.add("");
      heroList.add("]=]");
    }

    // ultiList introList heroList
    ultiList.addAll(introList);
    ultiList.addAll(heroList);
    return ultiList;

  }

  static Map<String, String> guanjingMap = new HashMap<>();
  static Map<String, String> introMap = new HashMap<>();

  public static void readGuanjing() {
    Set<String> keys = pMap.keySet();
    int i = 1;
    Map<Integer, String> map = new HashMap<>();
    for (String key : keys) {
      map.put(i++, key);
    }

    Pattern p = Pattern.compile("(\\d+)\\s*=\\s*\"(.*)\",?");
    try (BufferedReader br = new BufferedReader(
        new InputStreamReader(new FileInputStream("D:\\Code\\demo\\demo\\src\\main\\java\\com\\mp\\custom\\凝空光晶.txt"),
            "utf8"))) {
      String line = null;
      while ((line = br.readLine()) != null) {
        line = line.trim();
        Matcher matcher = p.matcher(line);
        if (matcher.find()) {
          int index = Integer.parseInt(matcher.group(1));
          String value = matcher.group(2);
          guanjingMap.put(map.get(index), value);
        }
      }
    } catch (Exception e) {
      System.out.println("读取凝空光晶异常处理");
    }
  }

  public static void readJianjie() {
    Set<String> keys = pMap.keySet();
    int i = 1;
    Map<Integer, String> map = new HashMap<>();
    for (String key : keys) {
      map.put(i++, key);
    }

    Map<String, String> headMap = new HashMap<>();
    Map<String, String> valueMap = new HashMap<>();

    boolean headFlag = true;
    Pattern p = Pattern.compile("(\\d+)\\s*=\\s*\"(.*)\",?");
    try (BufferedReader br = new BufferedReader(
        new InputStreamReader(new FileInputStream("D:\\Code\\demo\\demo\\src\\main\\java\\com\\mp\\custom\\简介.txt"),
            "utf8"))) {
      String line = null;
      while ((line = br.readLine()) != null) {
        line = line.trim();
        Matcher matcher = p.matcher(line);
        if (line.startsWith("Ubertip")) {
          headFlag = false;
        }
        if (matcher.find()) {
          int index = Integer.parseInt(matcher.group(1));
          String value = matcher.group(2);
          if (headFlag) {
            headMap.put(map.get(index), value);
          } else {
            valueMap.put(map.get(index), value);
          }
        }
      }
    } catch (Exception e) {
      System.out.println("读取简介异常处理");
    }

    // 拼接头和内容
    for (String key : keys) {
      String head = headMap.get(key);
      String value = valueMap.get(key);

      introMap.put(key, "\"" + head + value + "\"");
    }
  }

  public static void parse专属灵格(Map<String, AbilityDetail> abilityMap) throws Exception {
    List<String> list = new ArrayList<>();
    Pattern p = Pattern.compile("(\\d+)\\s*=\\s*(\\w{4})?\\s*");
    try (BufferedReader br = new BufferedReader(
        new InputStreamReader(Client.class.getResourceAsStream("custom/专属灵格.txt")))) {
      String line = null;
      while ((line = br.readLine()) != null) {
        line = line.trim();
        Matcher matcher = p.matcher(line);
        if (matcher.find()) {
          String no = matcher.group(1);
          String id = matcher.group(2);
          if (id != null) {
            AbilityDetail abilityDetail = abilityMap.get(id);
            String desc = abilityDetail.getUbertip();
            list.add(no + " = \"" + desc + "\"");
          }else{
            list.add(no + " = \"\"");
          }
        }
      }
    }
    try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
        new FileOutputStream("D:\\Code\\demo\\demo\\src\\main\\java\\com\\mp\\custom\\专属灵格.ini"), "utf8"))) {
      for (String row : list) {
        bw.write(row);
        bw.newLine();
      }
    }

  }
}
