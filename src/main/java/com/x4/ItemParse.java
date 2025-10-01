package com.x4;

import java.io.InputStream;
import java.math.RoundingMode;
import java.text.Collator;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.abc.ItemDetail.ShopDrop;

import lombok.Data;

@Data
public class ItemParse {

  private Map<String, ItemDetail> itemMap = new HashMap<>();
  private Map<String, List<ItemDetail>> levelClassListMap = new HashMap<>();

  private static int getValue(String level) {
    int value = 0;
    switch (level) {
      case "F":
        value = 10;
        break;
      case "F+":
        value = 11;
        break;
      case "E":
        value = 20;
        break;
      case "E+":
        value = 21;
        break;
      case "D":
        value = 30;
        break;
      case "D+":
        value = 31;
        break;
      case "C":
        value = 40;
        break;
      case "C+":
        value = 41;
        break;
      case "B":
        value = 50;
        break;
      case "B+":
        value = 51;
        break;
      case "A":
        value = 60;
        break;
      case "A+":
        value = 61;
        break;
      case "S":
        value = 70;
        break;
      case "S+":
        value = 71;
        break;
      case "SS":
        value = 80;
        break;
      case "SS+":
        value = 81;
        break;
      case "?":
        value = 90;
        break;
      default:
        value = 0;
        break;
    }
    return value;
  }

  private static String splitName(String name){
    return name.replaceAll("\\|cff\\w{6}", "").replaceAll("\\|r", "");
  }
  public Map<String, ItemDetail> parse(List<Item> items) {
    // 将装备和卷轴存到一起
    Map<String, ItemDetail> nameMap = new HashMap<>();
    items.forEach(e -> {
      String name = splitName(e.getName());
      ItemDetail itemDetail = new ItemDetail(name);
      nameMap.put(name, itemDetail);
      itemDetail.setId(e.getId());

      itemDetail.setPickRandom(e.getPickRandom());
      String levelClass = combineLeveClass(e);
      itemDetail.setLevelClass(levelClass);
      List<ItemDetail> list = MapUtil.getNotNull(levelClassListMap, levelClass, ArrayList::new);
      list.add(itemDetail);
      this.itemMap.put(e.getId(), itemDetail);
    });

    return this.itemMap;
  }

  private String combineLeveClass(Item e) {
    // // 物品分类 人造
    // ITEM_TYPE_ARTIFACT ConvertItemType(3) Artifact
    // // 物品分类 战役
    // ITEM_TYPE_CAMPAIGN ConvertItemType(5) Campaign
    // // 物品分类 永久
    // ITEM_TYPE_PERMANENT ConvertItemType(0) Permanent
    // // 物品分类 可购买
    // ITEM_TYPE_PURCHASABLE ConvertItemType(4) Purchasable
    // call CreateItemLoc(ChooseRandomItemExBJ(6,ITEM_TYPE_Campaign),

    String level = e.getLevel();
    if (level == null) {
      level = e.getOldLevel();
      if (level == null) {
        return null;
      }
    }

    String clazz = null;
    Map<String, String> others = e.getOthers();
    if (others == null) {
      return null;
    }
    clazz = others.get("class");
    if (clazz == null) {
      return null;
    }

    String levelClass = level + ",ITEM_TYPE_" + clazz.toUpperCase();
    return levelClass;
  }

  public static void main(String[] args) throws Exception {

    String w3xliniPath = "D:\\war5-jass\\jass_plugin\\w3x2lni_zhCN_v2.5.2\\w3x2lni_zhCN_v2.5.2\\";
    String assetPath = w3xliniPath + "0x4\\E2AF578809778F1821BF50DB4ECC3BAD\\";
    List<Item> list = new IniRead().read(w3xliniPath + "template/Custom/item.ini",
        assetPath + "table\\item.ini", Item.class);

    // 计算物品的随机概率
    ItemParse itemParse = new ItemParse();
    itemParse.parse(list);
    List<ItemDetail> levelClassList = itemParse.levelClassListMap.get("7,ITEM_TYPE_PURCHASABLE");
    int size = 0;
    for(ItemDetail t : levelClassList){
      if("1".equals(t.getPickRandom())){
        System.out.println(t.getId() + "\t" + t.getName());
        size++;
      }
    }
    System.out.println(size);

  }
}
