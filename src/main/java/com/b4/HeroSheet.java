package com.b4;

import static org.junit.Assert.assertNotNull;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.b4.function.hero.Hero;

public class HeroSheet {
  private XSSFWorkbook workbook;

  public HeroSheet(XSSFWorkbook workbook) {
    this.workbook = workbook;
  }

  public void insert(String sheetName, List<Hero> heroList) throws IOException {
    XSSFSheet sheet = workbook.createSheet(sheetName);

    int ii = 0;
    sheet.setColumnWidth(ii++, 2 * 256); // 单位为1/256个字符宽度
    sheet.setColumnWidth(ii++, 8 * 256); // 单位为1/256个字符宽度
    sheet.setColumnWidth(ii++, 4 * 256); // 单位为1/256个字符宽度
    sheet.setColumnWidth(ii++, 44 * 256); // 单位为1/256个字符宽度
    sheet.setColumnWidth(ii++, 8 * 256); // 单位为1/256个字符宽度
    sheet.setColumnWidth(ii++, 4 * 256); // 单位为1/256个字符宽度
    sheet.setColumnWidth(ii++, 44 * 256); // 单位为1/256个字符宽度
    sheet.setColumnWidth(ii++, 8 * 256); // 单位为1/256个字符宽度
    sheet.setColumnWidth(ii++, 4 * 256); // 单位为1/256个字符宽度
    sheet.setColumnWidth(ii++, 44 * 256); // 单位为1/256个字符宽度
    sheet.setColumnWidth(ii++, 8 * 256); // 单位为1/256个字符宽度
    sheet.setColumnWidth(ii++, 4 * 256); // 单位为1/256个字符宽度
    sheet.setColumnWidth(ii++, 44 * 256); // 单位为1/256个字符宽度
    Row row0 = sheet.createRow(0);
    ii = 0;
    row0.createCell(ii++).setCellValue("");
    row0.createCell(ii++).setCellValue("图标");
    row0.createCell(ii++).setCellValue("CD");
    row0.createCell(ii++).setCellValue("Z/Q");
    row0.createCell(ii++).setCellValue("图标");
    row0.createCell(ii++).setCellValue("CD");
    row0.createCell(ii++).setCellValue("D/W");
    row0.createCell(ii++).setCellValue("图标");
    row0.createCell(ii++).setCellValue("CD");
    row0.createCell(ii++).setCellValue("F/E");
    row0.createCell(ii++).setCellValue("图标");
    row0.createCell(ii++).setCellValue("CD");
    row0.createCell(ii++).setCellValue("G/R");
    sheet.createFreezePane(0, 1, 0, 1);

    // 创建单元格样式并设置背景颜色
    CellStyle cellStyle = workbook.createCellStyle();
    cellStyle.setFillForegroundColor(IndexedColors.BLACK.getIndex()); // 设置黄色背景
    cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND); // 设置填充模式

    // 设置边框颜色
    cellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex()); // 用默认颜色替换
    cellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex()); // 用默认颜色替换
    cellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex()); // 用默认颜色替换
    cellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex()); // 用默认颜色替换

    // // 设置边框样式
    cellStyle.setBorderTop(BorderStyle.THIN);
    cellStyle.setBorderBottom(BorderStyle.THIN);
    cellStyle.setBorderLeft(BorderStyle.THIN);
    cellStyle.setBorderRight(BorderStyle.THIN);
    // 设置水平居中
    cellStyle.setAlignment(HorizontalAlignment.LEFT); // 设置为左对齐

    // 设置垂直居中
    cellStyle.setVerticalAlignment(VerticalAlignment.CENTER); // 设置为垂直居中
    cellStyle.setWrapText(true);

    CellStyle cellStyle2 = workbook.createCellStyle();
    cellStyle2.setFillForegroundColor(IndexedColors.BLACK.getIndex()); // 设置黄色背景
    cellStyle2.setFillPattern(FillPatternType.SOLID_FOREGROUND); // 设置填充模式

    // 设置边框颜色
    cellStyle2.setBottomBorderColor(IndexedColors.BLACK.getIndex()); // 用默认颜色替换
    cellStyle2.setTopBorderColor(IndexedColors.BLACK.getIndex()); // 用默认颜色替换
    cellStyle2.setLeftBorderColor(IndexedColors.BLACK.getIndex()); // 用默认颜色替换
    cellStyle2.setRightBorderColor(IndexedColors.BLACK.getIndex()); // 用默认颜色替换

    // // 设置边框样式
    cellStyle2.setBorderTop(BorderStyle.THIN);
    cellStyle2.setBorderBottom(BorderStyle.THIN);
    cellStyle2.setBorderLeft(BorderStyle.THIN);
    cellStyle2.setBorderRight(BorderStyle.THIN);   
    cellStyle2.setWrapText(true);

    // 设置水平居中
    cellStyle2.setAlignment(HorizontalAlignment.LEFT); // 设置为左对齐
    // 设置垂直居中
    cellStyle2.setVerticalAlignment(VerticalAlignment.TOP); // 设置为垂直居中

    CellStyle cellStyle3 = workbook.createCellStyle();
    cellStyle3.setWrapText(true);
    cellStyle3.setFillForegroundColor(IndexedColors.BLACK.getIndex()); // 设置黄色背景
    cellStyle3.setFillPattern(FillPatternType.SOLID_FOREGROUND); // 设置填充模式

    // 设置边框颜色
    cellStyle3.setBottomBorderColor(IndexedColors.BLACK.getIndex()); // 用默认颜色替换
    cellStyle3.setTopBorderColor(IndexedColors.BLACK.getIndex()); // 用默认颜色替换
    cellStyle3.setLeftBorderColor(IndexedColors.BLACK.getIndex()); // 用默认颜色替换
    cellStyle3.setRightBorderColor(IndexedColors.BLACK.getIndex()); // 用默认颜色替换

    // // 设置边框样式
    cellStyle3.setBorderTop(BorderStyle.THIN);
    cellStyle3.setBorderBottom(BorderStyle.THIN);
    cellStyle3.setBorderLeft(BorderStyle.THIN);
    cellStyle3.setBorderRight(BorderStyle.THIN);
    // 设置水平居中
    cellStyle3.setAlignment(HorizontalAlignment.CENTER); // 设置为左对齐
    cellStyle3.setVerticalAlignment(VerticalAlignment.TOP); // 设置为垂直居中


    int dataI = 1;
    for (Hero hero1 : heroList) {
      {
        String heroName = hero1.getUnit().getPropernames();

        Row row = sheet.createRow(++dataI);
        row.setHeight((short) 1000);

        int index = 0;
        insertDescription(row, 0, cellStyle, workbook, "|cff000000" + hero1.getId());
        insertDescription(row, ++index, cellStyle, workbook, "|cffC0D9D9" + "");
        insertDescription(row, ++index, cellStyle, workbook, "|cffC0D9D9" + "");
        insertDescription(row, ++index, cellStyle, workbook, heroName);
        insertDescription(row, ++index, cellStyle, workbook, "|cffC0D9D9" + "");
        insertDescription(row, ++index, cellStyle, workbook, "|cffC0D9D9" + "");
        insertDescription(row, ++index, cellStyle, workbook, "|cffC0D9D9" + "");
        insertDescription(row, ++index, cellStyle, workbook, "|cffC0D9D9" + "");
        insertDescription(row, ++index, cellStyle, workbook, "|cffC0D9D9" + "");
        insertDescription(row, ++index, cellStyle, workbook, "|cffC0D9D9" + "");
        insertDescription(row, ++index, cellStyle, workbook, "|cffC0D9D9" + "");
        insertDescription(row, ++index, cellStyle, workbook, "|cffC0D9D9" + "");
        insertDescription(row, ++index, cellStyle, workbook, "|cffC0D9D9" + "");
        try {
          ExcelImageInsert.drawingBlp(sheet, hero1.getUnit().getArt(), 1, dataI, 1, 1, 1);
        } catch (Exception e) {
        }
      }

      List<AbilityDetail> list = hero1.getAbilList();
      {
        Row row = sheet.createRow(++dataI);
        row.setHeight((short) 1000);
        int index = 0;
        insertDescription(row, 0, cellStyle, workbook, "|cff000000" + "");
        for (int i = 0; i < 4; i++) {
          AbilityDetail ability = list.get(i);
          if (ability == null) {
            insertDescription(row, ++index, cellStyle, workbook, "|cffC0D9D9" + "");
            insertDescription(row, ++index, cellStyle, workbook, "|cffC0D9D9" + "");
            insertDescription(row, ++index, cellStyle, workbook, "|cffC0D9D9" + "");
          } else {
            insertDescription(row, ++index, cellStyle, workbook, "|cffC0D9D9" + "");
            insertDescription(row, ++index, cellStyle3, workbook, "|cffC0D9D9" + ability.getCool());
            insertDescription(row, ++index, cellStyle, workbook, ability.getName());
            try{
              ExcelImageInsert.drawingBlp(sheet, ability.getArt(), index - 2, dataI, 1, 1, 1);

            }catch(Exception e){
              System.out.println(ability.getArt()+":::" + ability.getId());
            }
          }
        } // 技能完毕
      }
      {
        Row row = sheet.createRow(++dataI);
        row.setHeight((short) 4000);
        int index = 0;
        insertDescription(row, 0, cellStyle, workbook, "|cff000000" + "");
        for (int i = 0; i < 4; i++) {
          AbilityDetail ability = list.get(i);
          sheet.addMergedRegion(new CellRangeAddress(dataI, dataI, index + 1, index + 3)); // 行从0开始，列从0开始
          if (ability == null) {
            insertDescription(row, ++index, cellStyle2, workbook, "|cffC0D9D9" + "");
          } else {
            insertDescription(row, ++index, cellStyle2, workbook, ability.getUbertip());
          }
          index+=2;
        } // 技能完毕
      }

      {
        Row row = sheet.createRow(++dataI);
        row.setHeight((short) 1000);
        int index = 0;
        insertDescription(row, 0, cellStyle, workbook, "|cff000000" + "");
        for (int i = 4; i < 8; i++) {
          AbilityDetail ability = list.get(i);
          if (ability == null) {
            insertDescription(row, ++index, cellStyle, workbook, "|cffC0D9D9" + "");
            insertDescription(row, ++index, cellStyle, workbook, "|cffC0D9D9" + "");
            insertDescription(row, ++index, cellStyle, workbook, "|cffC0D9D9" + "");
          } else {
            insertDescription(row, ++index, cellStyle, workbook, "|cffC0D9D9" + "");
            insertDescription(row, ++index, cellStyle3, workbook, "|cffC0D9D9" + ability.getCool());
            insertDescription(row, ++index, cellStyle, workbook, ability.getName());
            try{
              ExcelImageInsert.drawingBlp(sheet, ability.getArt(), index - 2, dataI, 1, 1, 1);
            }catch(Exception e){
              System.out.println(ability.getArt()+":::" + ability.getId());
            }
          }
        } // 技能完毕
      }
      {
        Row row = sheet.createRow(++dataI);
        row.setHeight((short) 4000);
        int index = 0;
        insertDescription(row, 0, cellStyle, workbook, "|cff000000" + "");
        for (int i = 4; i < 8; i++) {
          sheet.addMergedRegion(new CellRangeAddress(dataI, dataI, index + 1, index + 3)); // 行从0开始，列从0开始
          AbilityDetail ability = list.get(i);
          if (ability == null) {
            insertDescription(row, ++index, cellStyle2, workbook, "|cffC0D9D9" + "");
          } else {
            insertDescription(row, ++index, cellStyle2, workbook, ability.getUbertip());
          }
          index+=2;
        } // 技能完毕
      }

      // 末尾加一个空行
      ++dataI;
    }

  }

  public void writeTo(String fileName) throws FileNotFoundException, IOException {
    // 写入文件
    try (FileOutputStream fileOut = new FileOutputStream(fileName)) {
      workbook.write(fileOut);
    }
  }

  public void insertDescription(Row row, int column, CellStyle cellStyle, XSSFWorkbook workbook, String description)
      throws IOException {
    Cell cell = row.createCell(column);
    cell.setCellStyle(cellStyle);
    // 创建富文本字符串
    XSSFRichTextString richText = new XSSFRichTextString();
    List<String[]> fieldList = parseText(description);

    for (int i = 0; i < fieldList.size(); i++) {
      String[] arr = fieldList.get(i);
      String hexColorStr = arr[0].replace("|cff", "#");
      appendRichText(workbook, richText, (short) 12, hexColorStr, arr[1]);
    }

    // 计算行高
    {
      // 每行27个字
      int LINE_NUM = 27;
      int count = 0;
      StringBuilder buf = new StringBuilder();
      for (String[] arr : fieldList) {
        buf.append(arr[1]);
      }

      String[] split = buf.toString().split("\\n");
      for (String line : split) {
        count += line.length() / LINE_NUM;
        count++;
      }
      if (count > 11) {
        row.setHeight((short) ((count - 11) * 400 + 4100));
      }
    }

    // 设置单元格的内容
    cell.setCellValue(richText);
  }

  private void appendRichText(XSSFWorkbook workbook, XSSFRichTextString richText, short fontSize, String hexColorStr,
      String buf) {
    if (hexColorStr == null) {
      hexColorStr = "#cccccc";
    }
    if (fontSize == 0) {
      fontSize = 12;
    }
    XSSFFont font = workbook.createFont();
    font.setFontName("Microsoft YaHei");
    // 设置字体颜色为16进制颜色
    XSSFColor hexColor = new XSSFColor(java.awt.Color.decode(hexColorStr), null);
    font.setColor(hexColor);
    font.setFontHeightInPoints(fontSize);

    richText.append(buf, font);
  }

  public static void main(String[] args) throws IOException {

    // List<String[]> text =
    // parseText("|cffff9900当前照片：|r无|cff99ccff|n|r|cffff9900已拍摄女性：|r0|cff99cc00|n|r|cffff99cc类型：饰品|n|r|cff99cc00|n|r似乎是用拉索姆科技材料打造的照相机，可以将眼前景象转换为图片。随着拍摄内容增加，还能随机提升属性。|n");
    List<String[]> text = parseText("|cffffcc00|r|n|cffff99cc应急治疗药水|r × 1|n|cffff0000小恶魔的魔法书|r × 1|n|n|cffffcc00");

    text.stream().forEach(e -> System.out.println(e[0] + " --- " + e[1]));
  }

  public static List<String[]> parseText(String description) {
    // String description =
    // "|cffccffcc锻造材料|r|cffff9900|n|r|cffffcc00天心法袍|n能量宝石|n幻森之羽 x
    // 10|n|r|cff99ccff|n|n护甲+120|n生命上限+5000|n智力+200|n降低英雄技能6%冷却时间|n被暗属性敌人攻击时，反弹智力x10的伤害|n|r|cff999999东正主教专属：|n智力+175|r|cff99ccff|n|r|cff99cc00等级：B+|n|r|cffff9900类型：衣服|n|n|r索多曼尼斯教堂大主教的华丽礼服，在光辉的外衣下藏着奇异的符文。";
    List<String[]> list = new ArrayList<>();
    if (description == null || description.trim().length() == 0) {
      return list;
    }

    {
      Pattern p = Pattern.compile("\\|\\w{8}");
      Matcher matcher = p.matcher(description);
      while(matcher.find()){
        String group = matcher.group();
        description = description.replace(group, group.toLowerCase());
      }
    }

    {
      Pattern singleR = Pattern.compile("\\|r(?!\\|)");
      Matcher matcher = singleR.matcher(description);
      while (matcher.find()) {
        String group = matcher.group();
        description = description.replace(group, "|cffffffff");
      }

    }

    String[] lines = description.split("\\|n");
    String color = "|cffd6d5b7";
    for (String line : lines) {

      if (!line.startsWith("|cff")) {
        line = color + line;
      }
      if (!line.substring(line.length() - 10).startsWith("|cff")) {
        line += color;
      }

      Pattern singleR = Pattern.compile("(\\|cff\\w{6})(.*?)(?=\\|cff\\w{6})");
      Matcher matcher = singleR.matcher(line);

      while (matcher.find()) {
        color = matcher.group(1);
        String text = matcher.group(2);
        if (text == null || text.trim().length() == 0) {
          continue;
        }
        list.add(new String[] { color, text });
      }
      if (list.size() > 0) {
        list.get(list.size() - 1)[1] += "\n";
      }
    }

    if (list.size() > 0) {
      String lastLine = list.get(list.size() - 1)[1];
      list.get(list.size() - 1)[1] = lastLine.substring(0, lastLine.length() - 1);
    }
    return list;
  }

}
