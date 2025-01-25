package com.b4;

import com.b4.sqlite.SqLiteJDBC;

import java.util.List;
import java.util.Map;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Client {
  public static void main(String[] args) throws Exception {

    String excelName = "不可能的BOSS4图鉴_v4.1.391.xlsx";
    SqLiteJDBC.setVersion("v4.1.391", "v4.1.390");

    String w3xliniPath = "D:\\war5-jass\\jass_plugin\\w3x2lni_zhCN_v2.5.2\\w3x2lni_zhCN_v2.5.2\\";
    String assetPath = w3xliniPath + "b4\\6051AC80EA1FAEAEE6E54B83E1959170\\";
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

    List<Ability> abilityList = new IniRead().read("template/Custom/ability.ini", assetPath + "table\\ability.ini",
        Ability.class);
    System.out.println("读取技能完成");
    Map<String, AbilityDetail> abilityMap = AbilityParse.parse(abilityList);
    System.out.println("解析技能完成");

    // 归纳整理单位的掉落信息
    unitParse.wrapDropString(idItemMap);
    // 归纳整理物品的获得途径
    itemParse.wrapDropString();

    HeroParse heroParse = new HeroParse();
    heroParse.wrapHero(abilityMap, idUnitMap, idItemMap);

    // 生成excel
    XSSFWorkbook workbook = new XSSFWorkbook();
    UnitSheet unitSheet = new UnitSheet(workbook, idItemMap);
    unitSheet.insert("单位", unitParse.getDropUnitOrder());
    unitSheet.insert("单位(场景)", unitParse.getDropUnit());

    HeroSheet heroSheet = new HeroSheet(workbook);
    heroSheet.insert("(筋力", heroParse.getOrder(UnitDetail.Primary.STR));
    heroSheet.insert("敏捷", heroParse.getOrder(UnitDetail.Primary.AGI));
    heroSheet.insert("体力)", heroParse.getOrder(UnitDetail.Primary.INT));

    ItemSheet itemSheet = new ItemSheet(workbook);
    itemSheet.insert("武器", itemParse.getDropUnit("武器"));
    itemSheet.insert("副武器", itemParse.getDropUnit("副武器"));
    itemSheet.insert("头部道具", itemParse.getDropUnit("头部道具"));
    itemSheet.insert("装甲", itemParse.getDropUnit("装甲"));
    itemSheet.insert("道具(项链,手套,戒指,鞋子,灵魂)", itemParse.getDropUnit("道具(项链,手套,戒指,鞋子,灵魂)"));
    itemSheet.insert("不归类", itemParse.getDropUnit("不归类"));
    itemSheet.insert("未归类", itemParse.getDropUnit("未归类"));

    unitSheet.insertHead("更新记录");
    itemSheet.writeTo(excelName);
    System.out.println("输出Excel完成");

    System.out.println(ItemDetail.levelMap);
    System.out.println("---------");
    System.out.println(ItemDetail.typeMap);
  }
}
