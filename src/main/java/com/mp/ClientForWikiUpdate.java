package com.mp;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import com.alibaba.fastjson.JSONObject;
import com.common.ini.Ability;
import com.common.ini.Destructable;
import com.common.ini.IniRead;
import com.common.ini.Item;
import com.common.ini.Unit;
import com.mp.parse.AbilityDetail;
import com.mp.parse.AbilityParse;
import com.mp.parse.DestructableDetail;
import com.mp.parse.DestructableParse;
import com.mp.parse.ExcelImageInsert;
import com.mp.parse.ItemDetail;
import com.mp.parse.ItemParse;
import com.mp.parse.UnitDetail;
import com.mp.parse.UnitParse;
import com.mp.sqlite.SqLiteJDBC;
import com.mp.wiki.HeroJson;

public class ClientForWikiUpdate {
  public static void main(String[] args) throws Exception {

    String excelName = "梦想远景装备介绍_v1.8.2.xlsx";
    SqLiteJDBC.setVersion("v1.8.2", "v1.7.2");

    String w3xliniPath = "D:\\war5-jass\\jass_plugin\\w3x2lni_zhCN_v2.5.2\\w3x2lni_zhCN_v2.5.2\\";
    String assetPath = w3xliniPath + "0mp\\FE4EDA8C6CC7212BF8C63B3BA6E3915B\\";
    ExcelImageInsert.set(assetPath, Client.class);
    List<Ability> abilityList = IniRead.read("template/Custom/ability.ini", assetPath + "table\\ability.ini",
        Ability.class);
    System.out.println("读取ability.ini完成");

    List<Item> list = IniRead.read("template/Custom/item.ini", assetPath + "table\\item.ini", Item.class);
    System.out.println("读item.ini完成");
    ItemParse itemParse = new ItemParse();
    Map<String, ItemDetail> idItemMap = itemParse.parse(list);
    System.out.println("解析物品完成");

    List<Unit> unitList = IniRead.read("template/Custom/unit.ini", assetPath + "table\\unit.ini", Unit.class);
    System.out.println("读unit.ini完成");
    UnitParse unitParse = new UnitParse();
    Map<String, UnitDetail> idUnitMap = unitParse.parse(unitList);
    System.out.println("解析单位信息完成");

    List<Destructable> destructableList = IniRead.read("template/Custom/destructable.ini",
        assetPath + "table\\destructable.ini", Destructable.class);
    System.out.println("读destructable.ini完成");
    DestructableParse destructableParse = new DestructableParse();
    Map<String, DestructableDetail> destructableMap = destructableParse.parse(destructableList);
    System.out.println("解析箱子完成");

    Map<String, AbilityDetail> abilityMap = AbilityParse.parse(abilityList);
    System.out.println("解析技能完成");

    String unitid = "O016";
    String name = "彻寒冰拳";
    UnitDetail detail = idUnitMap.get(unitid);
    String abils = detail.getHeroAbilList() + ",";
    String[] abilList = abils.split(",");

    HeroJson.Hero h = new HeroJson.Hero();
    h.setName(detail.getName());
    h.setPropernames(clear(detail.getPropernames()));
    h.setAtkType1(detail.getAtkType1());
    // h.setAtkType1(UnitParse.convertAtkType(detail.getUnit().getAtkType1()));
    h.setCool1(Double.parseDouble(detail.getCool1()));
    h.setSpd(Integer.parseInt(detail.getSpd()));

    for (String abil : abilList) {
      AbilityDetail d = abilityMap.get(abil);
      if (d == null) {
        System.out.println("d==null,id=" + abil);
      }
      // clear name
      d.setName(d.getName().replaceAll("--\\[\\w+\\]", ""));
      h.getAbils().add(new HeroJson.Ability(d));
      String fileName = "技能-" + name + "-" + d.getHotkey() + d.getName() + ".png";
      File f = new File("wikiimageabils\\" + fileName);
      if (!f.exists()) {
        ExcelImageInsert.convertImageToPng(d.getArt().replace(".tga", ".blp"), f);
      }
    }
    System.out.println(JSONObject.toJSONString(h, true));

    {
      String fileName = "英雄-头像-" + name + ".png";
      File f = new File("wikiimageheros\\" + fileName);
      if (!f.exists()) {
        ExcelImageInsert.convertImageToPng(detail.getArt().replace(".tga", ".blp"), f);
      }
      FileUtils.copyFile(f, new File("wikiupdate\\" + fileName));
    }

    // 物品
    String item = "phlt,engs,kgal,thdm";
    String[] items = item.split(",");
    for (String it : items) {
      ItemDetail d = idItemMap.get(it);

      String fileName = "物品-" + d.getName() + ".png";
      System.out.println(JSONObject.toJSONString(d, true));
      File f = new File("wikiimages\\" + fileName);
      if (!f.exists()) {
        ExcelImageInsert.convertImageToPng(d.getIcon().replace(".tga", ".blp"), f);
      }
      FileUtils.copyFile(f, new File("wikiupdate\\" + fileName));

    }

    // 灵格
    String ling = "A163";
    String[] lings = ling.split(",");
    for (String li : lings) {
      AbilityDetail a = abilityMap.get(li);

      // - |cff00ffff熔岩龟|r -
      Pattern p = Pattern.compile("- (\\|cff(\\w{0,6}))?(.*?)(\\|r)? -");
      Matcher matcher = p.matcher(a.getName());
      if (matcher.find()) {
        name = matcher.group(3);
      }
      
      String fileName = "灵格-" + name + ".png";
      System.out.println(JSONObject.toJSONString(a, true));
      File f = new File("wikiimages\\" + fileName);
      if (!f.exists()) {
        ExcelImageInsert.convertImageToPng(a.getArt().replace(".tga", ".blp"), f);
      }
      FileUtils.copyFile(f, new File("wikiupdate\\" + fileName));
    }

  }

  public static void updateType(Map<String, List<ItemDetail>> itMap, String type) {
    for (String key : itMap.keySet()) {
      List<ItemDetail> list2 = itMap.get(key);
      for (ItemDetail itemDetail : list2) {
        if (itemDetail != null) {
          itemDetail.setType(type);
        }
      }
    }
  }

  public static String clear(String str) {
    return str.replaceAll("\\|cff\\w{6}", "").replaceAll("\\|r", "");
  }
}
