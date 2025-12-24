package com.dj.parse;

import static org.junit.Assert.assertNotNull;

import java.text.Collator;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.common.ini.IDropTrigger;
import com.common.ini.Item;
import com.common.ini.UnitGroup;
import com.common.parse.AbstractParse;
import com.common.parse.DropInfo;
import com.common.parse.ItemAccessories;
import com.common.util.MapUtil;
import com.dj.Client;
import com.dj.parse.ItemDetail.UnitDrop;

import lombok.Data;

@Data
public class ItemParse extends AbstractParse {

  private Map<String, ItemDetail> itemMap;
  private Map<String, ItemDetail> nameMap;

  private static int getValue(String level) {
    int value = 0;
    switch (level) {
      case "劣质":
        value = 10;
        break;
      case "良好":
        value = 11;
        break;
      case "普通":
        value = 12;
        break;
      case "优越":
        value = 13;
        break;
      case "稀有":
        value = 14;
        break;
      case "究极":
        value = 15;
        break;
      case "礼物":
        value = 30;
        break;
      case "传说":
        value = 40;
        break;
      case "灵魂":
      case "人造灵魂":
      case "灵魂(Earl's)":
      case "灵魂(Earl's level)":
      case "灵魂(God's level)":
      case "灵魂(King's level)":
      case "灵魂(Demon's level)":
        value = 50;
        break;
      case "神具":
        value = 60;
        break;
      case "★":
        value = 71;
        break;
      case "★★":
        value = 72;
        break;
      case "★★★":
        value = 73;
        break;
      case "★★★★":
        value = 74;
        break;
      case "★★★★★":
        value = 75;
        break;
      default:
        value = 0;
        break;
    }
    return value;
  }

  private static Pattern colorPattern = Pattern.compile("\\|[cffCFF]{3}\\w{3,6}");
  private static Pattern singlePattern = Pattern.compile("(?<![\\|\\w])([nr])(?!\\w)");
  private static Pattern doublePattern = Pattern.compile("\\|\\|");

  public String parseDescription(String id, String description) {
    {
      // 将|Cff之类的替换成小写
      StringBuffer sb = new StringBuffer();
      Matcher matcher = colorPattern.matcher(description);
      while (matcher.find()) {
        String color = matcher.group();
        matcher.appendReplacement(sb, color.toLowerCase());
      }
      matcher.appendTail(sb);
      description = sb.toString();
    }
    {
      // 补充少的竖杆
      StringBuffer sb = new StringBuffer();
      Matcher matcher = singlePattern.matcher(description);
      while (matcher.find()) {
        String n = matcher.group(1);
        matcher.appendReplacement(sb, "|" + n);
      }
      matcher.appendTail(sb);
      description = sb.toString();
    }
    description = description.replace("品质;", "品质:");
    description = description.replace("品质：", "品质:");
    if (id.equals("I0GJ")) {
      description = description.replace("灵魂(|r|cffff1493（Demon's Level)", "灵魂(|r|cffff1493Demon's Level)");
    }else if(id.equals("I0DW")){
      description = description.replace("能力|n品质:限制的爱(A级)Cd:3)传说", "能力:限制的爱(A级)Cd:3)|n品质:传说");
    }

    {
      // 删除多与的竖杆
      StringBuffer sb = new StringBuffer();
      Matcher matcher = doublePattern.matcher(description);
      while (matcher.find()) {
        String n = matcher.group();
        matcher.appendReplacement(sb, "|");
      }
      matcher.appendTail(sb);
      description = sb.toString();
    }
    return description;
  }

  public Map<String, ItemDetail> parse(List<Item> items) {
    // 将装备和卷轴存到一起
    Map<String, ItemDetail> nameMap = new HashMap<>();
    Map<String, ItemDetail> itemMap = new HashMap<>();
    items.forEach(e -> {

      String name = splitName(e.getName());
      ItemDetail itemDetail = new ItemDetail(name);
      nameMap.put(name, itemDetail);
      itemMap.put(e.getId(), itemDetail);

      itemDetail.setId(e.getId());

      /** 描述 */
      String description = parseDescription(e.getId(), e.getUbertip());
      itemDetail.setDescription(description);

      /** 物品等级 */
      String level = splitLevel(description);
      itemDetail.setLevel(level);
      itemDetail.setLevelInt(getValue(level) * 10);

      /** 物品类型 */
      String type = splitType(itemDetail.getDescription(), e.getOthers().get("class"));
      itemDetail.setType(type);

      itemDetail.setPickRandom(itemDetail.getPickRandom());
      String levelClass = combineLeveClass(e);
      itemDetail.setLevelClass(levelClass);

      /** 物品图标 */
      String icon = splitIcon(e.getArt());
      itemDetail.setIcon(icon);

      String[] splitHeroExclusive = splitHeroExclusive(itemDetail.getDescription());
      itemDetail.setHero(splitHeroExclusive[0]);
      itemDetail.setHeroExclusive(splitHeroExclusive[1]);

    });

    // 处理制作卷轴和炼化书
    nameMap.keySet().stream().filter(name -> name.contains("炼化书") || name.contains("制作卷轴")).forEach(name -> {
      String itemName = name.replace("炼化书", "")
          .replace("制作卷轴", "")
          .replace("魔石", "Lv1");
      ItemDetail book = nameMap.get(name);
      ItemDetail item = nameMap.get(itemName);
      if (item == null) {
        System.out.println("未找到炼化的物品：" + name);
      }
      // 这是一张合成卷轴
      // 获取锻造材料
      String formula = splitSynthesisFormula(book.getDescription());
      item.setSynthesisFormula(formula);
      item.setJuanzhouId(book.getId());

      // 删除制作书中的专属信息，后面会通过英雄名字找物品，会把制作书找出来
      book.setHero(null);
      book.setHeroExclusive(null);
    });

    this.nameMap = nameMap;
    this.itemMap = itemMap;
    return this.itemMap;
  }

  private static Pattern cnPattern = Pattern.compile("([\\u4e00-\\u9fa5\\-]+)");

  private String combineLeveClass(Item e) {
    // // 物品分类 人造
    // ITEM_TYPE_ARTIFACT ConvertItemType(3) Artifact
    // // 物品分类 战役
    // ITEM_TYPE_CAMPAIGN ConvertItemType(5) Campaign
    // // 物品分类 永久
    // ITEM_TYPE_PERMANENT ConvertItemType(0) Permanent
    // // 物品分类 可购买
    // ITEM_TYPE_PURCHASABLE ConvertItemType(4) Purchasable
    // call CreateItemLoc(ChooseRandomItemExBJ(6,ITEM_TYPE_PURCHASABLE),

    String level = e.getLevel();
    if (level == null) {
      level = e.getOldLevel();
      if (level == null) {
        return null;
      }
    }

    String clazz = null;
    Map<String, String> others = e.getOthers();
    if (others == null || (clazz = others.get("class")) == null) {
      return null;
    }

    String levelClass = level + ",ITEM_TYPE_" + clazz.toUpperCase();
    return levelClass;
  }

  private static Pattern heroExclusivePattern = Pattern.compile("(\\|cff\\w{6})([\\u4e00-\\u9fa5]+)专属：");

  private static String[] splitHeroExclusive(String description) {
    Matcher matcher = heroExclusivePattern.matcher(description);
    String splitKey = "|cff999999";

    String[] arr = new String[2];
    if (matcher.find()) {
      splitKey = matcher.group(1);
      arr[0] = matcher.group(2);
    }

    // |n|r|cff999999双剑女仆专属：|n增加战斗精通50%的背刺判定范围和伤害
    arr[1] = splitKey + split(splitKey, "|r", description);
    return arr;
  }

  public static void main(String[] args) throws Exception {
    System.out.println(splitType("|cffccffff闪避+25%|n|cff99cc00种类：魔石|r|n|n能够提升闪避能力的神奇魔石。", "Miscellaneous"));
  }

  private static String splitIcon(String art) {
    if (art == null) {
      return "";
    }
    return art;
  }

  private static String splitType(String description, String clazz) {

    String info = "未知";
    if (description.contains("种类：")) {
      info = split("种类：", "|n", description).replaceAll("(\\|n)|(\\|r)", "");
    } else if (description.contains("类型：")) {
      info = split("类型：", "|n", description).replaceAll("(\\|n)|(\\|r)", "");
    }

    switch (clazz) {
      case "Artifact":
        info = "武器";
        break;
      case "Permanent":
        info = "副武器";
        break;
      case "PowerUp":
        info = "装甲";
        break;
      case "Purchasable":
        info = "头部道具";
        break;
      case "Campaign":
        info = "道具(项链,手套,戒指,鞋子,灵魂)";
        break;
      case "Charged":
        info = "材料";
        break;
      case "Miscellaneous":
        info = "传送";
        break;
      default:
        info = "未知";
        break;
    }
    return info;
  }

  public Map<String, List<ItemDetail>> getDropUnit(String type) {

    // 物品进行分类
    Map<String, List<ItemDetail>> typeMap = new HashMap<>();
    itemMap.values().forEach(item -> {
      List<ItemDetail> typeList = MapUtil.getNotNull(typeMap, item.getType(), ArrayList::new);
      typeList.add(item);
    });

    // 挑选出某种类型的物品
    List<ItemDetail> itemList = MapUtil.getNotNull(typeMap, type, ArrayList::new);
    Map<String, ItemDetail> itemListMap = itemList.stream()
        .collect(Collectors.toMap(ItemDetail::getId, Function.identity()));

    Map<String, List<ItemDetail>> resultMap = new LinkedHashMap<>();
    switch (type) {
      case "武器":
      case "副武器":
      case "装甲":
      case "头部道具":
      case "道具(项链,手套,戒指,鞋子,灵魂)":
      case "材料":
      case "传送":
      case "未知":
        // 以支线举例
        Map<String, Map<String, Map<String, Integer>>> markType = getMarkType(Client.class);
        Map<String, Map<String, Integer>> zhixianMap = markType.get(type);
        if (zhixianMap != null) {
          for (Entry<String, Map<String, Integer>> entry : zhixianMap.entrySet()) {
            String 支线1 = entry.getKey();
            Map<String, Integer> map = entry.getValue();
            List<ItemDetail> subList = MapUtil.getNotNull(resultMap, 支线1, ArrayList::new);

            for (String itemId : map.keySet()) {
              ItemDetail tmp = this.itemMap.get(itemId);
              itemListMap.remove(itemId);
              subList.add(tmp);
            }
          }
          if (itemListMap.size() > 0) {
            List<ItemDetail> otherList = MapUtil.getNotNull(resultMap, "未归类", ArrayList::new);
            itemListMap.values().forEach(e -> {
              otherList.add(e);
            });
            sortList(otherList);
          }
        } else {
          sortList(itemList);
          resultMap.put(type, itemList);
        }
        break;
      default:
        typeMap.remove("武器");
        typeMap.remove("副武器");
        typeMap.remove("装甲");
        typeMap.remove("头部道具");
        typeMap.remove("道具(项链,手套,戒指,鞋子,灵魂)");
        typeMap.remove("材料");
        typeMap.remove("传送");
        typeMap.remove("未知");
        if (typeMap.size() > 0) {
          resultMap.putAll(typeMap);
        }

        break;
    }
    return resultMap;
  }

  private static void sortList(List<ItemDetail> itemList) {
    // 排序
    Collator collator = Collator.getInstance(new Locale("zh", "CN"));
    itemList.sort((o1, o2) -> {
      int v1 = o1.getLevelInt() - o2.getLevelInt();
      return v1 != 0 ? v1 : collator.compare(o1.getName(), o2.getName());
    });
  }

  private static String splitLevel(String description) {
    String info = split("品质:", "|n", description).replaceAll("(\\|n)|(\\|r)", "");
    info = info.replaceAll("\\|cff\\w{6}", "").replaceAll("\\|r", "");
    if (info.startsWith("|cff")) {
      info = info.substring(10);
    }
    return info;
  }

  private static String splitSynthesisFormula(String description) {
    // |cffccffcc锻造材料|n|r|cffffcc00工具箱|n拉索姆金属 x 10|n冰糖心 x 10|n|r|cff99ccff
    String str = split("需要材料|r", "|n|cffccffff", description);
    if (str.contains("需要材料")) {
      str = split("需要材料|r", "|r", description);
    }
    return "|r" + str;
  }

  public static String split(String startkey, String endKey, String description) {
    int i = description.indexOf(startkey);
    if (i > -1) {
      int j = description.indexOf(endKey, i + startkey.length());
      if (j > -1) {
        return description.substring(i + startkey.length(), j);
      } else {
        return description;
      }
    }
    return "";
  }

  public static String splitName(String desc) {
    // Name = "|cffff77ff凤凰羽靴|r"
    // Name = "|cffffff00冰龙头|r|cffccffcc合成卷轴|r"
    return desc.replaceAll("\\|[cffCFF]{3}\\w{4,6}", "").replaceAll("(\\|r)|(\\|n)", "").replace(".", "·");
  }

  @Data
  private static class CountDropInfo {
    private Map<String, AtomicInteger> cache = new LinkedHashMap<>();

    public void add(String desc) {
      AtomicInteger count = MapUtil.getNotNull(cache, desc, () -> new AtomicInteger(0));
      count.incrementAndGet();
    }

    public String toString() {
      StringBuilder buf = new StringBuilder();
      cache.forEach((k, v) -> {
        buf.append(k);
        if (v.get() > 1) {
          buf.append(" × ").append(v.get());
        }
        buf.append("|n");
      });
      // 去掉末尾的换行
      if (buf.length() > 2) {
        buf.setLength(buf.length() - 2);
      }
      return buf.toString();
    }
  }

  public void wrapDropString(Map<String, UnitDetail> idUnitMap, Map<String, DestructableDetail> destructableMap,
      UnitGroup unitGroup) {

    // ITEMID,SET<UNITDEAIL>
    Map<String, List<ItemDetail.UnitDrop>> shopSellItemMap = new HashMap<>();
    idUnitMap.forEach((unitId, unitDetail) -> {
      String sellitems = unitDetail.getSellitems();
      if (sellitems == null) {
        return;
      }
      String[] items = sellitems.split(",");
      for (String itemId : items) {
        List<ItemDetail.UnitDrop> itemDropUnits = MapUtil.getNotNull(shopSellItemMap, itemId, ArrayList::new);
        ItemDetail.UnitDrop unit = new ItemDetail.UnitDrop();
        unit.setDropTrigger(unitDetail);
        DropInfo dropInfo = new DropInfo(itemId);
        dropInfo.setDesc("出售");
        unit.setDropInfo(dropInfo);
        itemDropUnits.add(unit);
      }

    });
    System.out.println("归纳商店为Item完成");

    // 备注信息归纳
    Map<String, Map<String, String>> allMap = getMark(Client.class);
    Map<String, String> markMap = MapUtil.getNotNull(allMap, "备注", HashMap::new);
    Map<String, String> hechengMap = MapUtil.getNotNull(allMap, "合成", HashMap::new);
    Map<String, String> huodeMap = MapUtil.getNotNull(allMap, "获得", HashMap::new);

    DecimalFormat decimalFormat = new DecimalFormat("#%");
    itemMap.forEach((itemId, item) -> {
      // 英雄专属合成
      if (hechengMap.get(itemId) != null) {
        String formula = hechengMap.get(itemId);
        // |cff19caad铜戒指 |r× 1|n
        item.setSynthesisFormula(formula);
      }

      // 补充合成材料中的商店
      if (item.getJuanzhouId() != null && !item.getSynthesisFormula().contains("出售")) {
        List<UnitDrop> units = shopSellItemMap.get(item.getJuanzhouId());
        itemMap.get(item.getJuanzhouId());
        if (units != null) {
          for (UnitDrop unitDrop : units) {
            IDropTrigger dropTrigger = unitDrop.getDropTrigger();
            String synthesisFormula = item.getSynthesisFormula() + "|n" + dropTrigger.getName() + "出售卷轴";
            item.setSynthesisFormula(synthesisFormula);
          }
        } else {
          System.out.println("找不到合成卷轴！：" + item.getJuanzhouId());
        }
      }

      // 添加备注
      item.setMark(markMap.get(itemId));

      // 其他获取方式
      if (huodeMap.get(itemId) != null) {
        item.setDropPlace(huodeMap.get(itemId));
      }

      // 常规掉落统计
      List<ItemDetail.UnitDrop> droList = shopSellItemMap.get(itemId);
      if (droList == null) {
        return;
      }

      Collections.sort(droList, (o1, o2) -> {
        int v = 0;
        v = Integer.parseInt(o1.getDropTrigger().getHp().replaceAll("\\.\\d*", ""))
            - Integer.parseInt(o2.getDropTrigger().getHp().replaceAll("\\.\\d*", ""));
        if (v == 0) {
          return o1.getDropTrigger().getName().compareTo(o2.getDropTrigger().getName());
        }
        return v;
      });

      // 统计重复次数
      CountDropInfo countMap = new CountDropInfo();
      droList.forEach(e -> {
        IDropTrigger info = e.getDropTrigger();
        String unitName = info.getName();
        String dropDesc = e.getDropInfo().getDesc() == null ? "" : ("(" + e.getDropInfo().getDesc() + ")");
        String dropRate = e.getDropInfo().getRate() == null ? ""
            : (" " + decimalFormat.format(e.getDropInfo().getRate()));
        String desc = unitName + dropDesc + dropRate;
        countMap.add(desc);
      });

      // 总的获取方式= 其他获得形式+(商店出售+怪物掉落)
      item.setDropPlace((item.getDropPlace() == null ? "" : item.getDropPlace()) + "|n" + countMap.toString());
    });

    if (unitGroup == null) {
      return;
    }
    Map<String, List<ItemAccessories>> specialItemIdNotJuanzhouMap = new HashMap<>();
    Map<String, List<ItemDetail.UnitDrop>> monsterDropItemMap = new HashMap<>();

    System.out.println("归纳Funtion为Item完成");

    itemMap.forEach((itemId, item) -> {
      // 卷轴通用合成
      if (item.getUnitId() != null) {
        String desc = item.getSynthesisFormula();
        item.setSynthesisFormula(desc + "|n" + idUnitMap.get(item.getUnitId()).getName() + " 提供合成卷轴");
      }

      if (item.getSynthesisFormula() == null && specialItemIdNotJuanzhouMap.get(itemId) != null) {
        StringBuilder formula = new StringBuilder();

        boolean breakFlag = false;
        for (ItemAccessories itemsAccessories : specialItemIdNotJuanzhouMap.get(itemId)) {
          // itemMap不包含卷轴,故常青藤片手斧卷轴找不到
          if (itemMap.get(itemsAccessories.getItemId()) == null) {
            breakFlag = true;
            break;
          }
          formula.append("|cff19caad" + itemMap.get(itemsAccessories.getItemId()).getName() + " |r× "
              + itemsAccessories.getNum() + "|n");
        }

        if (breakFlag == false) {
          if (item.getHero() != null) {
            formula.append("|n" + item.getHero() + " |r无需卷轴合成");
          }
          // |cff19caad铜戒指 |r× 1|n
          item.setSynthesisFormula(formula.toString());
        } else {
          System.out.println("物品合成未成功!" + itemMap.get(itemId).getName());
        }
      }

      // --- 皮肤合成
      if (item.getJuanzhouId() != null && item.getUnitId() == null) {
        String desc = item.getSynthesisFormula();
        item.setSynthesisFormula(desc + "|n" + "剧情提供合成");
      }

      // 常规掉落统计
      List<ItemDetail.UnitDrop> droList = monsterDropItemMap.get(itemId);
      if (droList == null) {
        return;
      }

      Collections.sort(droList, (o1, o2) -> {
        int v = 0;
        v = Integer.parseInt(o1.getDropTrigger().getHp().replaceAll("\\.\\d*", ""))
            - Integer.parseInt(o2.getDropTrigger().getHp().replaceAll("\\.\\d*", ""));
        if (v == 0) {
          return o1.getDropTrigger().getName().compareTo(o2.getDropTrigger().getName());
        }
        return v;
      });

      // 统计重复次数
      CountDropInfo countMap = new CountDropInfo();
      droList.forEach(e -> {
        IDropTrigger info = e.getDropTrigger();
        String unitName = info.getName();
        String dropDesc = e.getDropInfo().getDesc() == null ? "" : ("(" + e.getDropInfo().getDesc() + ")");
        String dropRate = e.getDropInfo().getRate() == null ? ""
            : (" " + decimalFormat.format(e.getDropInfo().getRate()));
        String desc = unitName + dropDesc + dropRate;
        countMap.add(desc);

      });

      // 总的获取方式= (其他获得形式+固定商店出售)+(触发商店出售+怪物掉落)
      item.setDropPlace((item.getDropPlace() == null ? "" : item.getDropPlace()) + "|n" + countMap.toString());
    });

  }

}
