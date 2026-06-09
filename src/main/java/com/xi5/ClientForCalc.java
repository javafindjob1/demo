package com.xi5;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import com.common.ini.Ability;
import com.common.ini.Destructable;
import com.common.ini.IniRead;
import com.common.ini.Item;
import com.common.ini.Unit;
import com.xi5.parse.AbilityDetail;
import com.xi5.parse.AbilityParse;
import com.xi5.parse.DestructableDetail;
import com.xi5.parse.DestructableParse;
import com.xi5.parse.ExcelImageInsert;
import com.xi5.parse.ItemDetail;
import com.xi5.parse.ItemParse;
import com.xi5.parse.UnitDetail;
import com.xi5.parse.UnitParse;
import com.xi5.sqlite.SqLiteJDBC;

public class ClientForCalc {
    public static void main(String[] args) throws Exception {

        String excelName = "西方世界的劫难6装备介绍_v2.6.3.xlsx";
        SqLiteJDBC.setVersion("v2.6.3", "v2.6.3");

        String w3xliniPath = "D:\\war5-jass\\jass_plugin\\w3x2lni_zhCN_v2.5.2\\w3x2lni_zhCN_v2.5.2\\";
        String assetPath = w3xliniPath + "0x5\\BFE34A27828074CDD3AFD5F01D0A987B\\";
        ExcelImageInsert.set(assetPath, Client.class);

        List<Item> list = IniRead.read("template/Custom/item.ini", assetPath + "table\\item.ini", Item.class);
        System.out.println("读item.ini完成");
        ItemParse itemParse = new ItemParse();
        Map<String, ItemDetail> idItemMap = itemParse.parse(list);
        Map<String, ItemDetail> nameItemMap = itemParse.getNameMap();
        System.out.println("解析物品完成");

        List<Unit> unitList = IniRead.read("template/Custom/unit.ini", assetPath + "table\\unit.ini", Unit.class);
        System.out.println("读unit.ini完成");
        UnitParse unitParse = new UnitParse();
        Map<String, UnitDetail> idUnitMap = unitParse.parse(unitList);
        System.out.println("解析单位信息完成");

        List<Destructable> destructableList = IniRead.read("template/Custom/destructable.ini", assetPath + "table\\destructable.ini", Destructable.class);
        System.out.println("读destructable.ini完成");
        DestructableParse destructableParse = new DestructableParse();
        Map<String, DestructableDetail> destructableMap = destructableParse.parse(destructableList);
        System.out.println("解析箱子完成");

        List<Ability> abilityList = IniRead.read("template/Custom/ability.ini", assetPath + "table\\ability.ini", Ability.class);
        System.out.println("读取ability.ini完成");
        Map<String, AbilityDetail> abilityMap = AbilityParse.parse(abilityList);
        System.out.println("解析技能完成");

        int total = 0;
        for (ItemDetail it : idItemMap.values()) {
            if (it.getLevelClass().equals("7,ITEM_TYPE_CAMPAIGN") && "1".equals(it.getPickRandom())) {
                total++;
                System.out.println(it.getName());
            }
        }
        System.out.println("total = " + total);

        parseID(nameItemMap);
    }

    public static void parseID(Map<String, ItemDetail> nameItemMap) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get("D:\\Code\\demo\\demo\\src\\main\\java\\com\\xi5\\custom\\test")), "utf8"));
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(Paths.get("D:\\Code\\demo\\demo\\src\\main\\java\\com\\xi5\\custom\\testout")), "utf8"))) {

            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()){
                    bw.write("\n");
                    continue;
                }
                String id = line;
                ItemDetail item = nameItemMap.get(line);
                if (item == null)
                    bw.write("=" + line + "\n");
                else
                    bw.write(item.getId() + "=" + line + "\n");
            }

        }

    }
}
