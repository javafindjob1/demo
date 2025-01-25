package com.abc;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.net.URLDecoder;
import java.nio.Buffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import com.abc.fun.Hero;
import com.abc.fun.HeroIntroParse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.util.ImageMerger;

public class HeroParse {

  public static void main(String[] args) throws Exception {
    System.out.println(11234);
    String assetPath = "D:\\war5-jass\\jass_plugin\\w3x2lni_zhCN_v2.5.2\\w3x2lni_zhCN_v2.5.2\\";
    assetPath += "0x7\\F89770BCB2CE413E0608677D93A1F290\\";
    ExcelImageInsert.set(assetPath);

    List<Function> funList = new FunctionRead()
        .read(URLDecoder.decode(FunctionParse.class.getResource("custom/war3map.j还原256.j").getPath(), "utf8"));

    List<Item> list = new IniRead().read("template/Custom/item.ini",
        FunctionParse.class.getResource("custom/item.ini").getPath(), Item.class);
    ItemParse itemParse = new ItemParse();
    Map<String, ItemDetail> idItemMap = itemParse.parse(list);

    FunctionParse functionParse = new FunctionParse();
    Map<String, FunctionDetail> funDetailList = functionParse.parse(funList);

    List<Unit> unitList = new IniRead().read("template/Custom/unit.ini",
        FunctionParse.class.getResource("custom/unit.ini").getPath(), Unit.class);
    UnitParse unitParse = new UnitParse();
    Map<String, UnitDetail> idUnitMap = unitParse.parse(unitList);

    functionParse.wrapItem(idItemMap);
    unitParse.wrapDropString(funDetailList);
    itemParse.wrapDropString(funDetailList, idUnitMap);

    List<Ability> abilityList = new IniRead().read("template/Custom/ability.ini",
        FunctionParse.class.getResource("custom/ability.ini").getPath(), Ability.class);
    Map<String, AbilityDetail> abilityMap = AbilityParse.parse(abilityList);

    Map<String, UnitDetail> heroMap = unitParse.getHero();
    Map<String, Hero> map = new HeroIntroParse().parse(funList, heroMap, abilityMap, idItemMap);

    // System.out.println(JSON.toJSONString(map, SerializerFeature.PrettyFormat));
    System.out.println(JSON.toJSONString(map.get("H01E"), SerializerFeature.PrettyFormat));

    try (BufferedWriter br = new BufferedWriter(new OutputStreamWriter(
        new FileOutputStream("C:\\Users\\76769\\Desktop\\demo\\html\\javafindjob1.github.io\\x7\\x7-data.js"),
        "utf-8"))) {
      // br.write("var x7data = " + JSON.toJSONString(map,
      // SerializerFeature.PrettyFormat));
      br.write("var x7data = " + JSON.toJSONString(map));
    }

    if(true)return;
    // 合并图片
    // heroArr : [
    // [""],
    // ["", "H00X", "H00F", "H003", "H00D", "E006", "O005", "H002", "O001", "O000",
    // ""],
    // ["", "U000", "E003", "E00Y", "O003", "H001", "O002", "N006", "H005", "H00E",
    // ""],
    // ["", "U001", "H000", "O00J", "H006", "E021", "H00G", "E00T", "E00C", "E029",
    // ""],
    // ["", "O010", "", "E03O", "O018", ""],
    // [""],
    // ],

    Map<String, File> fileFullPathMap = new HashMap<>();
    File dir = new File("C:\\Users\\76769\\Desktop\\新建文件夹");
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
        { "H00X", "H00F", "H003", "H00D", "E006", "O005", "H002", "O001", "O000" },
        { "U000", "E003", "E00Y", "O003", "H001", "O002", "N006", "H005", "H00E" },
        { "U001", "H000", "O00J", "H006", "E021", "H00G", "E00T", "E00C", "E029" },
        { "O010", "", "E03O", "O018" },
    };
    Hero hero = map.get("H01E");
    String[][] iconPaths = hero.parseIconPath();
    String basePath = "html\\javafindjob1.github.io\\x7\\x7-imgs\\";
    ImageMerger.merge(iconPaths, "out.webp", 64, 64, basePath);
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
          hero = map.get(unitId);
          BufferedImage img0 = ImageMerger.mergeImages(ImageMerger.readPath(hero.parseIconPath(), basePath), 64, 64);
          BufferedImage img0h = ImageMerger.readFile(fileFullPathMap.get((i * 3 + 1) + "" + (j + 1)));
          img0 = ImageMerger.mergeImages(new BufferedImage[][] { { img0h, img0 } }, 145, 210 - 6);

          BufferedImage imgp1 = null;
          if (hero.getP1() != null) {
            hero = map.get(hero.getP1().getUnitId());
            imgp1 = ImageMerger.mergeImages(ImageMerger.readPath(hero.parseIconPath(), basePath), 64, 64);

            BufferedImage imgp1h = ImageMerger.readFile(fileFullPathMap.get((i * 3 + 2) + "" + (j + 1)));
            imgp1 = ImageMerger.mergeImages(new BufferedImage[][] { { imgp1h, imgp1 } }, 145, 210 - 6);
          }
          BufferedImage imgp2 = null;
          if (hero.getP2() != null) {
            hero = map.get(hero.getP2().getUnitId());
            imgp2 = ImageMerger.mergeImages(ImageMerger.readPath(hero.parseIconPath(), basePath), 64, 64);

            BufferedImage imgp2h = ImageMerger.readFile(fileFullPathMap.get((i * 3 + 3) + "" + (j + 1)));
            imgp2 = ImageMerger.mergeImages(new BufferedImage[][] { { imgp2h, imgp2 } }, 145, 210 - 6);
          }

          BufferedImage[][] imgItems = new BufferedImage[][] {
              { img0 },
              { imgp1 },
              { imgp2 }
          };
          img = ImageMerger.mergeImages(imgItems, 420 + 145, 210 - 6);

          // ImageIO.write(img, "webp", new File(basePath + unitId +"out.webp"));
          // if (true)
          // throw new RuntimeException();
        }
        imgHero[i][j] = img;
      }
    }

    BufferedImage mergedImage = ImageMerger.mergeImages(imgHero, 420 + 145, 630 - 6);

    String[][] panel = new String[][] {
        { "UI\\Widgets\\Console\\Human\\infocard-heroattributes-str.webp",
            "UI\\Widgets\\Console\\Human\\infocard-heroattributes-agi.webp",
            "UI\\Widgets\\Console\\Human\\infocard-heroattributes-int.webp",
            "war3mapImported\\tYPEDeFend_Light.webp",
            "war3mapImported\\tYPEDefDark.webp",
            "war3mapImported\\tYPEDefend_Land.webp",
            "war3mapImported\\tYPESystemPic_Defend_Wind.webp",
            "war3mapImported\\tYPEDeFend_Water.webp",
            "war3mapImported\\tYPEDeFend_Fire.webp",
            "war3mapImported\\tYPEAttack_Fire.webp",
            "war3mapImported\\tYPESystemPic_Attack_Dark.webp",
            "war3mapImported\\tYPEAttack_Light.webp",
            "war3mapImported\\tYPEAttack_Water.webp",
            "war3mapImported\\tYPEAttack_Water.webp",
            "war3mapImported\\tYPEAttack2_Feng.webp",
            "war3mapImported\\tYPEAttackLand.webp",
        }
    };
    BufferedImage img0 = ImageMerger.mergeImages(ImageMerger.readPath(panel, basePath), 64, 64);

    mergedImage = ImageMerger.mergeImages(new BufferedImage[][] { { mergedImage }, { img0 } }, 64, 64);

    ImageIO.write(mergedImage, "webp", new File(basePath + "out.webp"));

  }
}
