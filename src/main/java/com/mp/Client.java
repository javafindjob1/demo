package com.mp;

import com.common.ini.Ability;
import com.common.ini.Destructable;
import com.common.ini.IniRead;
import com.common.ini.Item;
import com.common.ini.Unit;
import com.common.parse.Function;
import com.mp.parse.AbilityDetail;
import com.mp.parse.AbilityParse;
import com.mp.parse.DestructableDetail;
import com.mp.parse.DestructableParse;
import com.mp.parse.ExcelImageInsert;
import com.mp.parse.FunctionDetail;
import com.mp.parse.FunctionParse;
import com.mp.parse.FunctionRead;
import com.mp.parse.HeroParse;
import com.mp.parse.HeroSheet;
import com.mp.parse.ItemDetail;
import com.mp.parse.ItemParse;
import com.mp.parse.ItemSheet;
import com.mp.parse.UnitDetail;
import com.mp.parse.UnitParse;
import com.mp.parse.UnitSheet;
import com.mp.sqlite.SqLiteJDBC;
import java.util.List;
import java.util.Map;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Client {
  public static void main(String[] args) throws Exception {

    String excelName = "梦想远景装备介绍_v1.2.13.xlsx";
    SqLiteJDBC.setVersion("v1.2.13", "v1.2.4");

    String w3xliniPath = "D:\\war5-jass\\jass_plugin\\w3x2lni_zhCN_v2.5.2\\w3x2lni_zhCN_v2.5.2\\";
    String assetPath = w3xliniPath + "0mp\\FB6D1E69E144A72B06A159542DBF51A3\\";
    ExcelImageInsert.set(assetPath, Client.class);

    List<Item> list = new IniRead().read("template/Custom/item.ini", assetPath + "table\\item.ini", Item.class);
    System.out.println("读item.ini完成");
    ItemParse itemParse = new ItemParse();
    Map<String, ItemDetail> idItemMap = itemParse.parse(list);
    System.out.println("解析物品完成");

    List<Unit> unitList = new IniRead().read("template/Custom/unit.ini", assetPath + "table\\unit.ini", Unit.class);
    System.out.println("读unit.ini完成");
    UnitParse unitParse = new UnitParse();
    Map<String, UnitDetail> idUnitMap = unitParse.parse(unitList);
    System.out.println("解析单位信息完成");

    List<Destructable> destructableList = new IniRead().read("template/Custom/destructable.ini",
        assetPath + "table\\destructable.ini", Destructable.class);
    System.out.println("读destructable.ini完成");
    DestructableParse destructableParse = new DestructableParse();
    Map<String, DestructableDetail> destructableMap = destructableParse.parse(destructableList);
    System.out.println("解析箱子完成");

    List<Ability> abilityList = new IniRead().read("template/Custom/ability.ini", assetPath + "table\\ability.ini",
        Ability.class);
    System.out.println("读取ability.ini完成");
    Map<String, AbilityDetail> abilityMap = AbilityParse.parse(abilityList);
    System.out.println("解析技能完成");

    unitParse.wrapDropString(idItemMap,null);
    itemParse.wrapDropString(idUnitMap, destructableMap, null);
    HeroParse heroParse = new HeroParse();
    heroParse.wrapHero(abilityMap, idUnitMap, idItemMap, null);

    // 生成excel
    XSSFWorkbook workbook = new XSSFWorkbook();
    UnitSheet unitSheet = new UnitSheet(workbook, idItemMap);
    unitSheet.insert("单位", unitParse.getDropUnitOrder());
    unitSheet.insert("单位(场景)", unitParse.getDropUnit());

    HeroSheet heroSheet = new HeroSheet(workbook);
    heroSheet.insert("(力量", heroParse.getOrder(UnitDetail.Primary.STR));
    heroSheet.insert("敏捷", heroParse.getOrder(UnitDetail.Primary.AGI));
    heroSheet.insert("智力)", heroParse.getOrder(UnitDetail.Primary.INT));

    ItemSheet itemSheet = new ItemSheet(workbook);
    itemSheet.insert("武器", itemParse.getDropUnit("武器"));
    itemSheet.insert("副手", itemParse.getDropUnit("副手"));
    itemSheet.insert("铠甲", itemParse.getDropUnit("铠甲"));
    itemSheet.insert("鞋子", itemParse.getDropUnit("鞋子"));
    itemSheet.insert("饰品", itemParse.getDropUnit("饰品"));
    itemSheet.insert("主线任务所需物品", itemParse.getDropUnit("主线任务所需物品"));
    itemSheet.insert("支线任务", itemParse.getDropUnit("支线任务"));
    itemSheet.insert("材料", itemParse.getDropUnit("材料"));
    itemSheet.insert("物品", itemParse.getDropUnit("物品"));
    itemSheet.insert("食物", itemParse.getDropUnit("食物"));
    itemSheet.insert("消耗品", itemParse.getDropUnit("消耗品"));
    Map<String, List<ItemDetail>> dropUnit = itemParse.getDropUnit("情报");
    itemSheet.insert("情报", dropUnit);
    itemSheet.insert("未归类", itemParse.getDropUnit("未归类"));

    unitSheet.insertHead("更新记录");
    itemSheet.writeTo(excelName);
    System.out.println("输出Excel完成");
  }
}
