package com.b4;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.netty.util.CharsetUtil;

public class MdxRead {
  public static void main(String[] args) throws Exception {

    // List<String> ff = getTextures("test/mdxfiles/mdx-e02Z-[hr]lrv_ms.mdx");

    String assetPath = "D:\\war5-jass\\jass_plugin\\w3x2lni_zhCN_v2.5.2\\w3x2lni_zhCN_v2.5.2\\";
    assetPath += "F39DB93EB93346004EB6613DF7F29F54\\F39DB93EB93346004EB6613DF7F29F54\\";
    ExcelImageInsert.set(assetPath);

    // parseMdx("ss", "Abilities\\Spells\\Items\\StaffOfPurification\\PurificationCaster.mdl");
    // if (true)
    //   return;

    List<Unit> list = new IniRead().read("template/Custom/unit.ini", assetPath + "table\\unit.ini", Unit.class);
    Map<String, UnitDetail> unitMap = new UnitParse().parse(list);
    List<UnitDetail> u = unitMap.values().stream().filter(e -> {
      // return e.getType().contains("giant");
      return e.getId().contains("H005");
      // return !e.getAbilList().contains("AInv");
      // return e.getAbilList().contains("AInv");
    }).collect(Collectors.toList());

    for (UnitDetail unit : u) {
      String mdxfile = unit.getFile();
      System.out.println("单位名称:" + unit.getName() + ",propName:" + unit.getPropernames());
     
      try {
        parseMdx(unit.getId(), mdxfile);
      } catch (IOException e1) {
        e1.printStackTrace();
        System.out.println("文件未找到");
      }
    }

  }

  public static void parseMdx(String unitId, String mdxfile) throws IOException {
    if (!mdxfile.endsWith("mdx")) {
      if (mdxfile.endsWith("mdl")) {
        mdxfile = mdxfile.replace(".mdl", ".mdx");
      } else if (mdxfile.indexOf(".") > -1) {
        System.out.println("文件名不合法:" + mdxfile);
      } else {
        mdxfile += ".mdx";
      }
    }
    
    String mdxFullPath = ExcelImageInsert.combineFullPath(mdxfile);

    List<String> noexistblp = new ArrayList<>();
    Map<String, String> dataMap = new HashMap<>();
    dataMap.put("mdx", mdxFullPath);
    List<String> blps = getTextures(mdxFullPath);

    blps.stream().forEach(blp -> {
      System.out.println("开始处理:" + blp);
      while (true) {
        try {
          String fullPath = ExcelImageInsert.combineFullPath(blp);
          dataMap.put(blp, fullPath);
          System.out.println("贴图全路径:" + fullPath);
          break;
        } catch (FileNotFoundException e) {
          if (blp.length() > 5) {
            blp = blp.substring(1).trim();
          }
        }
      }
    });

    System.out.println(noexistblp);
    {
      String mdxf = dataMap.remove("mdx");
      copyfile(mdxf, "test/mdxfiles/mdx-" + unitId + "-" + new File(mdxf).getName());
      dataMap.forEach((blp, blpPath) -> {
        copyfile(blpPath, "test/mdxfiles/" + blp);
      });
    }
  }

  public static void createPath(File file) {
    if (file.exists()) {
      return;
    } else {
      createPath(file.getParentFile());
      file.mkdir();
    }
  }

  public static void copyfile(String srcPath, String destPath) {
    createPath(new File(destPath).getParentFile());

    try (
        FileInputStream in = new FileInputStream(srcPath);
        FileChannel fc = in.getChannel();

        FileOutputStream os = new FileOutputStream(destPath);
        FileChannel fcc = os.getChannel();) {
      fc.transferTo(0, fc.size(), fcc);
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("复制出错!!" + srcPath);
      System.out.println("复制出错!!" + destPath);
      throw new RuntimeException();
    }

  }

  private static List<String> getTextures(String mdxPath) throws IOException, FileNotFoundException {
    List<String> list = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(
        new InputStreamReader(new FileInputStream(mdxPath), CharsetUtil.US_ASCII))) {
      String line = null;
      while ((line = br.readLine()) != null) {
        // TEXS$units\Creeps\MurlocFleshEater\MurlocFleshEater.blpTextures\TeamGlow00.blp
        if (line.contains("TEXS")) {

          System.out.println(line);
          if (line.indexOf(".blp") > -1) {
            List<String> textures = parseTexture(line, ".blp");
            list.addAll(textures);
          }

          if (line.indexOf(".tga") > -1) {
            List<String> textures = parseTexture(line, ".tga");
            list.addAll(textures);
          }

        }
      }
    }
    return list;
  }

  public static List<String> parseTexture(String line, String key) throws IOException, FileNotFoundException {
    int i = line.indexOf("TEXS") + 4;

    List<String> list = new ArrayList<>();

    int j = -1;
    while ((j = line.indexOf(key, i)) > -1) {
      j += key.length();
      String blp = line.substring(i, j);
      System.out.println(blp);
      for (int j2 = 0; j2 < blp.length(); j2++) {
        char charAt = blp.charAt(j2);
        if (charAt >= 'A' && charAt <= 'Z' || charAt >= 'a' && charAt <= 'z' || charAt >= '0' && charAt <= '9') {
          blp = blp.substring(j2);
          break;
        }
      }
      list.add(blp);
      i = j;
    }

    return list;
  }

}
