package com.xi7;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MdxRead {
  public static void main(String[] args) throws Exception {

    // List<String> ff = getTextures("test/mdxfiles/mdx-e02Z-[hr]lrv_ms.mdx");
    // if (true)
    // return;

    String assetPath = "D:\\war5-jass\\jass_plugin\\w3x2lni_zhCN_v2.5.2\\w3x2lni_zhCN_v2.5.2\\";
    assetPath += "0x7\\13EAFB2AFF422D4A26AE941286A3ECF0\\";
    ExcelImageInsert.set(assetPath);

    List<Unit> list = new IniRead().read("template/Custom/unit.ini", assetPath + "table/unit.ini", Unit.class);
    Map<String, UnitDetail> unitMap = new UnitParse().parse(list);
    List<UnitDetail> u = unitMap.values().stream().filter(e -> {
      // return e.getType().contains("giant");
      return e.getId().contains("H01F");
      // return !e.getAbilList().contains("AInv");
      // return e.getAbilList().contains("AInv");
    }).collect(Collectors.toList());

    String pathPre = "C:\\Users\\76769\\Desktop\\demo\\html\\javafindjob1.github.io\\x7\\x7-mdx\\";
    // String pathPre = "C:\\Users\\76769\\Desktop\\demo\\test\\mdxfiles\\";
    copyMdx(u, assetPath + "resource\\", pathPre);
  }

  public static void copyMdx(List<UnitDetail> u, String srcPath, String outputPath) {
    Map<String, List<String>> noexistblp = new HashMap<>();
    Map<String, Map<String, String>> dataMap = new HashMap<>();
    List<String> paths = u.stream().map(unit -> {
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
      String path = unit.getId() + " " + mdxfile.replaceAll("\\\\\\\\", "\\\\");
      return path;
    }).collect(Collectors.toList());
    try (
        BufferedWriter br = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("D:\\mdx.txt"), "utf-8"));) {

      br.write(srcPath + "\n");
      br.write(outputPath + "\n");
      for (String path : paths) {
        br.write(path+"\n");
      }
    } catch (Exception e) {

    }
    System.out.println(noexistblp);
  }

}
