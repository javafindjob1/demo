package com.xi3;

import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.common.ini.Ability;
import com.common.ini.Destructable;
import com.common.ini.HeroGroup;
import com.common.ini.IniRead;
import com.common.ini.Item;
import com.common.ini.Unit;
import com.xi3.parse.AbilityDetail;
import com.xi3.parse.AbilityParse;
import com.xi3.parse.DestructableDetail;
import com.xi3.parse.DestructableParse;
import com.xi3.parse.ExcelImageInsert;
import com.xi3.parse.HeroParse;
import com.xi3.parse.HeroSheet;
import com.xi3.parse.ItemDetail;
import com.xi3.parse.ItemParse;
import com.xi3.parse.ItemSheet;
import com.xi3.parse.UnitDetail;
import com.xi3.parse.UnitParse;
import com.xi3.parse.UnitSheet;
import com.xi3.sqlite.SqLiteJDBC;

public class Client {
  public static void main(String[] args) throws Exception {

    String excelName = "西3装备介绍_v2.4.6.xlsx";
    SqLiteJDBC.setVersion("v2.4.6", "v2.4.6");

    String w3xliniPath = "D:\\war5-jass\\jass_plugin\\w3x2lni_zhCN_v2.5.2\\w3x2lni_zhCN_v2.5.2\\";
    String assetPath = w3xliniPath + "0x3\\510F7E04DB3FFE578BF1F04D9ED3E37D\\";
    ExcelImageInsert.set(assetPath, Client.class);

    List<Ability> abilityList = IniRead.read("template/Custom/ability.ini", assetPath + "table\\ability.ini",
            Ability.class);
    System.out.println("读取ability.ini完成");
    Map<String, AbilityDetail> abilityMap = AbilityParse.parse(abilityList);
    System.out.println("解析技能完成");

    List<Item> list = IniRead.read("template/Custom/item.ini", assetPath + "table\\item.ini", Item.class);
    System.out.println("读item.ini完成");
    ItemParse itemParse = new ItemParse();
    Map<String, ItemDetail> idItemMap = itemParse.parse(list, abilityMap);
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

    HeroGroup heroGroup = new HeroGroup(IniRead.read2(Client.class.getResourceAsStream("custom/hero.ini")));

    unitParse.wrapDropString(idItemMap,null);
    itemParse.wrapDropString(idUnitMap, destructableMap, null);
    HeroParse heroParse = new HeroParse();
    heroParse.wrapHero(abilityMap, idUnitMap, idItemMap, heroGroup);

    System.out.println(idItemMap.get("I060").getName());
    // 生成excel
    XSSFWorkbook workbook = new XSSFWorkbook();
    UnitSheet unitSheet = new UnitSheet(workbook, idItemMap);
    // unitSheet.insert("单位", unitParse.getDropUnitOrder());
    unitSheet.insert("商店", unitParse.getDropUnit());

    HeroSheet heroSheet = new HeroSheet(workbook);
    heroSheet.insert("(力量", heroParse.getOrder(UnitDetail.Primary.STR));
    heroSheet.insert("敏捷", heroParse.getOrder(UnitDetail.Primary.AGI));
    heroSheet.insert("智力)", heroParse.getOrder(UnitDetail.Primary.INT));

    ItemSheet itemSheet = new ItemSheet(workbook);
    itemSheet.insert("武器", itemParse.getDropUnit("武器"));
    itemSheet.insert("衣服", itemParse.getDropUnit("衣服"));
    itemSheet.insert("鞋子", itemParse.getDropUnit("鞋子"));
    itemSheet.insert("饰品", itemParse.getDropUnit("饰品"));
    itemSheet.insert("特殊", itemParse.getDropUnit("特殊"));
    itemSheet.insert("秘药", itemParse.getDropUnit("秘药"));
    itemSheet.insert("材料", itemParse.getDropUnit("材料"));
    itemSheet.insert("套装", itemParse.getDropUnit("套装"));
    // itemSheet.insert("未分类", itemParse.getDropUnit("未分类"));
    unitSheet.insertHead("更新记录", heroParse.getRes());
    itemSheet.writeTo(excelName);
    System.out.println("输出Excel完成");
  }
}
