package com.abc;

import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import com.abc.FunctionDetail.Item;
import com.abc.fun.HeroIntroParse;
import com.abc.fun.NanduManager;
import com.abc.fun.RandomRate;
import com.abc.fun.RandomRateManager;

import lombok.Data;

@Data
public class FunctionParse {

  private static final ScriptEngineManager manager = new ScriptEngineManager();
  protected static final ScriptEngine engine = manager.getEngineByName("JavaScript");
  private static final Pattern arrPattern = Pattern.compile("^set \\w+\\[[0-9]+\\]='(\\w{4})'$");
  protected static final Pattern randomIntPattern = Pattern.compile("^.*,\\$(\\w+),GetRandomInt\\(1,(\\d+([+-](\\d+\\*)?(\\w+))?)\\)\\)$");
  protected static final Pattern ratePattern = Pattern.compile("LoadInteger\\(\\w+,GetHandleId\\(GetTriggeringTrigger\\(\\)\\)\\*\\w+,\\$(\\w+)\\)([=><]+)(\\d+)([\\+-](\\d+\\*)?(\\w+))?([\\+-](\\d+\\*)?LoadInteger\\(\\w+,GetHandleId\\(GetTriggeringTrigger\\(\\)\\)\\*\\w+,\\$\\w+\\))?");
  // if GetUnitTypeId(GetTriggerUnit())=='h00A' then
  private static final Pattern unitNamePattern = Pattern
      .compile("GetUnitTypeId\\(GetTriggerUnit\\(\\)\\)=='(\\w{4})'");
  // call CreateItemLoc('manh',LoadLocation
  private static final Pattern itemNamePattern = Pattern.compile("^call CreateItemLoc\\('(\\w{4})',");
  private static final Pattern itemRandomPattern = Pattern
      .compile("^call CreateItemLoc\\(ChooseRandomItemExBJ\\((\\d+,ITEM_TYPE_\\w{4,})\\),");

  private Map<String, FunctionDetail> functionDetailMap;

  public void wrapItem(Map<String, ItemDetail> idItemMap) {

    // 构建物品分类Map
    Map<String, List<ItemDetail>> levelClassItemMap = new HashMap<>();
    idItemMap.forEach((k, v) -> {
      String pick = v.getPickRandom();
      String classType = v.getLevelClass();
      if ("1".equals(pick) && classType != null) {
        List<ItemDetail> list2 = MapUtil.getNotNull(levelClassItemMap, classType, ArrayList::new);
        list2.add(v);
      }

    });

    functionDetailMap.values().forEach(fun -> {

      // 随机物品处理 添加到掉落Map中
      Optional.ofNullable(fun.getItemLevelClassMap()).ifPresent(items -> {
        items.forEach((uid, levelClassStrs) -> {
          levelClassStrs.forEach(str -> {
            List<ItemDetail> list = levelClassItemMap.get(str.getId());

            if (fun.getItemMap() == null) {
              fun.setItemMap(new HashMap<>());
            }
            List<Item> notNull = MapUtil.getNotNull(fun.getItemMap(), uid, ArrayList::new);
            notNull.addAll(list.stream().map(itemDetail -> {
              Item u = new FunctionDetail.Item(itemDetail.getId());
              u.setRate(str.getRate()*1.0 / list.size());
              return u;
            }).collect(Collectors.toList()));
          });
        });
      });

      // 物品名称汉化
      fun.getItemMap().values().forEach(e -> {
        e.forEach(e2 -> {
          ItemDetail itemDetail = idItemMap.get(e2.getId());
          if (itemDetail == null) {
            System.out.println("物品信息为null" + e2.getId());
          }
          e2.setName(itemDetail.getName());
        });
      });

    });
  }

  public Map<String, FunctionDetail> parse(List<Function> functions) throws Exception{
    List<FunctionDetail> functionDetails = new ArrayList<>();

    for (Function function : functions) {
      FunctionDetail functionDetail = new FunctionDetail();
      functionDetail.setRows(function.getRows());
      functionDetails.add(functionDetail);

      for (String row : function.getRows()) {
        if (row.startsWith("function")) {
          functionDetail.setName(row.split("\\s+")[1]);
        }
        if (row.contains("GetRandomInt")) {
          functionDetail.setGetRandomInt(true);
        }
        if (row.contains("AddItemToStockBJ")) {
          functionDetail.setAddItemToStockBJ(true);
        }
        if (row.contains("RemoveItemFromStockBJ")) {
          functionDetail.setRemoveItemFromStockBJ(true);
        }

        if (row.startsWith("if GetUnitTypeId")) {
          functionDetail.setGetUnitTypeId(true);
        }
        if (row.startsWith("call CreateItemLoc('") || row.startsWith("call CreateItemLoc(Choose")) {
          functionDetail.setCreateItemLoc(true);
        }

      }

      if (functionDetail.isAddItemToStockBJ() && functionDetail.isRemoveItemFromStockBJ()
          && functionDetail.isGetRandomInt()) {
        // private boolean itemArr;
        // private List<String> items;
        // private List<String> specialItems;
        // private double specialRate;
        int i = 0;
        for (; i < function.getRows().size(); i++) {
          String row = function.getRows().get(i);
          if (row.startsWith("call SaveInteger")) {
            Matcher matcher = randomIntPattern.matcher(row);
            if (functionDetail.getSpecialRate() == null && matcher.find()) {
              String rate = matcher.group(2);
              String vp = matcher.group(5);
              if(vp!=null){
                rate = rate.replace(vp, "5");
              }
              Integer ff = (Integer)engine.eval(rate);
              functionDetail.setSpecialRate(1.0 / ff);
            }
          }
          if (row.startsWith("set")) {
            Matcher matcher = arrPattern.matcher(row);
            if (matcher.find()) {
              functionDetail.setItemArr(true);
              String itemid = matcher.group(1);
              if (functionDetail.getItems() == null) {
                functionDetail.setItems(new ArrayList<>());
              }
              functionDetail.getItems().add(new FunctionDetail.Item(itemid));
            }

          }

          if (row.startsWith("if LoadInteger")) {
            break;
          }

        }

        int j = i + 1;
        while (true) {
          String itemrow = function.getRows().get(j);
          if (itemrow.startsWith("set")) {
            String itemid = parseItemName(itemrow);
            functionDetail.getSpecialItems().add(new FunctionDetail.Item(itemid));
          }

          j++;
          if (itemrow.startsWith("else") || j >= function.getRows().size()) {
            break;
          }
        }

        List<Item> items = functionDetail.getItems();
        List<Item> specialItems = functionDetail.getSpecialItems();
        if (items != null) {
          int total = items.size();
          if (!items.contains(specialItems.get(0))) {
            total += specialItems.size();
          }

          Set<String> nameset = new HashSet<>();
          for (Item item : items) {
            if (!specialItems.contains(item)) {
              nameset.add(item.getId());
            }
          }

          List<Item> itemset = new ArrayList<>();
          for (String id : nameset) {
            Item item = new FunctionDetail.Item(id);
            itemset.add(item);
            final AtomicInteger num = new AtomicInteger(0);
            items.stream().filter(e -> e.getId().equals(id)).forEach(e -> num.incrementAndGet());
            double rate = (total - num.get()) * 1.0 / total;
            rate = 1 - Math.pow(rate, 8);
            item.setRate(rate);
          }

          if (functionDetail.getSpecialItems() != null) {
            double rate = (total - 1) * 1.0 / total;
            rate = 1 - Math.pow(rate, 8);

            for (Item item : specialItems) {
              Double specialRate = functionDetail.getSpecialRate();
              item.setRate(rate * specialRate);
            }
          }
          itemset.addAll(specialItems);
          functionDetail.setItems(itemset);
        }

      }
      if (functionDetail.isGetUnitTypeId() && functionDetail.isCreateItemLoc()) {
        System.out.println("function=" + functionDetail.getName());
        for (int i = 0; i < function.getRows().size(); i++) {
          String row = function.getRows().get(i);
          if (row.startsWith("if GetUnitTypeId")) {
            List<String> units = parseUnitname(row);
            if (units == null) {
              continue;
            }
            int j = 1;
            RandomRateManager randomRateManager = new RandomRateManager();
            NanduManager nanduManager = new NanduManager();
            while (i + 1 < function.getRows().size()) {
              if (j < 1) {
                break;
              }
              i++;
              row = function.getRows().get(i);

              if (row.startsWith("if")) {
                j++;
                Matcher matcher = ratePattern.matcher(row);
                List<Object[]> list = new ArrayList<>();
                while(matcher.find()){
                  String name = matcher.group(1);
                  String fuhao = matcher.group(2);
                  String base = matcher.group(3);
                  String vpString = matcher.group(4);
                  String vp = matcher.group(6);
                  if(vpString!=null){
                    base += vpString.replace(vp, "5");
                  }
                  Integer k = (Integer)engine.eval(base);
                  list.add(new Object[]{name,fuhao,k});
                }

                if(list.size()==1){
                  try{
                    randomRateManager.updateIfLoad(j, (String)list.get(0)[0], (String)list.get(0)[1], (Integer)list.get(0)[2], null, 0);
                  }catch(Exception e){
                    System.out.println("updateIfLoad 异常function=" + functionDetail.getName());
                    System.out.println("updateIfLoad 异常row=" + row);
                    System.out.println("updateIfLoad 异常name=" + list.get(0)[0]+",fuhao=" + list.get(0)[1] + ",k=" + list.get(0)[2] + ",unitname=" + units);
                    throw e;
                  }
                }else if(list.size()==2){
                  try{
                    randomRateManager.updateIfLoad(j, (String)list.get(0)[0], (String)list.get(0)[1], (Integer)list.get(0)[2], (String)list.get(1)[1], (Integer)list.get(1)[2]);
                  }catch(Exception e){
                    System.out.println("updateIfLoad 异常function=" + functionDetail.getName());
                    System.out.println("updateIfLoad 异常row=" + row);
                    System.out.println("updateIfLoad 异常name=" + list.get(0)[0]+",fuhao=" + list.get(0)[1] + ",k=" + list.get(0)[2] + ",unitname=" + units);
                    throw e;
                  }
                }else if(list.size()>2){
                  assertFalse("不支持这么多and", true);
                }else{
                  boolean flag = nanduManager.updateIfLoad(j, row);
                  if(flag == false){
                    if(units.contains("okod") // 巨型蛤蟆
                    || units.contains("nchg") // 蜡像兄弟西蒙
                    || units.contains("nmcf") // 冰精灵
                    || units.contains("h00A") // 妖精公主
                    || units.contains("n00M") // 独角兽阿尔海洛
                    ){
                      // 排除这些
                    }else{
                      assertFalse("杂乱的IF:" +units, true);
                    }
                  }
                  
                }
                continue;
              }else if(row.startsWith("else")){
                randomRateManager.updateElse(j);
                nanduManager.updateElse(j);
              } else if (row.startsWith("endif")) {
                j--;
                randomRateManager.updateEndif(j);
                nanduManager.updateEndif(j);
                continue;
              }else if(row.startsWith("call SaveInteger")){
                Matcher matcher = randomIntPattern.matcher(row);
                if(matcher.find()){
                  String name = matcher.group(1);
                  String ranStr = matcher.group(2);
                  String vp = matcher.group(5);
                  if(vp !=null){
                    ranStr = ranStr.replace(vp, "5");
                  }
                  int max = (Integer)engine.eval(ranStr);
                  randomRateManager.create(name, max, j);
                  System.out.println("create=" +name + ",max=" +max + ",j=" +j);
                }
              }

              if (!row.startsWith("call CreateItemLoc('") && !row.startsWith("call CreateItemLoc(Choose")) {
                continue;
              }

              if (row.startsWith("call CreateItemLoc('")) {
                String itemname = parseItemName2(row);
                if (itemname == null) {
                  continue;
                }
                Item item = new FunctionDetail.Item(itemname);
                item.setRate(randomRateManager.getRate());
                item.setDesc(nanduManager.getDesc());
                Map<String, List<Item>> itemMap = functionDetail.getItemMap();
                for (String name : units) {
                  List<Item> itemList = MapUtil.getNotNull(itemMap, name, ArrayList::new);
                  itemList.add(item);
                }

              } else {
                // 随机物品统计
                String levelClass = parseRandomItemName(row);
                if (levelClass == null) {
                  continue;
                }

                Item item = new FunctionDetail.Item(levelClass);
                item.setRate(randomRateManager.getRate());
                item.setDesc(nanduManager.getDesc());
                Map<String, List<Item>> itemLevelClassMap = functionDetail.getItemLevelClassMap();
                for (String name : units) {
                  List<Item> itemList = MapUtil.getNotNull(itemLevelClassMap, name, ArrayList::new);
                  itemList.add(item);
                }

              }

            }

          }
        }
      }
    }

    this.functionDetailMap = functionDetails.stream().filter(e -> e.getItems().size() > 0 || e.getItemMap().size() > 0)
        .collect(Collectors.toMap(FunctionDetail::getName, java.util.function.Function.identity()));
    return functionDetailMap;

  }

  public static List<String> parseUnitname(String row) {
    List<String> units = new ArrayList<>();
    if (row.startsWith("if GetUnitTypeId")) {
      Matcher matcher = unitNamePattern.matcher(row);
      while (matcher.find()) {
        String unitname = matcher.group(1);
        units.add(unitname);
      }
      if (units.size() > 0) {
        return units;
      }
    }
    return null;
  }

  public static String parseItemName2(String row) {
    if (row.startsWith("call CreateItemLoc('")) {
      Matcher matcher = itemNamePattern.matcher(row);
      if (matcher.find()) {
        String itemname = matcher.group(1);
        return itemname;
      }
    }
    return null;

  }

  public static String parseRandomItemName(String row) {
    if (row.startsWith("call CreateItemLoc(Choose")) {
      Matcher matcher = itemRandomPattern.matcher(row);
      if (matcher.find()) {
        String itemname = matcher.group(1);
        return itemname;
      }
    }
    return null;

  }

  public static String parseItemName(String row) {
    if (row.startsWith("set")) {
      Matcher matcher = arrPattern.matcher(row);
      if (matcher.find()) {
        String itemname = matcher.group(1);
        return itemname;
      }
    }
    return null;
  }

  public static void main(String[] args) throws Exception {
    List<Function> list = new FunctionRead().read(URLDecoder.decode(FunctionParse.class.getResource("custom/war3map.j还原256.j").getPath(), "utf8"));
    // Map<String, List<String>> map = new HeroIntroParse().parse(list);
    // List<FunctionDetail> list2 = new FunctionParse().parse(list);
    // System.out.println(list2.size());
    // // list2.stream().filter(e ->
    // // "BOs".equals(e.getName())).forEach(System.out::println);
    // // list2.stream().filter(e ->
    // // "CGU".equals(e.getName())).forEach(System.out::println);
    // list2.stream().filter(e ->
    // "CGp".equals(e.getName())).forEach(System.out::println);

    // list2.stream().filter(e -> e.getSpecialRate() !=
    // null).forEach(System.out::println);

    // System.out.println(
    //     parseUnitname("if GetUnitTypeId(GetTriggerUnit())=='E02P' GetUnitTypeId(GetTriggerUnit())=='E02G'  then"));
    // System.out.println(parseItemName2(
    //     "call CreateItemLoc('I04G',LoadLocationHandle(TF,GetHandleId(GetTriggeringTrigger())*W4,$2EE5541))"));

    // System.out.println(parseRandomItemName(
    //     "call CreateItemLoc(ChooseRandomItemExBJ(6,ITEM_TYPE_CAMPAIGN),LoadLocationHandle(TF,GetHandleId(GetTriggeringTrigger())*W4,$2EE5541))"));

  }
}
