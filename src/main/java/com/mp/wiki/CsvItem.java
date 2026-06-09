package com.mp.wiki;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.management.relation.RoleList;

import com.alibaba.fastjson.JSONObject;
import com.common.util.MapUtil;
import com.mp.function.HeroData;
import com.mp.function.HeroData.ViewData;
import com.mp.function.hero.Hero;
import com.mp.parse.ExcelImageInsert;
import com.mp.parse.ItemDetail;

public class CsvItem extends Csv {
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
    for (Entry<String, List<ItemDetail>> entry : map.entrySet()) {
      List<ItemDetail> list = entry.getValue();
      for (ItemDetail detail : list) {
        if (detail == null || detail.getId() == null) {
          System.out.println("null id");
        }
        if (detail.getId().equals("ocor")) {
          detail.setName("终焉之剑");
        }
        String nameOri = detail.getName();
        int count = MapUtil.getNotNull(itemCountMap, nameOri, () -> 0);
        itemCountMap.put(nameOri, count + 1);
        String newname = nameOri;
        if (count > 0) {
          newname += count;
        }

        ItemJson json = new ItemJson();
        json.setType(subname);
        if (!nameOri.equals(newname)) {
          List<String> label = new ArrayList<>();
          label.add(nameOri);
          json.setLabel(label);
        }
        json.setLevel(format(detail.getLevel().split("·")[0]));
        json.setLevelVal(detail.getLevelInt());
        json.setName(newname);

        String str = detail.getDescription();
        if (str.length() > 400) {
          json.setProp(str.substring(0, 400));
          json.setProp2(str.substring(400, Math.min(800, str.length())));
        } else {
          json.setProp(str);
        }

        String dropPlace = detail.getDropPlace();
        dropPlace = dropPlace == null ? "" : dropPlace.trim();
        if (dropPlace.length() > 0) {
          json.setGetFrom(format(dropPlace));
        }

        String formula = detail.getSynthesisFormula();
        formula = formula == null ? "" : formula.trim();
        if (formula.length() > 0) {
          json.setGetFrom((json.getGetFrom() == null ? "" : json.getGetFrom() + "\n") + format(formula));

          if (formula.contains("樱")) {
            if (json.getLabel() == null) {
              json.setLabel(new ArrayList<>());
            }
            json.getLabel().add("樱");
          }
        }

        json.setMark(format(detail.getMark()));
        json.setID(detail.getId());

        String fileName = "物品-" + newname + ".png";
        File f = new File("wikiimages\\" + fileName);
        if (!f.exists()) {
          ExcelImageInsert.convertImageToPng(detail.getIcon().replace(".tga", ".blp"), f);
        }

        boolean append = false;
        try (BufferedWriter wr = new BufferedWriter(
            new OutputStreamWriter(new FileOutputStream("wiki//" + subname + "//" + newname + ".json", append),
                "UTF8"))) {
          wr.write(JSONObject.toJSONString(json, true));
        }
      }

    }

  }
}
