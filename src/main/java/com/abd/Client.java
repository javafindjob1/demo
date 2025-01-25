package com.abd;

import com.abd.sqlite.SqLiteJDBC;
import java.util.List;
import java.util.Map;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Client {
  public static void main(String[] args) throws Exception {

    String excelName = "梦想远景装备介绍_v1.1.38.xlsx";
    SqLiteJDBC.setVersion("v1.1.38", "v1.1.28");

    String w3xliniPath = "D:\\war5-jass\\jass_plugin\\w3x2lni_zhCN_v2.5.2\\w3x2lni_zhCN_v2.5.2\\";
    String assetPath = w3xliniPath + "0mp\\4FFD4CA60115240BEFBD7D6278E38E2F\\";
    ExcelImageInsert.set(assetPath);

    List<Item> list = new IniRead().read("template/Custom/item.ini", assetPath + "table\\item.ini", Item.class);
    // list.stream().filter(e->e.getId().equals("kysn")).forEach(System.out::println);;
    System.out.println("读文件完成");
    ItemParse itemParse = new ItemParse();
    Map<String, ItemDetail> idItemMap = itemParse.parse(list);
    System.out.println("解析物品完成");

    List<Unit> unitList = new IniRead().read("template/Custom/unit.ini", assetPath + "table\\unit.ini", Unit.class);
    System.out.println("读单位信息文件完成");
    UnitParse unitParse = new UnitParse();
    Map<String, UnitDetail> idUnitMap = unitParse.parse(unitList);
    System.out.println("解析单位信息完成");
    System.out.println(idUnitMap.get("O009"));

    List<Destructable> destructableList = new IniRead().read("template/Custom/destructable.ini",
        assetPath + "table\\destructable.ini", Destructable.class);
    System.out.println("读文件完成");
    DestructableParse destructableParse = new DestructableParse();
    Map<String, DestructableDetail> destructableMap = destructableParse.parse(destructableList);
    System.out.println("解析箱子完成");

    List<Function> funList = new FunctionRead().read(assetPath + "map\\war3map.j还原256.j");
    System.out.println("读取j文件完成");
    FunctionParse functionParse = new FunctionParse();
    Map<String, FunctionDetail> funDetailList = functionParse.parse(funList);
    System.out.println("解析Function完成");
    System.out.println(funDetailList.containsKey(null));

    List<Ability> abilityList = new IniRead().read("template/Custom/ability.ini", assetPath + "table\\ability.ini",
        Ability.class);
    System.out.println("读取技能完成");
    Map<String, AbilityDetail> abilityMap = AbilityParse.parse(abilityList);
    System.out.println("解析技能完成");

    // 汉化物品名称
    functionParse.wrapItem(idItemMap);
    System.out.println(funDetailList.get("CHj"));

    // 归纳整理单位的掉落信息
    unitParse.wrapDropString(funDetailList, idItemMap);
    // 归纳整理物品的获得途径
    itemParse.wrapDropString(funDetailList, idUnitMap, destructableMap);

    HeroParse heroParse = new HeroParse();
    heroParse.wrapHero(abilityMap, idUnitMap, idItemMap, funList);

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
    itemSheet.insert("情报", itemParse.getDropUnit("情报"));
    itemSheet.insert("未归类", itemParse.getDropUnit("未归类"));

    unitSheet.insertHead("更新记录");
    itemSheet.writeTo(excelName);
    System.out.println("输出Excel完成");
  }
}
