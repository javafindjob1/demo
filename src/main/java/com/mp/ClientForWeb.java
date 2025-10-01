package com.mp;

import com.mp.function.HeroData;
import com.mp.parse.AbilityDetail;
import com.mp.parse.AbilityParse;
import com.mp.parse.DestructableDetail;
import com.mp.parse.DestructableParse;
import com.mp.parse.ExcelImageInsert;
import com.mp.parse.FunctionDetail;
import com.mp.parse.FunctionParse;
import com.mp.parse.FunctionRead;
import com.mp.parse.HeroParse;
import com.mp.parse.ItemDetail;
import com.mp.parse.ItemParse;
import com.mp.parse.MdxRead;
import com.mp.parse.UnitDetail;
import com.mp.parse.UnitParse;
import com.mp.sqlite.SqLiteJDBC;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.common.ini.Ability;
import com.common.ini.Destructable;
import com.common.ini.IniRead;
import com.common.ini.Item;
import com.common.ini.Unit;
import com.common.parse.Function;
import com.util.ImageMerger;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

public class ClientForWeb {
  public static void main(String[] args) throws Exception {
    String w3xliniPath = "D:\\war5-jass\\jass_plugin\\w3x2lni_zhCN_v2.5.2\\w3x2lni_zhCN_v2.5.2\\";
    String assetPath = w3xliniPath + "0mp\\FB6D1E69E144A72B06A159542DBF51A3\\";
    ExcelImageInsert.set(assetPath, ClientForWeb.class);

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

    List<Function> funList = new FunctionRead().read(assetPath + "map\\war3map.j还原256.j");
    System.out.println("读取j文件完成");
    FunctionParse functionParse = new FunctionParse();
    Map<String, FunctionDetail> funDetailList = functionParse.parse(funList);
    System.out.println("解析Function完成");

    List<Ability> abilityList = new IniRead().read("template/Custom/ability.ini", assetPath + "table\\ability.ini",
        Ability.class);
    System.out.println("读取ability.ini完成");
    Map<String, AbilityDetail> abilityMap = AbilityParse.parse(abilityList);
    System.out.println("解析技能完成");

    // 汉化物品名称
    functionParse.wrapItem(idItemMap);
    // 归纳整理单位的掉落信息
    unitParse.wrapDropString(idItemMap, funDetailList);
    // 归纳整理物品的获得途径
    itemParse.wrapDropString(idUnitMap, destructableMap, funDetailList);

    HeroParse heroParse = new HeroParse();
    heroParse.wrapHero(abilityMap, idUnitMap, idItemMap, funList);
    Map<String, HeroData> map = heroParse.wrapHeroData(abilityMap, idUnitMap, idItemMap, funList);

    try (BufferedWriter br = new BufferedWriter(new OutputStreamWriter(
        new FileOutputStream("html\\javafindjob1.github.io\\mp\\mp-data.js"),
        "utf-8"))) {
      br.write("var mpdata = " + JSON.toJSONString(map));
    }
    System.out.println(JSON.toJSONString(map.get("N00E"), SerializerFeature.PrettyFormat));

    List<UnitDetail> heroList = new ArrayList<>();
    for (String unitId : map.keySet()) {
      UnitDetail detail = idUnitMap.get(unitId);
      heroList.add(detail);
    }
    MdxRead.generateCopyMdxTask(heroList, assetPath + "resource\\", "html\\javafindjob1.github.io\\mp\\mp-mdx\\");


    Map<String, File> fileFullPathMap = new HashMap<>();
    File dir = new File("D:\\war5-jass\\jass_plugin\\新建文件夹3");
    File[] listFiles = dir.listFiles();
    Pattern pa = Pattern.compile("^(\\d+).*");
    for (File file : listFiles) {
      Matcher matcher = pa.matcher(file.getName());
      if (matcher.find()) {
        String loc = matcher.group(1);
        fileFullPathMap.put(loc, file);
      }
    }

    String[][] heroArr = new String[][] {
        { "O000", "E011", "E004", "E008", "E016", "H00S" },
        { "O001", "O003", "E015", "H006", "H007", "H00H" },
        { "O004", "E01H", "O00M", "N06G", "E014", "H005" },
        { "N02D", "O00X", "", "O002", "H00B", "H00U" },
        { "O012", "O014", "O015", "E01Q", "E01R", "H00Y"},
    };
    String basePath = "html\\javafindjob1.github.io\\mp\\mp-imgs\\";
    { 
      // 测试合并
      HeroData hero = map.get("O000");
      String[][] iconPaths = hero.parseIconPath();
      ImageMerger.merge(iconPaths, "out.webp", 64, 64, basePath);
    }
    // width 414 height 274
    BufferedImage[][] imgHero = new BufferedImage[heroArr.length][];
    for (int i = 0; i < heroArr.length; i++) {
      imgHero[i] = new BufferedImage[heroArr[i].length];
      for (int j = 0; j < heroArr[i].length; j++) {
        String unitId = heroArr[i][j];
        BufferedImage img = null;
        if (unitId == null || unitId.isEmpty()) {
          img = null;
        } else {
          HeroData hero = map.get(unitId);
          BufferedImage img0 = ImageMerger.mergeImages(ImageMerger.readPath(hero.parseIconPath(), basePath), 64, 64);
          BufferedImage img0h = ImageMerger.readFile(fileFullPathMap.get((i * 2 + 1) + "" + (j + 1)));
          img0 = ImageMerger.mergeImages(new BufferedImage[][] { { img0h, img0 } }, 145, 210 - 6);

          BufferedImage imgp1 = null;
          if (hero.getP1() != null) {
            hero = map.get(hero.getP1().getUnitId());
            imgp1 = ImageMerger.mergeImages(ImageMerger.readPath(hero.parseIconPath(), basePath), 64, 64);

            BufferedImage imgp1h = ImageMerger.readFile(fileFullPathMap.get((i * 2 + 2) + "" + (j + 1)));
            imgp1 = ImageMerger.mergeImages(new BufferedImage[][] { { imgp1h, imgp1 } }, 145, 210 - 6);
          }

          BufferedImage[][] imgItems = new BufferedImage[][] {
              { img0 },
              { imgp1 },
          };
          img = ImageMerger.mergeImages(imgItems, 420 + 145, 210 - 6);

          // ImageIO.write(img, "webp", new File(basePath + unitId +"out.webp"));
          // if (true)
          // throw new RuntimeException();
        }
        imgHero[i][j] = img;
      }
    }

    BufferedImage mergedImage = ImageMerger.mergeImages(imgHero, 420 + 145, 420 - 6);

    String[][] panel = new String[][] {
        {
            "ATTshuxing-str.webp",
            "ATTshuxing-agi.webp",
            "ATTshuxing-int.webp",
            "TYPElightdef.webp",
            "TYPEdarkdef.webp",
            "TYPEwaterdef.webp",
            "TYPEfiredef.webp",
            "TYPEstonedef.webp",
            "TYPEwinddef.webp",
            "TYPEwateratk.webp",
            "TYPEdarkatk.webp",
            "TYPElightatk.webp",
            "TYPEstoneatk.webp",
            "TYPEstoneatk.webp",
            "TYPEwindatk.webp",
            "TYPEfireatk.webp",
        }
    };
    BufferedImage img0 = ImageMerger.mergeImages(ImageMerger.readPath(panel, basePath), 64, 64);

    mergedImage = ImageMerger.mergeImages(new BufferedImage[][] { { mergedImage }, { img0 } }, 64, 64);

    ImageIO.write(mergedImage, "webp", new File(basePath + "out.webp"));

  }
}
