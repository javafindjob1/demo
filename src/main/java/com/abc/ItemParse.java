package com.abc;

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

  public Map<String, ItemDetail> parse(List<Item> items) {
    // 将装备和卷轴存到一起
    Map<String, ItemDetail> nameMap = new HashMap<>();
    items.forEach(e -> {

      String[] arr = splitName(e.getName());
      if (arr.length < 1) {
        return;
      }

      String name = arr[0];
      if ("？？？？".equals(name)) {
        name = "不朽之王";
      }
      if (arr.length == 2) {
        final String nameFinal = name;
        ItemDetail itemDetail = MapUtil.getNotNull(nameMap, name, ()-> new ItemDetail(nameFinal));
        // 这是一张合成卷轴
        // 遍历这件物品获取锻造材料，就结束了
        String formula = splitSynthesisFormula(e.getDescription());
        itemDetail.setSynthesisFormula(formula);
        itemDetail.setJuanzhouId(e.getId());
        return;
      }

      ItemDetail itemDetail = new ItemDetail(name);
      if(!nameMap.containsKey(name)){
        nameMap.put(name, itemDetail);
      }else{
        if(nameMap.get(name).getJuanzhouId()!=null){
          itemDetail = nameMap.get(name);
        }
      }

      this.itemMap.put(e.getId(), itemDetail);
      itemDetail.setId(e.getId());

      /** 物品等级 */
      String level = splitLevel(e.getDescription());
      if ("蒸汽相机".equals(name)) {
        level = "?";
      }
      if ("阿瓦隆之靴".equals(name)) {
        nameMap.put(name + level, nameMap.remove(name));
      }
      itemDetail.setLevel(level);
      itemDetail.setLevelInt(getValue(level));

      /** 物品类型 */
      String type = splitType(e.getDescription());
      itemDetail.setType(type);

      itemDetail.setPickRandom(e.getPickRandom());
      String levelClass = combineLeveClass(e);
      itemDetail.setLevelClass(levelClass);

      /** 物品图标 */
      String icon = splitIcon(e.getArt());
      itemDetail.setIcon(icon);
      /** 描述 */
      String Description = e.getDescription();
      itemDetail.setDescription(Description);
      String[] heroExclusive = splitHeroExclusive(e.getDescription());
      itemDetail.setHeroExclusive(heroExclusive[0]);
      itemDetail.setHero(heroExclusive[1]);
    });
    // 锻造信息显示材料等级和类型
    this.itemMap.forEach((itemId, itemDetail) -> {
      String synthesisFormula = itemDetail.getSynthesisFormula();
      if (synthesisFormula != null) {
        // |cffccffcc锻造材料|r|cffff9900|n|r|cffffcc00达摩克利斯之剑|n失乐魔羽|n雷龙石 x 20|n
        itemDetail.setSynthesisFormula(extendFormula(synthesisFormula, nameMap));
      }

    });

    return this.itemMap;
  }

  private static Pattern cnPattern = Pattern.compile("([\\u4e00-\\u9fa5]+)");

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
        if(itemDetail.getType()==null){
          System.out.println("type==null"+itemDetail.getId());
        }
        if ("饰品鞋子衣服武器".contains(itemDetail.getType())) {
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

  private static Pattern heroExclusivePattern = Pattern.compile("(\\|cff\\w{6})[\\u4e00-\\u9fa5]+专属");

  private static String[] splitHeroExclusive(String description) {
    Matcher matcher = heroExclusivePattern.matcher(description);
    String splitKey = "|cff999999";
    if (matcher.find()) {
      splitKey = matcher.group(1);
    }
    // |cff999999冰霜剑使专属：|n力量+75|n敏捷+150|r

    String desc = split(splitKey, "|r", description);
    int i = desc.indexOf("专属");
    String hero = i<=0? "":desc.substring(0, i);
    return new String[]{splitKey + desc, hero};
  }

  public static void main(String[] args) throws Exception {
    List<Item> list = new IniRead().read("template/Custom/item.ini", UnitParse.class.getResource("custom/item.ini").getFile(), Item.class);
    
    System.out.println("--------------------");
    list.stream().filter(e -> e.getId().equals("I02G")).forEach(System.out::println);

    Map<String, ItemDetail> map = new ItemParse().parse(list);
    System.out.println(map.get("I02G"));
    System.out.println(map.get("|cffffcc99拉索姆人工大脑|r"));
    // System.out.println(map);

    // System.out.println(splitSynthesisFormula(
    // "|cffccffcc锻造材料|r|cffff9900|n|r|cffffcc00猛虎戒指|n降龙戒指|n拉索姆金属 x
    // 10|r|cff99ccff|n|n力量+160|n"));
    // System.out.println(splitType(
    // "|cff99cc00等级：B+|r|n|cffffcc00类型：材料|n（可在百宝书内查看材料数量）|r|cffff9900|n|r|n古代拉索姆工业技术的结晶，后世的阿卡迪亚科技也能看到它的影子。"));
    // System.out.println(splitHeroExclusive(
    // "|cff99ccff攻击力+45000|n力量+350|n敏捷+350|n法术吸血+4%|n暴击伤害+80%|n战栗：降低附近敌人20%攻击力和10点护甲|n|r|cff808080金色魔王专属：|n增加30%分裂攻击伤害和范围|r|cff99ccff|n|r|cff99cc00等级：S+|n|r|cffff9900类型：武器|r|n|n来自东方世界的魔剑，拥有焚毁天地间仙人神兽的邪霸之力。"));
    System.out.println(splitHeroExclusive("|cff99ccff攻击力+45000|cff808080金色魔王专属：|nasdf|r"));
    // System.out.println(splitName("|cffff77ff凤凰羽靴|r")[0]);
    // System.out.println(splitName("|cffffff00冰龙头|r|cffccffcc合成卷轴|r")[0]);
    // System.out.println(splitName("|cffffff00冰龙头|r|cffccffcc合成卷轴|r")[1]);

    System.out.println(
        extendFormula("|cffccffcc锻造材料|r|cffff9900|n|r|cffffcc00猛虎戒指|n降龙戒指|n拉索姆金属 x 10|r|cff99ccff|n|n力量+160|n", null));
  }

  private static String splitIcon(String art) {
    if (art == null) {
      return "";
    }
    return art;
  }

  private static String splitType(String description) {
    return split("类型：", "|", description);
  }

  private static String splitLevel(String description) {
    return split("等级：", "|", description);
  }

  private static String splitSynthesisFormula(String description) {
    // |cffccffcc锻造材料|n|r|cffffcc00工具箱|n拉索姆金属 x 10|n冰糖心 x 10|n|r|cff99ccff
    return "|cffffcc00" + split("|r|cffffcc00", "|r", description);
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
    String[] arr = desc.split("\\|r");
    for (int i = 0; i < arr.length; i++) {
      arr[i] = arr[i].replaceAll("\\|cff\\w{6}", "").replaceAll("\\|", "");
    }

    return arr;
  }

  public void wrapDropString(Map<String, FunctionDetail> funDetailList, Map<String, UnitDetail> idUnitMap) {

    Map<String, ItemDetail.ShopDrop> idShopItemMap = new HashMap<>();
    // ITEMID,SET<UNITDEAIL>
    Map<String, Set<ItemDetail.UnitDrop>> itemIdAndDropUnitMap = new HashMap<>();

    funDetailList.values().forEach(e -> {
      if (e.getItems().size() > 0) {
        idShopItemMap.putAll(e.getItems().stream()
            .collect(Collectors.toMap(FunctionDetail.Item::getId, e2 -> {
              ShopDrop shopDrop = new ItemDetail.ShopDrop();
              shopDrop.setItem(e2);
              shopDrop.setDropRate(e2.getRate());
              return shopDrop;
            })));
      }

      if (e.getItemMap().size() > 0) {
        // Map<unitID, List<Item>>
        e.getItemMap().forEach((unitId, v) -> {
          v.forEach(item -> {
            Set<ItemDetail.UnitDrop> list2 = MapUtil.getNotNull(itemIdAndDropUnitMap, item.getId(), HashSet::new);
            UnitDetail u = idUnitMap.get(unitId);

            ItemDetail.UnitDrop unit = new ItemDetail.UnitDrop();
            unit.setUnitDetail(u);
            unit.setDropRate(item.getRate());
            unit.setDesc(item.getDesc());
            list2.add(unit);
          });

        });
      }

    });
    System.out.println("转换Funtion为Item完成");

    Map<String, Map<String, String>> mark = readMark();
    Map<String, String> markMap = mark.get("备注");
    DecimalFormat decimalFormat = new DecimalFormat("#%");
    decimalFormat.setRoundingMode(RoundingMode.HALF_UP);//HALF_UP四舍五入
    DecimalFormat decimalFormat2 = new DecimalFormat("#‰");
    decimalFormat2.setRoundingMode(RoundingMode.HALF_UP);//HALF_UP四舍五入

    itemMap.forEach((k, v) -> {
      ItemDetail.ShopDrop item = idShopItemMap.get(k);

      if (item != null) {
        // 创建 DecimalFormat 实例
        // 格式化数值
        String formattedValue = decimalFormat.format(item.getDropRate());
        v.setShop("√ " + formattedValue);
      } else {
        v.setShop("");
      }

      Set<ItemDetail.UnitDrop> droSet = itemIdAndDropUnitMap.get(k);
      if (droSet != null) {
        StringBuffer buf = new StringBuffer();

        List<ItemDetail.UnitDrop> droList = new ArrayList<>();
        droList.addAll(droSet);
        Collections.sort(droList, (o1, o2) -> {
          return Integer.parseInt(o1.getUnitDetail().getHp()) - Integer.parseInt(o2.getUnitDetail().getHp());
        });
        droList.forEach(e -> {
          if (e.getUnitDetail().getName() != null) {

            buf.append(e.getUnitDetail().getName());
            buf.append(e.getDesc()==null?"":e.getDesc());

            if (e.getDropRate() != null) {
              String formattedValue = decimalFormat.format(e.getDropRate());
              if(formattedValue.startsWith("0")){
                formattedValue = decimalFormat2.format(e.getDropRate());
              }
              if(!formattedValue.startsWith("0")){
                buf.append(" " + formattedValue);
              }
            }
            buf.append("|n");
          }
        });
        v.setDropPlace(buf.toString());
      } 

        // 人工补录
        // "I04U" "玛丽坠饰
        // "I022" "党魂军令
        // "I04H" "菩提金印.改
        // "I042" "卡夫卡神像
        // "I04R" "祖海天梦珠
        // "I04M" "蒸汽相机
        // "I04F" "沙暴之星
        // "I060" "海姆达尔
        // "I060" "海姆达尔
        // "I05V" 黄金树假面
        // "I05X" "奴隶铁链+
        // "I04N" "折叠野餐桌
        // jpnt 旅人头巾+
        // "I02N 反革命装甲
        // I04Q 阿瓦隆之石
        // I036 阿瓦隆之靴
        // I04O 阿瓦隆之靴
        // I04P 阿瓦隆之靴
        if(k==null){
          System.out.println(v);
          return;
        }
      switch (k) {
        case "I02N":
          v.setDropPlace("祖海神像占卜");
          break;
        case "jpnt":
          v.setDropPlace("0区1区开箱子");
          break;
        case "I04U":
          v.setDropPlace("拍完玛丽后,找蒸汽绅士送");
          break;
        case "I022":
          v.setDropPlace("梦境白云山边上的箱子");
          break;
        case "I04H":
          v.setDropPlace("实验室电梯内的小怪 2‰");
          break;
        case "I042":
          v.setDropPlace("雷神殿奖励");
          break;
        case "I04R":
          v.setDropPlace("全任务后到祖海石像领取奖励");
          break;
        case "I04M":
          v.setDropPlace("支线获得");
          break;
        case "I04F":
          v.setDropPlace("黑曜城时回玛丽房间与司机对话送");
          break;
        case "I060":
          v.setDropPlace("击败女剑士后送");
          break;
        case "I04N":
          v.setDropPlace("薇薇安的野餐桌");
          break;
        case "I05X":
          v.setDropPlace("奴隶铁链*3自动合成（仅限难5以上）");
          break;
        case "I05V":
          v.setDropPlace("沙威的黑名单支线最终奖励");
          break;
        case "I04Q":
          v.setDropPlace("南瓜地野餐,祖海野餐");
          break;
        case "I036":
          v.setDropPlace("火山野餐");
          break;
          //I05M	千年帝王	僵尸皇帝 50%
          // I05K	太吾冥袍	僵尸皇帝 50%
          // I05J	婆罗迦楼印	僵尸皇帝 50%
          // I05L  太吾冥靴	僵尸皇帝 50%
          case "I05M":
          case "I05K":
          case "I05J":
          case "I05L":
          v.setDropPlace("僵尸皇帝 50%");
          break;
        default:
          break;
      }

      switch(k){
        case "ssan":
          v.setType("鞋子");
          break;
        default:
          break;
      }

      String value = markMap.get(k);
      v.setMark(value);

      switch (k) {
        case "I04O":
          v.setSynthesisFormula("阿瓦隆之石+阿瓦隆之靴B");
          break;
        case "I04P":
          v.setSynthesisFormula("阿瓦隆之石+阿瓦隆之靴A+");
          break;
        default:
          break;
      }

    });
  }

  public Map<String, Map<String, String>> readMark() {
    // Map<备注,Map>
    Map<String, Map<String, String>> markMap = new LinkedHashMap<>();
    try {
      InputStream file = UnitParse.class.getResourceAsStream("custom/mark.ini");
      Map<String, Map<String, String>> units = IniRead.read2(file);

      return units;
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("读取mark.ini文件异常" + e.getMessage());
    }
    return markMap;
  }

  public static Map<String, Map<String, Map<String, Integer>>> getMarkType() {
    // Map<备注,Map>
    Map<String, Map<String, Map<String, Integer>>> markMap = new LinkedHashMap<>();
    try {
      InputStream file = UnitParse.class.getResourceAsStream("custom/markType.ini");
      Map<String, Map<String, String>> units = IniRead.read2(file);

      for (Entry<String, Map<String, String>> entry : units.entrySet()) {
        String type = entry.getKey();
        String[] arr = type.split("-");
        String bigType = arr[0];
        String smallType = arr[1];

        Map<String, Map<String, Integer>> notNull = MapUtil.getNotNull(markMap, bigType, LinkedHashMap::new);
        Map<String, Integer> list2 = new LinkedHashMap<>();
        List<String> list = new ArrayList<>();
        list.addAll(entry.getValue().keySet());
        for (int i = 0; i < list.size(); i++) {
          list2.put(list.get(i), i);
        }
        notNull.put(smallType, list2);
      }
      return markMap;
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("读取markType.ini文件异常" + e.getMessage());
    }
    return markMap;

  }

  
  public Map<String, List<ItemDetail>> getDropUnit(String type) {
    Map<String, List<ItemDetail>> typeItemMap = new HashMap<>();
    itemMap.values().forEach(item -> {
      List<ItemDetail> typeItemList = MapUtil.getNotNull(typeItemMap, item.getType(), ArrayList::new);
      typeItemList.add(item);
    });

    List<ItemDetail> pickItemList = MapUtil.getNotNull(typeItemMap, type, ArrayList::new);
    Map<String, ItemDetail> pickItemMap = null;
    try{
      if(type.equals("材料")){
        pickItemList.stream().filter(item->item.getName().contains("拉索姆人工大脑")).forEach(e->{
          System.out.println(e.getId() +"->" + e.getName());
        });
      }
      pickItemMap = pickItemList.stream()
        .collect(Collectors.toMap(ItemDetail::getId, Function.identity()));
    }catch(Exception e){
      System.out.println(e);
    }
    Collator collator = Collator.getInstance(new Locale("zh", "CN"));

    Map<String, List<ItemDetail>> resultMap = new LinkedHashMap<>();
    switch (type) {
      case "武器":
      case "衣服":
      case "饰品":
      case "鞋子":
        pickItemList.sort((o1, o2) -> {
          int v1 = o1.getLevelInt() - o2.getLevelInt();
          return v1 != 0 ? v1 : collator.compare(o1.getName(), o2.getName());
        }); 
        resultMap.put(type, pickItemList);
        break;
      case "特殊":
      case "灵药":
      case "材料":
      case "饰品、特殊":
      case "靴子":
        // 以支线举例
        Map<String, Map<String, Map<String, Integer>>> markType = getMarkType();
        Map<String, Map<String, Integer>> zhixianMap = markType.get(type);
        if (zhixianMap != null) {
          for (Entry<String, Map<String, Integer>> entry : zhixianMap.entrySet()) {
            String 支线1 = entry.getKey();
            Map<String, Integer> map = entry.getValue();
            List<ItemDetail> subList = MapUtil.getNotNull(resultMap, 支线1, ArrayList::new);

            for (String itemId : map.keySet()) {
              ItemDetail tmp = pickItemMap.remove(itemId);
              if (tmp == null) {
                tmp = this.itemMap.get(itemId);
              }
              subList.add(tmp);
            }
          }
          if (pickItemMap.size() > 0) {
            List<ItemDetail> otherList = MapUtil.getNotNull(resultMap, "未归类", ArrayList::new);
            pickItemMap.values().forEach(e -> {
              otherList.add(e);
            });
          }
        } else {
          resultMap.put(type, pickItemList);
        }

        break;
      default:
        typeItemMap.remove("武器");
        typeItemMap.remove("衣服");
        typeItemMap.remove("饰品");
        typeItemMap.remove("鞋子");
        typeItemMap.remove("特殊");
        typeItemMap.remove("灵药");
        typeItemMap.remove("材料");
        typeItemMap.remove("饰品、特殊");
        typeItemMap.remove("靴子");
        if (typeItemMap.size() > 0) {
          resultMap.putAll(typeItemMap);
        }
        break;

    }
    return resultMap;
  }
}
