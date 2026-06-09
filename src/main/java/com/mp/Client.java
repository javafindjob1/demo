package com.mp;

import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.common.ini.Ability;
import com.common.ini.Destructable;
import com.common.ini.IniRead;
import com.common.ini.Item;
import com.common.ini.Unit;
import com.mp.parse.AbilityDetail;
import com.mp.parse.AbilityParse;
import com.mp.parse.DestructableDetail;
import com.mp.parse.DestructableParse;
import com.mp.parse.ExcelImageInsert;
import com.mp.parse.HeroParse;
import com.mp.parse.ItemDetail;
import com.mp.parse.ItemParse;
import com.mp.parse.ItemSheet;
import com.mp.parse.LingeSheet;
import com.mp.parse.UnitDetail;
import com.mp.parse.UnitParse;
import com.mp.parse.UnitSheet;
import com.mp.sqlite.SqLiteJDBC;

public class Client {
  public static void main(String[] args) throws Exception {

    String excelName = "梦想远景装备介绍_v1.8.2.xlsx";
    SqLiteJDBC.setVersion("v1.8.2", "v1.7.2");

    String w3xliniPath = "D:\\war5-jass\\jass_plugin\\w3x2lni_zhCN_v2.5.2\\w3x2lni_zhCN_v2.5.2\\";
    String assetPath = w3xliniPath + "0mp\\FE4EDA8C6CC7212BF8C63B3BA6E3915B\\";
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

    unitParse.wrapDropString(idItemMap, null);
    itemParse.wrapDropString(idUnitMap, destructableMap, null);
    HeroParse heroParse = new HeroParse();

    // 生成excel
    XSSFWorkbook workbook = new XSSFWorkbook();
    UnitSheet unitSheet = new UnitSheet(workbook, idItemMap);
    unitSheet.insert("单位", unitParse.getDropUnitOrder());
    unitSheet.insert("单位(场景)", unitParse.getDropUnit());

    LingeSheet lingeSheet = new LingeSheet(workbook, abilityMap);
    lingeSheet.insert("灵格");

    ItemSheet itemSheet = new ItemSheet(workbook);
    itemSheet.insert("武器", itemParse.getDropUnit("武器"));
    itemSheet.insert("副手", itemParse.getDropUnit("副手"));
    itemSheet.insert("铠甲", itemParse.getDropUnit("铠甲"));
    itemSheet.insert("鞋子", itemParse.getDropUnit("鞋子"));
    itemSheet.insert("饰品", itemParse.getDropUnit("饰品"));
    itemSheet.insert("主线任务所需物品", itemParse.getDropUnit("主线任务所需物品"));
    itemSheet.insert("支线任务", itemParse.getDropUnit("支线任务"));
    itemSheet.insert("材料", itemParse.getDropUnit("材料"));
    itemSheet.insert("食物", itemParse.getDropUnit("食物"));
    itemSheet.insert("纪念碑", itemParse.getDropUnit("纪念碑"));
    itemSheet.insert("特殊", itemParse.getDropUnit("特殊"));
    Map<String, List<ItemDetail>> dropUnit = itemParse.getDropUnit("套装");
    itemSheet.insert("套装", dropUnit);
    itemSheet.insert("未归类", itemParse.getDropUnit("未归类"));

    unitSheet.insertHead("更新记录", heroParse.getRes());
    itemSheet.writeTo(excelName);
    System.out.println("输出Excel完成");
  }
}
