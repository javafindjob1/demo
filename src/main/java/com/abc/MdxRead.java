package com.abc;

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
    // if (true)
    //   return;

    String assetPath = "D:\\war5-jass\\jass_plugin\\w3x2lni_zhCN_v2.5.2\\w3x2lni_zhCN_v2.5.2\\";
    assetPath += "0x7\\F89770BCB2CE413E0608677D93A1F290\\";
    ExcelImageInsert.set(assetPath);

    List<Unit> list = new IniRead().read("template/Custom/unit.ini", assetPath + "table/unit.ini", Unit.class);
    Map<String, UnitDetail> unitMap = new UnitParse().parse(list);
    List<UnitDetail> u = unitMap.values().stream().filter(e -> {
      // return e.getType().contains("giant");
      return e.getId().contains("nmgr");
      // return !e.getAbilList().contains("AInv");
      // return e.getAbilList().contains("AInv");
    }).collect(Collectors.toList());

    Map<String, List<String>> noexistblp = new HashMap<>();
    Map<String, Map<String, String>> dataMap = new HashMap<>();
    u.stream().forEach(unit -> {
      String mdxfile = unit.getFile();
      System.out.println("单位名称:" + unit.getName() + ",propName:" + unit.getPropernames());
      if (!mdxfile.endsWith("mdx")) {
        if (mdxfile.endsWith("mdl")) {
          mdxfile = mdxfile.replace(".mdl", ".mdx");
        } else if (mdxfile.indexOf(".") > -1) {
          System.out.println("文件名不合法:" + mdxfile);
        } else {
          mdxfile += ".mdx";
        }
        System.out.println(mdxfile);
      }

      try {
        String mdxFullPath = ExcelImageInsert.combineFullPath(mdxfile);

        dataMap.put(unit.getId(), new HashMap<>());
        dataMap.get(unit.getId()).put("mdx", mdxFullPath);
        List<String> blps = getTextures(mdxFullPath);

        blps.stream().forEach(blp -> {
          try {
            System.out.println("开始处理:" + blp);
            String fullPath = ExcelImageInsert.combineFullPath(blp);
            dataMap.get(unit.getId()).put(blp, fullPath);

            System.out.println("贴图全路径:" + fullPath);
          } catch (FileNotFoundException e) {

            String blp2 = blp.substring(1).trim();
            try {
              String fullPath = ExcelImageInsert.combineFullPath(blp2);
              dataMap.get(unit.getId()).put(blp2, fullPath);

              System.out.println("贴图全路径:" + fullPath);
            } catch (FileNotFoundException e1) {
              System.out.println("贴图未找到!名字为:" + blp);
              System.out.println("贴图未找到!名字为:" + blp2);
              List<String> noList = noexistblp.get(unit.getId());
              if (noList == null) {
                noList = new ArrayList<>();
                noexistblp.put(unit.getId(), noList);
              }
              noList.add(blp);
              noList.add(blp2);

              // throw new RuntimeException("贴图未找到@");
            }

          }

        });
      } catch (Exception e) {
        System.out.println("获取贴图异常, mdx=" + mdxfile);
        List<String> noList = noexistblp.get(unit.getId());
        if (noList == null) {
          noList = new ArrayList<>();
          noexistblp.put(unit.getId(), noList);
        }
        noList.add(mdxfile);

        // throw new RuntimeException("获取贴图异常");
      }
    });

    System.out.println(noexistblp);

    dataMap.forEach((unitId, blpMap) -> {
      String mdxf = blpMap.remove("mdx");
      copyfile(mdxf, "test/mdxfiles/mdx-" + unitId + "-" + new File(mdxf).getName());
      blpMap.forEach((blp, blpPath) -> {
        copyfile(blpPath, "test/mdxfiles/" + blp);
      });
    });

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
          if(line.indexOf(".blp")>-1){
            List<String> textures = parseTexture(line, ".blp");
            list.addAll(textures);
          }
          
          if(line.indexOf(".tga")>-1){
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
      list.add(blp);
      i = j;
    }

    return list;
  }

}
