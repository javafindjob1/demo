package com.b4;

import static org.junit.Assert.assertNotNull;

import java.text.Collator;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ItemParse extends AbstractParse {

  private Map<String, ItemDetail> itemMap;
  private Map<String, ItemDetail> nameMap;

  public Map<String, ItemDetail> parse(List<Item> items) {

    Map<String, Map<String, String>> markMap = getMark();
    Map<String, String> descMap = markMap.get("说明");
    // 将装备和卷轴存到一起
    Map<String, ItemDetail> nameMap = new HashMap<>();
    items.forEach(e -> {

      String name = parseName(e.getName());
      ItemDetail itemDetail = MapUtil.getNotNull(nameMap, name, () -> new ItemDetail(name));
      itemDetail.setId(e.getId());
      itemDetail.setIcon(e.getArt());
      /** 解析物品文本 */
      parseDescription(itemDetail, descMap.getOrDefault(e.getId(), e.getUbertip()));

      /** 解析物品类型 */
      parseType(itemDetail, e.getOthers().get("class"));

    });

    this.nameMap = nameMap;
    this.itemMap = nameMap.values().stream().collect(Collectors.toMap(ItemDetail::getId, Function.identity()));
    return this.itemMap;
  }

  public static void parseDescription(ItemDetail itemDetail, String desc) {

    // 格式化颜色代码
    desc = formatColorCode(desc);

    itemDetail.setDescription(desc);

    /** 物品等级 */
    parseLevel(itemDetail, desc);
    /** 迷宫掉落 */
    praseDropList(itemDetail, desc);
  }

  public static void praseDropList(ItemDetail itemDetail, String desc) {
    if (desc.contains("迷宫") && desc.contains("掉落物品")) {
      String dropStr = desc.substring(desc.indexOf("掉落物品") + 4);
      if (dropStr.indexOf("|n") > 0) {
        dropStr = dropStr.substring(0, dropStr.indexOf("|n"));
      }

      System.out.println("->" + dropStr + "<-");
      String[] items = dropStr.split("\\s+");
      for (String item : items) {

        System.out.println("->" + item + "<-");
        item = trimName(item);
        if (item.startsWith(":") || item.startsWith("：")) {
          item = item.substring(1);
        }
        if (item.length() > 0) {
          itemDetail.getDropList().add(item);
        }
      }
    }
  }

  private static String formatColorCode(String desc) {
    Pattern p = Pattern.compile("\\|[cfCF]{3}\\w{6}");
    Matcher matcher = p.matcher(desc);
    while (matcher.find()) {
      String group = matcher.group();
      desc = desc.replace(group, group.toLowerCase());
    }
    return desc;
  }

  public static void main(String[] args) throws Exception {
    List<Item> list = new IniRead().read("template/Custom/item.ini",
        UnitParse.class.getResource("custom/item.ini").getFile(),
        Item.class);
    // list.stream().filter(e ->
    // e.getId().equals("ram1")).forEach(System.out::println);
    // list = list.stream().filter(e ->
    // e.getId().equals("ram1")).collect(Collectors.toList());
    Map<String, ItemDetail> map = new ItemParse().parse(list);
    System.out.println(map.get("ram1"));
    System.out.println(ItemDetail.levelMap);
    System.out.println(ItemDetail.nengliMap);
    System.out.println(ItemDetail.typeMap);
    // System.out.println(ItemDetail.typeMap);
    // Map<String, Map<String, Map<String, Integer>>> markType = getMarkType();
    // System.out.println(markType);
  }

  public static void parseType(ItemDetail itemDetail, String classType) {
    String type = ClassType.valueOf(classType).getDesc();
    itemDetail.setType(type);
  }

  private static String parseLevel(ItemDetail itemDetail, String description) {
    String info = split("品质", "|n", description);
    if (info.length() > 0) {
      info = trimName(info);
      info = info.substring(1);
    }
    if (info.length() == 0) {
      // System.out.println("物品的品质未找到desc:" + itemDetail.getId() + " = \"" +
      // description + "\"");
    }
    itemDetail.setLevel(info);
    ItemDetail.levelMap.put(info, ItemDetail.levelMap.getOrDefault(info, 0) + 1);

    // 力量 筋力
    // 敏捷
    // 智力 体力
    // 全属性 三维
    // 攻击力 攻击 +2w -2w
    // 魔法防御 防御 魔抗 50% 52
    // 生命 +2w -2w
    // 闪避
    itemDetail.setStr(findNum("(力量)|(筋力)", description));
    itemDetail.setItn(findNum("(智力)|(体力)", description));
    itemDetail.setAgi(findNum("(敏捷)", description));

    itemDetail.setStr(itemDetail.getStr() + findNum("(全属)|(全属性)|(三维)", description));
    itemDetail.setItn(itemDetail.getItn() + findNum("(全属)|(全属性)|(三维)", description));
    itemDetail.setAgi(itemDetail.getAgi() + findNum("(全属)|(全属性)|(三维)", description));

    itemDetail.setAttack(findNum("(攻击力)|(攻击)", description));
    itemDetail.setDef(findNum("(魔法防御)|(防御)|(魔抗)", description));
    itemDetail.setHp(findNum("(生命)|(生命值)|(血量)", description));
    itemDetail.setMiss(findNum("(闪避)", description));

    itemDetail.setNengli(findAbility("(能力)", description));

    itemDetail.setLevelInt(itemDetail.getAttack() + itemDetail.getDef() * 400 + itemDetail.getHp()
        + itemDetail.getStr() * 100 + itemDetail.getAgi() * 100 + itemDetail.getItn() * 100
        + itemDetail.getNengli().length());

    return info;
  }

  private static int findNum(String regexKey, String description) {
    Pattern p = Pattern.compile("(?:" + regexKey + ")\\s*[:：]?\\s*[\\+-]?\\s*(\\d+[wW万]?)");
    Matcher matcher = p.matcher(description);
    if (matcher.find()) {
      // 计算|的数量推断后面的数字是实际位置
      int count = 0;
      int start = 0;
      while ((start = regexKey.indexOf("|", start) + 1) > 0) {
        count++;
      }
      String group = matcher.group(count + 2);
      group = group.replaceAll("w|W|万", "0000");
      return Integer.parseInt(group);
    }
    return 0;
  }

  private static String findAbility(String regexKey, String description) {

    Pattern p = Pattern.compile("(?:" + regexKey + ")\\s*[:：\\s].+?[（\\(\\[]([ABCDESRabcdesr]+)");

    StringBuilder buf = new StringBuilder();

    // 计算|的数量推断后面的数字是实际位置
    int count = 0;
    int start = 0;
    while ((start = regexKey.indexOf("|", start) + 1) > 0) {
      count++;
    }
    for (String desc : description.split("\\|n")) {
      Matcher matcher = p.matcher(desc);
      while (matcher.find()) {
        String group = matcher.group(count + 2).toUpperCase();
        buf.append(group).append(",");
        ItemDetail.nengliMap.put(group, ItemDetail.nengliMap.getOrDefault(group, 0) + 1);
      }
    }

    if (buf.length() > 0) {
      buf.setLength(buf.length() - 1);
    } else {
      Pattern p2 = Pattern.compile("(?:" + regexKey + ")\\s*[:：\\s]");
      Matcher matcher2 = p2.matcher(description);
      if (matcher2.find()) {
        // System.out.println("能力解析失败:" + description);
      }
    }
    return buf.toString();
  }

  public static String split(String startkey, String endKey, String description) {
    int i = description.indexOf(startkey);
    if (i > -1) {
      int j = description.indexOf(endKey, i + startkey.length());
      if (j > -1) {
        return description.substring(i + startkey.length(), j);
      } else {
        Matcher matcher = cnPattern.matcher(description.substring(i + startkey.length()));
        if (matcher.find()) {
          return matcher.group();
        } else {
          return description;
        }
      }
    }
    return "";
  }

  public static String parseName(String name) {
    name = formatColorCode(name);
    name = name.replaceAll("\\|cff\\w{6}", "").replaceAll("\\|r", "");
    return name;
  }

  public void wrapDropString() {

    Map<String, Map<String, String>> allMap = getMark();
    Map<String, String> markMap = MapUtil.getNotNull(allMap, "备注", HashMap::new);
    Map<String, String> hechengMap = MapUtil.getNotNull(allMap, "合成", HashMap::new);
    Map<String, String> huodeMap = MapUtil.getNotNull(allMap, "获得", HashMap::new);

    itemMap.forEach((itemId, item) -> {
      // 其他获取方式
      if (huodeMap.get(itemId) != null) {
        item.setDropPlace(huodeMap.get(itemId));
      }
      // 添加备注
      item.setMark(markMap.get(itemId));
    });

    // 迷宫掉落统计
    {
      Map<String, List<String>> dropMap = new HashMap<>();
      itemMap.forEach((itemId, item) -> {
        if (ClassType.Miscellaneous.getDesc().equals(item.getType())) {
          List<String> itemList = item.getDropList();
          if (itemList.size() > 0) {
            for (String itemstr : itemList) {
              MapUtil.getNotNull(dropMap, itemstr, ArrayList::new).add(item.getName());
            }
          }
        }
      });

      nameMap.forEach((name, item) -> {
        List<String> itemList = dropMap.get(name);
        if (itemList != null) {
          item.setDropPlace(String.join("|n", itemList));
        }
      });
    }

  }

  public Map<String, List<ItemDetail>> getDropUnit(String type) {
    Map<String, List<ItemDetail>> typeMap = new HashMap<>();
    itemMap.values().forEach(item -> {
      List<ItemDetail> list2 = MapUtil.getNotNull(typeMap, item.getType(), ArrayList::new);
      if (item.getLevelInt() > 0)
        list2.add(item);
    });

    List<ItemDetail> itemList = MapUtil.getNotNull(typeMap, type, ArrayList::new);
    Collator collator = Collator.getInstance(new Locale("zh", "CN"));

    Map<String, List<ItemDetail>> resultMap = new LinkedHashMap<>();
    switch (type) {
      case "武器":
      case "副武器":
      case "头部道具":
      case "装甲":
      case "道具(项链,手套,戒指,鞋子,灵魂)":
      case "不归类":
        itemList.sort((o1, o2) -> {
          int v1 = o1.getLevelInt() - o2.getLevelInt();
          return v1 != 0 ? v1 : collator.compare(o1.getName(), o2.getName());
        });
        resultMap.put(type, itemList);
        break;
      default:
        typeMap.remove("武器");
        typeMap.remove("副武器");
        typeMap.remove("头部道具");
        typeMap.remove("装甲");
        typeMap.remove("道具(项链,手套,戒指,鞋子,灵魂)");
        typeMap.remove("不归类");
        if (typeMap.size() > 0) {
          resultMap.putAll(typeMap);
        }
        break;

    }
    return resultMap;
  }

  private static enum ClassType {
    Artifact("武器"),
    Charged("副武器"),
    Purchasable("头部道具"),
    PowerUp("装甲"),
    Campaign("道具(项链,手套,戒指,鞋子,灵魂)"),
    Permanent("不归类"),
    Miscellaneous("迷宫");

    private String desc;

    private ClassType(String desc) {
      this.desc = desc;
    }

    public String getDesc() {
      return this.desc;
    }

  }

}
