package com.mp;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.common.ini.Ability;
import com.common.ini.Destructable;
import com.common.ini.HeroGroup;
import com.common.ini.IniRead;
import com.common.ini.Item;
import com.common.ini.Unit;
import com.common.j.FunctionRead;
import com.common.parse.Function;
import com.mp.function.HeroData;
import com.mp.parse.AbilityDetail;
import com.mp.parse.AbilityParse;
import com.mp.parse.DestructableDetail;
import com.mp.parse.DestructableParse;
import com.mp.parse.ExcelImageInsert;
import com.mp.parse.FunctionDetail;
import com.mp.parse.FunctionParse;
import com.mp.parse.HeroParse;
import com.mp.parse.HeroSheet;
import com.mp.parse.ItemDetail;
import com.mp.parse.ItemParse;
import com.mp.parse.ItemSheet;
import com.mp.parse.LingeSheet;
import com.mp.parse.UnitDetail;
import com.mp.parse.UnitParse;
import com.mp.parse.UnitSheet;
import com.mp.sqlite.SqLiteJDBC;
import com.mp.wiki.CsvAbil;
import com.mp.wiki.CsvCailiao;
import com.mp.wiki.CsvHero;
import com.mp.wiki.CsvItem;
import com.mp.wiki.CsvLingge;
import com.mp.wiki.CsvTaozhuang;

public class ClientForWiki {
  public static void main(String[] args) throws Exception {

    String excelName = "梦想远景装备介绍_v1.5.1.xlsx";
    SqLiteJDBC.setVersion("v1.5.1", "v1.4.3");

    String w3xliniPath = "D:\\war5-jass\\jass_plugin\\w3x2lni_zhCN_v2.5.2\\w3x2lni_zhCN_v2.5.2\\";
    String assetPath = w3xliniPath + "0mp\\E283F124FB2723BD153989F8150EA39F\\";
    ExcelImageInsert.set(assetPath, Client.class);

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

    List<Ability> abilityList = IniRead.read("template/Custom/ability.ini", assetPath + "table\\ability.ini",
        Ability.class);
    System.out.println("读取ability.ini完成");
    Map<String, AbilityDetail> abilityMap = AbilityParse.parse(abilityList);
    System.out.println("解析技能完成");

    List<Function> funList = new FunctionRead().read(assetPath + "map\\war3map.j还原256.j");
    System.out.println("读取j文件完成");
    FunctionParse functionParse = new FunctionParse();
    Map<String, FunctionDetail> funDetailList = functionParse.parse(funList);
    System.out.println("解析Function完成");

    Map<String, Map<String, String>> lingeMap = IniRead.read2(Client.class.getResourceAsStream("custom/灵格.ini"));

    HeroGroup heroGroup = new HeroGroup(IniRead.read2(Client.class.getResourceAsStream("custom/hero.ini")));

    // 汉化物品名称
    functionParse.wrapItem(idItemMap);
    // 归纳整理单位的掉落信息
    unitParse.wrapDropString(idItemMap, funDetailList);
    // 归纳整理物品的获得途径
    itemParse.wrapDropString(idUnitMap, destructableMap, funDetailList);

    HeroParse heroParse = new HeroParse();
    heroParse.wrapHero(abilityMap, idUnitMap, idItemMap, heroGroup);
    Map<String, HeroData> map = heroParse.wrapHeroData(abilityMap, idUnitMap, idItemMap, funList);

    // 清空文件
    try (BufferedWriter wr = new BufferedWriter(
        new OutputStreamWriter(new FileOutputStream("梦想愿景-材料.csv", false), "GBK"))) {
      wr.write("levelVal,level,name,prop,prop2,getFrom,mark,type,hero,ID\n");
    }
    try (BufferedWriter wr = new BufferedWriter(
        new OutputStreamWriter(new FileOutputStream("梦想愿景-abils.txt", false), "UTF-8"))) {
      wr.write("");
    }
    try (BufferedWriter wr = new BufferedWriter(
        new OutputStreamWriter(new FileOutputStream("梦想愿景-heros.txt", false), "UTF-8"))) {
      wr.write("");
    }
    try (BufferedWriter wr = new BufferedWriter(
        new OutputStreamWriter(new FileOutputStream("梦想愿景-lingges.csv", false), "GBK"))) {
      wr.write("");
    }

    CsvLingge.insert("梦想愿景", "灵格", lingeMap, abilityMap);
    CsvAbil.insert("梦想愿景", "技能", heroParse.getRes());
    CsvHero.insert("梦想愿景", "角色", heroParse.getRes());
    // 生成excel
    XSSFWorkbook workbook = new XSSFWorkbook();
    UnitSheet unitSheet = new UnitSheet(workbook, idItemMap);
    unitSheet.insert("单位", unitParse.getDropUnitOrder());
    unitSheet.insert("单位(场景)", unitParse.getDropUnit());

    CsvItem.insert("梦想愿景", "武器", itemParse.getDropUnit("武器"), map, heroParse.getRes());
    CsvItem.insert("梦想愿景", "副手", itemParse.getDropUnit("副手"), map, heroParse.getRes());
    CsvItem.insert("梦想愿景", "铠甲", itemParse.getDropUnit("铠甲"), map, heroParse.getRes());
    CsvItem.insert("梦想愿景", "鞋子", itemParse.getDropUnit("鞋子"), map, heroParse.getRes());
    CsvItem.insert("梦想愿景", "饰品", itemParse.getDropUnit("饰品"), map, heroParse.getRes());
    CsvCailiao.insert("梦想愿景", "主线任务所需物品", itemParse.getDropUnit("主线任务所需物品"), map, heroParse.getRes());
    CsvCailiao.insert("梦想愿景", "支线任务所需物品", itemParse.getDropUnit("支线任务"), map, heroParse.getRes());
    Map<String, List<ItemDetail>> 材料Map = itemParse.getDropUnit("材料");
    updateType(材料Map, "材料");
    CsvCailiao.insert("梦想愿景", "材料", 材料Map, map, heroParse.getRes());
    Map<String, List<ItemDetail>> foodMap = itemParse.getDropUnit("食物");
    updateType(foodMap, "食物");
    CsvCailiao.insert("梦想愿景", "食物", foodMap, map, heroParse.getRes());
    Map<String, List<ItemDetail>> 纪念碑Map = itemParse.getDropUnit("纪念碑");
    updateType(纪念碑Map, "纪念碑");
    CsvCailiao.insert("梦想愿景", "纪念碑", 纪念碑Map, map, heroParse.getRes());

    Map<String, List<ItemDetail>> 特殊Map = itemParse.getDropUnit("特殊");
    updateType(特殊Map, "特殊");
    CsvCailiao.insert("梦想愿景", "特殊", 特殊Map, map, heroParse.getRes());

    Map<String, List<ItemDetail>> 套装Map = itemParse.getDropUnit("套装");
    updateType(套装Map, "套装");
    CsvTaozhuang.insert("梦想愿景", "套装", 套装Map, map, heroParse.getRes());

    unitSheet.insertHead("更新记录", heroParse.getRes());
    System.out.println("输出Excel完成");
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
}
