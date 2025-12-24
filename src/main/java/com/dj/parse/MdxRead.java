package com.xi42.parse;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.xi42.Client;

import com.common.ini.IniRead;
import com.common.ini.Unit;

public class MdxRead {
  public static void main(String[] args) throws Exception {

    // List<String> ff = getTextures("test/mdxfiles/mdx-e02Z-[hr]lrv_ms.mdx");

    String assetPath = "D:\\war5-jass\\jass_plugin\\w3x2lni_zhCN_v2.5.2\\w3x2lni_zhCN_v2.5.2\\";
    assetPath += "0mp\\4FFD4CA60115240BEFBD7D6278E38E2F\\";
    ExcelImageInsert.set(assetPath, Client.class);

    // parseMdx("ss",
    // "Abilities\\Spells\\Items\\StaffOfPurification\\PurificationCaster.mdl");
    // if (true)
    // return;

    List<Unit> list = IniRead.read("template/Custom/unit.ini", assetPath + "table\\unit.ini", Unit.class);
    Map<String, UnitDetail> unitMap = new UnitParse().parse(list);
    List<UnitDetail> u = unitMap.values().stream().filter(e -> {
      // return e.getType().contains("giant");
      return e.getId().contains("E011");
      // return !e.getAbilList().contains("AInv");
      // return e.getAbilList().contains("AInv");
    }).collect(Collectors.toList());

  
    String pathPre = "html\\javafindjob1.github.io\\mp\\mp-mdx\\";
    // String pathPre = "C:\\Users\\76769\\Desktop\\demo\\test\\mdxfiles\\";
    generateCopyMdxTask(u, assetPath + "resource\\", pathPre);
  }

  public static void generateCopyMdxTask(List<UnitDetail> heroList, String srcPath, String outputPath) {
    Map<String, List<String>> noexistblp = new HashMap<>();
    Map<String, Map<String, String>> dataMap = new HashMap<>();
    List<String> paths = heroList.stream().map(unit -> {
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
