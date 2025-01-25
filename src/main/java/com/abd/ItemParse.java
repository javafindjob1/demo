package com.abd;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.text.Collator;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.abd.FunctionDetail.ItemAccessories;

import lombok.Data;

@Data
public class ItemParse extends AbstractParse {

  private Map<String, ItemDetail> itemMap;
  private Map<String, ItemDetail> nameMap;

  private static int getValue(String level) {
    int value = 0;
    switch (level) {
      case "普通":
        value = 10;
        break;
      case "普通·辉":
        value = 11;
        break;
      case "精良":
        value = 20;
        break;
      case "精良·辉":
        value = 21;
        break;
      case "优秀":
        value = 30;
        break;
      case "优秀·辉":
        value = 31;
        break;
      case "卓越":
        value = 40;
        break;
      case "卓越·辉":
        value = 41;
        break;
      case "罕见":
        value = 50;
        break;
      case "罕见·辉":
        value = 51;
        break;
      case "珍稀":
        value = 60;
        break;
      case "珍稀·辉":
        value = 61;
        break;
      case "传世":
        value = 70;
        break;
      case "传世·辉":
        value = 71;
        break;
      case "神代":
        value = 80;
        break;
      case "神代·辉":
        value = 81;
        break;
      case "神代·伊始":
        value = 82;
        break;
      case "特供":
        value = 1;
        break;
      default:
        value = 0;
        break;
    }
    return value;
  }

  public Map<String, ItemDetail> parse(List<Item> items) {
    // 将装备和卷轴存到一起
    Map<String, ItemDetail> nameMap = new HashMap<>();
    items.forEach(e -> {

      String[] arr = splitName(e.getName());
      if (arr.length < 1) {
        return;
      }

      String name = arr[0];
      ItemDetail itemDetail = nameMap.get(name);
      if (itemDetail == null) {
        itemDetail = new ItemDetail(name);
        nameMap.put(name, itemDetail);
      }

      if (arr.length == 2) {
        // 这是一张合成卷轴
        // 获取锻造材料
        String formula = splitSynthesisFormula(e.getDescription());
        itemDetail.setSynthesisFormula(formula);
        itemDetail.setJuanzhouId(e.getId());
        return;
      }

      itemDetail.setId(e.getId());

      /** 描述 */
      String Description = e.getUbertip();
      itemDetail.setDescription(Description);

      /** 物品等级 */
      String level = splitLevel(itemDetail.getDescription());
      itemDetail.setLevel(level);
      itemDetail.setLevelInt(getValue(level));

      /** 物品类型 */
      String type = splitType(itemDetail.getDescription());
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

    this.nameMap = nameMap;
    this.itemMap = nameMap.values().stream().collect(Collectors.toMap(ItemDetail::getId, Function.identity()));

    // 锻造信息显示材料等级和类型
    this.itemMap.forEach((itemId, itemDetail) -> {
      String synthesisFormula = itemDetail.getSynthesisFormula();
      if (synthesisFormula != null) {
        // |cffccffcc锻造材料|r|cffff9900|n|r|cffffcc00达摩克利斯之剑|n失乐魔羽|n雷龙石 x 20|n
        itemDetail.setSynthesisFormula(extendFormula(synthesisFormula, nameMap));
      }

    });

    // 3个药剂添加到饰品内
    this.itemMap.forEach((itemId, itemDetail) -> {
      switch (itemId) {
        case "I00A":// 应急治疗药水
          itemDetail.setLevelInt(9);
          itemDetail.setType("饰品");
          break;
        case "I07O":// 魔萃药剂
          itemDetail.setLevelInt(getValue("罕见·辉"));
          itemDetail.setType("饰品");
          break;
        case "rej6":// 圣光药剂
          itemDetail.setLevelInt(getValue("神代·伊始"));
          itemDetail.setType("饰品");
          break;
        case "frhg":// 潮涌剑
          itemDetail.setHero("暗礁之龙");
          break;
        default:
          break;
      }
    });

    return this.itemMap;
  }

  private static Pattern cnPattern = Pattern.compile("([\\u4e00-\\u9fa5\\-]+)");

  public static String extendFormula(String desc, Map<String, ItemDetail> nameMap) {
    Map<String, String> strs = new HashMap<>();
    Matcher matcher = cnPattern.matcher(desc);
    while (matcher.find()) {
      String str = matcher.group(1);

      strs.put(str, str);
      if (nameMap == null)
        continue;

      ItemDetail itemDetail = nameMap.get(str);
      if (itemDetail != null) {
        if ("铠甲饰品副手鞋子武器".contains(itemDetail.getType())) {
          strs.put(str, str + "(" + itemDetail.getLevel() + itemDetail.getType() + ")");
        }
      }
    }

    // 替换
    StringBuffer buf = new StringBuffer(desc);
    strs.forEach((old, n) -> {
      String nString = buf.toString().replace(old, n);
      buf.setLength(0);
      buf.append(nString);

    });

    return buf.toString();

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

  private static Pattern heroExclusivePattern = Pattern.compile("(\\|cff\\w{6})([\\u4e00-\\u9fa5]+)特殊效果：");

  private static String[] splitHeroExclusive(String description) {
    Matcher matcher = heroExclusivePattern.matcher(description);
    String splitKey = "|cff999999";

    String[] arr = new String[2];
    if (matcher.find()) {
      splitKey = matcher.group(1);
      arr[0] = matcher.group(2);
    }

    // |cff999999冰霜剑使专属：|n力量+75|n敏捷+150|r
    arr[1] = splitKey + split(splitKey, "|r", description);
    return arr;
  }

  public static void main(String[] args) throws Exception {
    // List<Item> list = new IniRead().read("template/Custom/item.ini", "item.ini",
    // Item.class);
    // list.stream().filter(e ->
    // e.getId().equals("ram1")).forEach(System.out::println);
    // list = list.stream().filter(e ->
    // e.getId().equals("ram1")).collect(Collectors.toList());
    // Map<String, ItemDetail> map = new ItemParse().parse(list);
    // System.out.println(map.get("ram1"));
    // System.out.println(map);

    Map<String, Map<String, Map<String, Integer>>> markType = getMarkType();
    System.out.println(markType);
    if (true)
      return;

    // // System.out.println(splitSynthesisFormula(
    // // "|cffccffcc锻造材料|r|cffff9900|n|r|cffffcc00猛虎戒指|n降龙戒指|n拉索姆金属 x
    // // 10|r|cff99ccff|n|n力量+160|n"));
    // // System.out.println(splitType(
    // //
    // "|cff99cc00等级：B+|r|n|cffffcc00类型：材料|n（可在百宝书内查看材料数量）|r|cffff9900|n|r|n古代拉索姆工业技术的结晶，后世的阿卡迪亚科技也能看到它的影子。"));
    // // System.out.println(splitHeroExclusive(
    // //
    // "|cff99ccff攻击力+45000|n力量+350|n敏捷+350|n法术吸血+4%|n暴击伤害+80%|n战栗：降低附近敌人20%攻击力和10点护甲|n|r|cff808080金色魔王专属：|n增加30%分裂攻击伤害和范围|r|cff99ccff|n|r|cff99cc00等级：S+|n|r|cffff9900类型：武器|r|n|n来自东方世界的魔剑，拥有焚毁天地间仙人神兽的邪霸之力。"));
    // System.out.println(splitHeroExclusive("|cff99ccff攻击力+45000|cff808080金色魔王专属：|nasdf|r"));
    // // System.out.println(splitName("|cffff77ff凤凰羽靴|r")[0]);
    // System.out.println(splitName("|cffffff00冰龙头|r|cffccffcc合成卷轴|r")[0]);
    // System.out.println(splitName("|cffffff00冰龙头|r|cffccffcc合成卷轴|r")[1]);
    {
      String[] nameArr = splitName("|cffff9900白剑-皓白月望|r");
      System.out.println(nameArr[0]);
    }
    {

      String[] nameArr = splitName("|cffff9900地精机械臂合成卷轴|r");
      System.out.println(nameArr[0]);
      System.out.println(nameArr[1]);
    }
    System.out.println(splitSynthesisFormula(
        "|cffffcc00合成所需材料：|r|n|cffcc99ff工程扳手|r × 1|n|cffff00ff雪熊钢骨|r × 2|n|cffff00ff冰魔铁斧碎片|r × 3|n|n|cffffcc00合成物品属性：|r|n|cffc0c0c0地精机械臂（副手）|n护甲 + 130|n力量 + 310|n防御提升·中（被动）：降低15%自身受到的物理伤害。|n重击（被动）：物理攻击时有10%机率令目标眩晕1秒。|n工程大师特殊效果：|n-暴雨火箭弹击中目标时回复自身0.7%最大生命值。|n-制作工程机器人时会额外制作一个多重火箭弹机器人。|r|n|n|cffd6d5b7由锻造师制作，魔法师附魔的卷轴，可以将材料凭空合成为卷轴内记录的道具。|r"));
    System.out.println(splitSynthesisFormula(
        "|cffffcc99支线任务·木材爱好者所需道具|r|n|n|cffffcc00合成所需材料：|r|n木柄斧 × 1|n小树枝 × 1|n|cff19caad常青藤木 |r× 1|n|n|cffffcc00合成物品属性：|r|n|cffc0c0c0常青藤片手斧（副手）|n攻击力 + 240|n全能力 + 10|n生命回复+ 10|n魔法回复 + 5|n藤斧劈击（被动）：物理攻击时附加全能力×1的法术伤害。|r|n|n|cffd6d5b7由锻造师制作，魔法师附魔的卷轴，可以将材料凭空合成为卷轴内记录的道具。|r"));

    System.out.println(
        splitType("|cffe6ceac类型：|r|cfffcd211支线任务·冰之种所需物品|r|n|n|cffd6d5b7冰之种结出的纯白花朵，宛若天使的羽毛。|r"));
    System.out.println(
        splitLevel(
            "|cffbee7e9移动速度 + 50|n全能力 + 5|r|n|cffe6ceac装备品级：|r普通|n|cffe6ceac装备类型：|r|cfffcd211鞋子|r|cffe6ceac|n|r|n|cffd6d5b7皮质的长靴，旅人出行的标准装备。|r"));
    System.out.println(
        splitType(
            "|cffbee7e9幸运值 + 1|n生命值 + 800|n全能力 + 50|n法术抗性 + 10%|r|n|cffecad9e林音（主动）：治疗900码范围内友方单位1500点生命值，冷却时间9秒。|r|n|cffe6ceac装备品级：|r|cffa0eee1优秀·辉|n|r|cffe6ceac装备类型：|r|cfffcd211饰品·唯一|r|cffe6ceac|n|r|n|cffd6d5b7特殊霸主森林古主掉落的灵木，它网罗了森林大部分的本质。|r"));
    System.out.println(
        splitLevel(
            "|cffbee7e9幸运值 + 1|n生命值 + 800|n全能力 + 50|n法术抗性 + 10%|r|n|cffecad9e林音（主动）：治疗900码范围内友方单位1500点生命值，冷却时间9秒。|r|n|cffe6ceac装备品级：|r|cffa0eee1优秀·辉|n|r|cffe6ceac装备类型：|r|cfffcd211饰品·唯一|r|cffe6ceac|n|r|n|cffd6d5b7特殊霸主森林古主掉落的灵木，它网罗了森林大部分的本质。|r"));
    System.out.println(
        splitHeroExclusive(
            "|cffbee7e9移动速度 + 190|n智力 + 420|n生命回复 + 500|n法术抗性 + 15%|r|n|cffbeedc7光属性强化·技（被动）：提高光属性英雄20%技能伤害。|r|n|cffd1ba74天罚之烁特殊效果：|n·罚光解放的持续时间延长4秒。|n·晖降击中敌方单位时，会附加23%无视法术抗性的纯粹法术伤害。|r|n|cffe6ceac装备品级：|r|cffff9900神代|r|n|cffe6ceac装备类型：|r|cfffcd211鞋子|n|r|n|cffd6d5b7光之主赐福的圣洁之靴。正因为它任何色彩及情感都不会吸收，所以才呈现出纯白的姿态。|r"));
    System.out.println(
        splitSynthesisFormula(
            "|cffffcc00合成所需材料：|r|n|cffcc99ff工程扳手|r × 1|n|cffff00ff雪熊钢骨|r × 2|n|cffff00ff冰魔铁斧碎片|r × 3|n|n|cffffcc00合成物品属性：|r|n|cffc0c0c0地精机械臂（副手）|n护甲 + 130|n力量 + 310|n防御提升·中（被动）：降低15%自身受到的物理伤害。|n重击（被动）：物理攻击时有10%机率令目标眩晕1秒。|n工程大师特殊效果：|n-暴雨火箭弹击中目标时回复自身0.7%最大生命值。|n-制作工程机器人时会额外制作一个多重火箭弹机器人。|r|n|n|cffd6d5b7由锻造师制作，魔法师附魔的卷轴，可以将材料凭空合成为卷轴内记录的道具。|r"));
    System.out.println(
        splitSynthesisFormula(
            "|cffffcc00合成所需材料：|r|n|cffff99cc应急治疗药水|r × 1|n|cffff0000小恶魔的魔法书|r × 1|n|n|cffffcc00合成物品属性：|r|n|cffc0c0c0魔萃药剂（药水）|n全能力 + 110|n法术抗性 + 15%|n魔能治疗（主动）：回复（100+1%）×自身等级的生命值，（20+1%）×自身等级的魔法值，同时提高100%攻击速度，持续10秒，冷却20秒。|r|n|n|cffd6d5b7由锻造师制作，魔法师附魔的卷轴，可以将材料凭空合成为卷轴内记录的道具。|r"));

    System.out.println("11\\n".indexOf("\\", 2));
  }

  private static String splitIcon(String art) {
    if (art == null) {
      return "";
    }
    return art;
  }

  private static String splitType(String description) {
    String info = split("类型：|r", "|r", description).replace("|n", "");
    if (info.startsWith("|cff")) {
      info = info.substring(10);
    }
    if (info.length() == 0) {
      info = "物品";
    }
    if (info.indexOf("·") > -1) {
      info = info.substring(0, info.indexOf("·"));
    }
    return info;
  }

  private static String splitLevel(String description) {
    String info = split("装备品级：|r", "|cffe6ceac", description).replaceAll("(\\|n)|(\\|r)", "");
    if (info.startsWith("|cff")) {
      info = info.substring(10);
    }
    return info;
  }

  private static String splitSynthesisFormula(String description) {
    // |cffccffcc锻造材料|n|r|cffffcc00工具箱|n拉索姆金属 x 10|n冰糖心 x 10|n|r|cff99ccff
    return "|cffffcc00" + split("合成所需材料：", "合成物品属性", description);
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

  public static String[] splitName(String desc) {
    // Name = "|cffff77ff凤凰羽靴|r"
    // Name = "|cffffff00冰龙头|r|cffccffcc合成卷轴|r"
    String[] arr = desc.split("(合成卷)|(配方)");
    for (int i = 0; i < arr.length; i++) {
      arr[i] = arr[i].replaceAll("\\|cff\\w{6}", "").replaceAll("\\|r", "");
    }

    return arr;
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

  public void wrapDropString(Map<String, FunctionDetail> funDetailList, Map<String, UnitDetail> idUnitMap,
      Map<String, DestructableDetail> destructableMap) {

    // ITEMID,SET<UNITDEAIL>
    Map<String, List<ItemDetail.UnitDrop>> itemIdAndDropUnitMap = new HashMap<>();
    Map<String, List<ItemAccessories>> specialItemIdNotJuanzhouMap = new HashMap<>();

    funDetailList.values().forEach(e -> {
      // 普通/精英/霸主/材料 掉落
      // Map<unitID, List<Item>>
      e.getItemMap().forEach((unitId, v) -> {
        v.forEach(item -> {
          List<ItemDetail.UnitDrop> list2 = MapUtil.getNotNull(itemIdAndDropUnitMap, item.getItemId(), ArrayList::new);
          IDropTrigger u = idUnitMap.get(unitId);
          if (u == null) {
            u = destructableMap.get(unitId);
          }
          assertNotNull("unit不应该为空", u);

          ItemDetail.UnitDrop unit = new ItemDetail.UnitDrop();
          unit.setDropTrigger(u);
          unit.setDropInfo(item);
          list2.add(unit);
        });

      });

      // 商店出售
      e.getJuanzhouMap().forEach((unitId, juanzhouList) -> {
        juanzhouList.forEach(item -> {
          List<ItemDetail.UnitDrop> list2 = MapUtil.getNotNull(itemIdAndDropUnitMap, item.getItemId(), ArrayList::new);

          ItemDetail.UnitDrop unit = new ItemDetail.UnitDrop();
          unit.setDropTrigger(idUnitMap.get(unitId));
          unit.setDropInfo(item);
          item.setDesc("出售");
          list2.add(unit);
        });
      });

      // 专属合成非合成卷轴类的物品
      e.getNewItemFormulaMap().forEach((itemId, subItems) -> {
        // List<ItemAccessories>
        specialItemIdNotJuanzhouMap.put(itemId, subItems);
      });

    });

    // 铁铲挖掘
    funDetailList.values().stream().filter(e -> e.getWaJueList().size() > 0).map(e -> e.getWaJueList())
        .forEach(dropList -> {
          // Map<unitID, List<Item>>
          dropList.forEach((item) -> {
            List<ItemDetail.UnitDrop> list2 = MapUtil.getNotNull(itemIdAndDropUnitMap, item.getItemId(),
                ArrayList::new);

            ItemDetail.UnitDrop unit = new ItemDetail.UnitDrop();
            unit.setDropTrigger(new IDropTrigger() {
              @Override
              public String getId() {
                return "";
              }

              @Override
              public String getName() {
                return "铁铲挖地";
              }

              @Override
              public String getHp() {
                return "0";
              }

            });
            unit.setDropInfo(item);
            list2.add(unit);
          });

        });

    // 套装
    funDetailList.values().stream().filter(e -> e.getTaozhuangList().size() > 0).map(e -> e.getTaozhuangList())
        .forEach(list -> {
          list.forEach(taozhuang -> {
            ItemDetail itemDetail = new ItemDetail(taozhuang.getTaozhuangName());
            itemDetail.setId(taozhuang.getItemList().get(0) + "_");

            String desc = "|cffe6ceac所需装备组件：|n" + taozhuang.getColor();
            int levelInt = 0;
            for (String i : taozhuang.getItemList()) {
              desc += itemMap.get(i).getName() + "|n";
              if (itemMap.get(i).getLevelInt() > levelInt) {
                levelInt = itemMap.get(i).getLevelInt();
              }
            }
            desc = extendFormula(desc, this.nameMap) + taozhuang.getTaozhuangDesc();
            itemDetail.setDescription(desc);
            itemDetail.setType("情报");
            itemDetail.setLevelInt(levelInt);
            itemMap.put(itemDetail.getId(), itemDetail);
          });
        });

    System.out.println("归纳Funtion为Item完成");

    idUnitMap.forEach((unitId, unitDetail) -> {
      String sellitems = unitDetail.getSellitems();
      if (sellitems == null) {
        return;
      }
      String[] items = sellitems.split(",");
      for (String itemId : items) {
        List<ItemDetail.UnitDrop> list2 = MapUtil.getNotNull(itemIdAndDropUnitMap, itemId, ArrayList::new);
        ItemDetail.UnitDrop unit = new ItemDetail.UnitDrop();
        unit.setDropTrigger(unitDetail);
        FunctionDetail.DropInfo dropInfo = new FunctionDetail.DropInfo(itemId);
        dropInfo.setDesc("出售");
        unit.setDropInfo(dropInfo);
        list2.add(unit);
      }

    });
    System.out.println("归纳商店为Item完成");

    // 备注信息归纳
    Map<String, Map<String, String>> allMap = getMark();
    Map<String, String> markMap = MapUtil.getNotNull(allMap, "备注", HashMap::new);
    Map<String, String> hechengMap = MapUtil.getNotNull(allMap, "合成", HashMap::new);
    Map<String, String> huodeMap = MapUtil.getNotNull(allMap, "获得", HashMap::new);

    DecimalFormat decimalFormat = new DecimalFormat("#%");
    assertNotNull("I09Q不能为空", itemMap.get("I09Q"));
    itemMap.forEach((itemId, item) -> {
      // 卷轴通用合成
      if (item.getUnitId() != null) {
        String desc = item.getSynthesisFormula();
        item.setSynthesisFormula(desc + "|n" + idUnitMap.get(item.getUnitId()).getName() + " 提供合成卷轴");
      }

      // 英雄专属合成
      if (hechengMap.get(itemId) != null) {
        StringBuilder formula = new StringBuilder();
        List<String> list = Arrays.asList(hechengMap.get(itemId).split(","));
        String unitId = list.get(0);
        for (int i = 1; i < list.size(); i++) {
          formula.append("|cff19caad" + itemMap.get(list.get(i)).getName() + " |r× 1|n");
        }
        // |cff19caad铜戒指 |r× 1|n
        String synthesisFormula = formula.toString() + "|n" + idUnitMap.get(unitId).getPropernames() + " 无需卷轴合成";
        item.setSynthesisFormula(extendFormula(synthesisFormula, nameMap));

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

      // 其他获取方式
      if (huodeMap.get(itemId) != null) {
        item.setDropPlace(huodeMap.get(itemId));
      }

      // 添加备注
      item.setMark(markMap.get(itemId));

      // --- 皮肤合成
      if (item.getJuanzhouId() != null && item.getUnitId() == null) {
        String desc = item.getSynthesisFormula();
        item.setSynthesisFormula(desc + "|n" + "剧情提供合成");
      }

      // 常规掉落统计
      List<ItemDetail.UnitDrop> droList = itemIdAndDropUnitMap.get(itemId);
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

  }

  public Map<String, List<ItemDetail>> getDropUnit(String type) {
    Map<String, List<ItemDetail>> typeMap = new HashMap<>();
    itemMap.values().forEach(item -> {
      List<ItemDetail> list2 = MapUtil.getNotNull(typeMap, item.getType(), ArrayList::new);
      list2.add(item);
    });

    List<ItemDetail> itemList = MapUtil.getNotNull(typeMap, type, ArrayList::new);
    Map<String, ItemDetail> itemListMap = itemList.stream()
        .collect(Collectors.toMap(ItemDetail::getId, Function.identity()));
    Collator collator = Collator.getInstance(new Locale("zh", "CN"));

    Map<String, List<ItemDetail>> resultMap = new LinkedHashMap<>();
    switch (type) {
      case "武器":
      case "副手":
      case "铠甲":
      case "鞋子":
      case "饰品":
        itemList.sort((o1, o2) -> {
          int v1 = o1.getLevelInt() - o2.getLevelInt();
          return v1 != 0 ? v1 : collator.compare(o1.getName(), o2.getName());
        }); 
        resultMap.put(type, itemList);
        break;
      case "主线任务所需物品":
      case "支线任务":
      case "材料":
      case "物品":
      case "食物":
      case "消耗品":
      case "情报":
        // 以支线举例
        Map<String, Map<String, Map<String, Integer>>> markType = getMarkType();
        Map<String, Map<String, Integer>> zhixianMap = markType.get(type);
        if (zhixianMap != null) {
          for (Entry<String, Map<String, Integer>> entry : zhixianMap.entrySet()) {
            String 支线1 = entry.getKey();
            Map<String, Integer> map = entry.getValue();
            List<ItemDetail> subList = MapUtil.getNotNull(resultMap, 支线1, ArrayList::new);

            for (String itemId : map.keySet()) {
              ItemDetail tmp = itemListMap.remove(itemId);
              if (tmp == null) {
                tmp = this.itemMap.get(itemId);
              }
              subList.add(tmp);
            }
          }
          if (itemListMap.size() > 0) {
            List<ItemDetail> otherList = MapUtil.getNotNull(resultMap, "未归类", ArrayList::new);
            itemListMap.values().forEach(e -> {
              otherList.add(e);
            });
          }
        } else {
          resultMap.put(type, itemList);
        }

        break;
      default:
        typeMap.remove("武器");
        typeMap.remove("副手");
        typeMap.remove("铠甲");
        typeMap.remove("鞋子");
        typeMap.remove("饰品");
        typeMap.remove("主线任务所需物品");
        typeMap.remove("支线任务");
        typeMap.remove("材料");
        typeMap.remove("物品");
        typeMap.remove("食物");
        typeMap.remove("消耗品");
        typeMap.remove("情报");
        if (typeMap.size() > 0) {
          resultMap.putAll(typeMap);
        }
        break;

    }
    return resultMap;
  }

}
