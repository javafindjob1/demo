package com.xi4;

import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.common.ini.Ability;
import com.common.ini.Destructable;
import com.common.ini.HeroGroup;
import com.common.ini.IniRead;
import com.common.ini.Item;
import com.common.ini.Unit;
import com.xi4.parse.AbilityDetail;
import com.xi4.parse.AbilityParse;
import com.xi4.parse.DestructableDetail;
import com.xi4.parse.DestructableParse;
import com.xi4.parse.ExcelImageInsert;
import com.xi4.parse.HeroParse;
import com.xi4.parse.HeroSheet;
import com.xi4.parse.ItemDetail;
import com.xi4.parse.ItemParse;
import com.xi4.parse.ItemSheet;
import com.xi4.parse.UnitDetail;
import com.xi4.parse.UnitParse;
import com.xi4.parse.UnitSheet;
import com.xi4.sqlite.SqLiteJDBC;

public class ClientForCalc {
  public static void main(String[] args) throws Exception {

    String excelName = "西方世界的劫难4装备介绍_v2.1.9.xlsx";
    SqLiteJDBC.setVersion("v2.1.9", "v2.1.9");

    String w3xliniPath = "D:\\war5-jass\\jass_plugin\\w3x2lni_zhCN_v2.5.2\\w3x2lni_zhCN_v2.5.2\\";
    String assetPath = w3xliniPath + "0x4\\E2AF578809778F1821BF50DB4ECC3BAD\\";
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

    int total = 0;
    for(ItemDetail it : idItemMap.values()){
      if(it.getLevelClass().equals("7,ITEM_TYPE_CAMPAIGN") && "1".equals(it.getPickRandom())){
        total ++;
        System.out.println(it.getName());
      }
    }
    System.out.println("total = " + total);
    
  }
}
