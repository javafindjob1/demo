package com.mp.wiki;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSONObject;
import com.common.util.MapUtil;
import com.mp.function.HeroData;
import com.mp.function.HeroData.ViewData;
import com.mp.function.hero.Hero;
import com.mp.parse.ItemDetail;

public class CsvTaozhuang extends Csv {
  // 禁止出现| 防止wiki语法冲突

  public static void insert(String mapName, String subname, Map<String, List<ItemDetail>> map,
      Map<String, HeroData> heroMap, Map<String, Hero[]> heroArrMap)
      throws UnsupportedEncodingException, FileNotFoundException, IOException {

    // 物品 对应 英雄名字
    Map<String, List<String>> itemHeroMap = new HashMap<>();
    // O00R:狂战士1
    Map<String, String> heroNameMap = new HashMap<>();
    for (Hero[] heros : heroArrMap.values()) {
      String heroName = heros[0].getUnit().getPropernames().replaceAll("\\|cff\\w{6}", "").replaceAll("\\|r", "");
      heroNameMap.put(heros[0].getId(), heroName);
      for (int i = 1; i < heros.length; i++) {
        Hero hero = heros[i];
        if (hero == null) {
          break;
        }
        heroNameMap.put(hero.getId(), heroName + i);
      }
    }

    for (HeroData hero : heroMap.values()) {
      List<ViewData> items = hero.getItems();
      String heroName = heroNameMap.get(hero.getUnitId());
      for (ViewData item : items) {
        List<String> list = MapUtil.getNotNull(itemHeroMap, item.getName(), ArrayList::new);
        list.add(heroName);
      }
    }
    // 同名装备处理 木雕神像
    Map<String, Integer> itemCountMap = new HashMap<>();

    // 文件 物品-猩红杖.png
    // ID
    // 名称
    // 基本属性
    // 特殊属性
    // 故事属性
    // 品质
    // 分类
    // 获取方式
    // 点评
    StringBuilder buf = new StringBuilder();
    buf.append("levelVal,level,name,prop,prop2,getFrom,mark,type,hero,ID\n");

    int i = 0;
    for (Entry<String, List<ItemDetail>> entry : map.entrySet()) {
      List<ItemDetail> list = entry.getValue();
      Iterator<ItemDetail> iterator = list.iterator();
      iterator.hasNext();
      ItemDetail first = iterator.next();
      List<String> type = new ArrayList<>();
      while (iterator.hasNext()) {
        ItemDetail detail2 = iterator.next();
        type.add(detail2.getName());
      }

      if (first == null || first.getId() == null) {
        System.out.println("null id");
      }
      if (first.getId().equals("ocor")) {
        first.setName("终焉之剑");
      }
      String nameOri = first.getName();
      int count = MapUtil.getNotNull(itemCountMap, nameOri, () -> 0);
      itemCountMap.put(nameOri, count + 1);
      String newname = nameOri;
      if (count > 0) {
        newname += count;
      }
      i += 3;

      ItemJson json = new ItemJson();
      json.setType(subname);
      json.setLabel(type);
      json.setLevel(format(first.getLevel().split("·")[0]));
      json.setLevelVal(first.getLevelInt());
      json.setName(newname);

      String str = first.getDescription();
      if (str.length() > 400) {
        json.setProp(str.substring(0, 400));
        json.setProp2(str.substring(400, Math.min(800, str.length())));
      } else {
        json.setProp(str);
      }
      json.setMark(format(first.getMark()));
      boolean append = false;
      try (BufferedWriter wr = new BufferedWriter(
          new OutputStreamWriter(new FileOutputStream("wiki//" + subname + "//" + newname + ".json", append), "UTF8"))) {
        wr.write(JSONObject.toJSONString(json, true));
      }

    }

  }
}
