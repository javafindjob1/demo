package com.abc;

import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.abc.sqlite.SqLiteJDBC;

/**
 * O006 哥布林大少爷
 * H007 皇家骑士
 * O007 火葬王
 * U002 幽灵船长
 * H008 冰封女巫
 * 
 * E01M 幻象古神
 * U004 岩浆魔人
 * O00A 疯狂科学家
 * U005 黑曜城主
 * E01U 魔蝇人
 * E02P 祖海遗迹国王
 * H011 堕落教母
 * Otch 僵尸皇帝
 * O00V 孕核魔胎
 * U008 遥远救世主
 */

public class Client {
  public static void main(String[] args) throws Exception {

    String excelName = "西7装备介绍_v2.0.5.xlsx";
    SqLiteJDBC.setVersion("v2.0.5", "v2.0.0");
    String w3xliniPath = "D:\\war5-jass\\jass_plugin\\w3x2lni_zhCN_v2.5.2\\w3x2lni_zhCN_v2.5.2\\";
    String assetPath = w3xliniPath + "0x7\\4AE2E0C9550414EB2F8BC32CE213965E\\";
    ExcelImageInsert.set(assetPath);

    List<Item> list = new IniRead().read("template/Custom/item.ini", assetPath + "table\\item.ini", Item.class);
    // list.stream().filter(e->e.getId().equals("kysn")).forEach(System.out::println);;
    System.out.println("读文件完成");
    ItemParse itemParse = new ItemParse();
    Map<String, ItemDetail> idItemMap = itemParse.parse(list);

    System.out.println("解析物品完成");

    List<Function> funList = new FunctionRead().read(assetPath + "map\\war3map.j还原256.j");
    System.out.println("读取j文件完成");
    FunctionParse functionParse = new FunctionParse();
    Map<String, FunctionDetail> funDetailList = functionParse.parse(funList);
    System.out.println("解析Function完成");
    System.out.println(funDetailList.get("CHj"));

    List<Unit> unitList = new IniRead().read("template/Custom/unit.ini", assetPath + "table\\unit.ini", Unit.class);
    System.out.println("读单位信息文件完成");
    UnitParse unitParse = new UnitParse();
    Map<String, UnitDetail> idUnitMap = unitParse.parse(unitList);
    System.out.println("解析单位信息完成");
    System.out.println(idUnitMap.get("O009"));

    functionParse.wrapItem(idItemMap);
    unitParse.wrapDropString(funDetailList);
    itemParse.wrapDropString(funDetailList, idUnitMap);

    XSSFWorkbook workbook = new XSSFWorkbook();
    UnitSheet unitSheet = new UnitSheet(workbook, idItemMap);
    unitSheet.insert("单位", unitParse.getDropUnitOrder());
    unitSheet.insert("单位(场景)", unitParse.getDropUnit());

    ItemSheet itemSheet = new ItemSheet(workbook);
    itemSheet.insert("武器", itemParse.getDropUnit("武器"));
    itemSheet.insert("衣服", itemParse.getDropUnit("衣服"));
    itemSheet.insert("饰品", itemParse.getDropUnit("饰品"));
    itemSheet.insert("鞋子", itemParse.getDropUnit("鞋子"));
    itemSheet.insert("特殊", itemParse.getDropUnit("特殊"));
    itemSheet.insert("灵药", itemParse.getDropUnit("灵药"));
    itemSheet.insert("材料", itemParse.getDropUnit("材料"));
    itemSheet.insert("饰品、特殊", itemParse.getDropUnit("饰品、特殊"));
    itemSheet.insert("未归类", itemParse.getDropUnit("未归类"));

    unitSheet.insertHead("更新记录");
    itemSheet.writeTo(excelName);
    System.out.println("输出Excel完成"); 
  }
}
