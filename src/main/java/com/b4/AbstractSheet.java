package com.b4;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public abstract class AbstractSheet {

  protected XSSFWorkbook workbook;

  protected short newHeight;

  public void writeTo(String fileName) throws FileNotFoundException, IOException {
    // 写入文件
    try (FileOutputStream fileOut = new FileOutputStream(fileName)) {
      workbook.write(fileOut);
    }
  }

  public void insertDescription(Row row, int column, CellStyle cellStyle, XSSFWorkbook workbook, String description)
      throws IOException {
    insertDescription(row, column, cellStyle, workbook, (short) 12, description);
  }

  public void insertDescription(Row row, int column, CellStyle cellStyle, XSSFWorkbook workbook, short fontsize,
      String description)
      throws IOException {
    Cell cell = row.createCell(column);
    // row.setHeight((short) 5100);
    cell.setCellStyle(cellStyle);

    // 创建富文本字符串
    XSSFRichTextString richText = new XSSFRichTextString();
    List<String[]> fieldList = parseText(description);
    for (int i = 0; i < fieldList.size(); i++) {
      String[] arr = fieldList.get(i);
      String hexColorStr = arr[0].replace("|cff", "#");
      appendRichText(workbook, richText, fontsize, hexColorStr, arr[1]);
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
        newHeight = (short) ((count - 11) * 400 + 4100);
      }
    }

    // 设置单元格的内容
    cell.setCellValue(richText);
  }

  protected void appendRichText(XSSFWorkbook workbook, XSSFRichTextString richText, short fontSize, String hexColorStr,
      String buf) {
    appendRichText(workbook, richText, buf, fontSize, hexColorStr, XSSFFont.U_NONE);
  }

  protected void appendRichText(XSSFWorkbook workbook, XSSFRichTextString richText, String buf, short fontSize,
      String hexColorStr, byte underline) {
    if (hexColorStr == null) {
      hexColorStr = "#FFFFFF";
    }
    if (fontSize == 0) {
      fontSize = 12;
    }
    XSSFFont font = workbook.createFont();
    font.setUnderline(underline);
    font.setFontName("Microsoft YaHei");
    // 设置字体颜色为16进制颜色
    XSSFColor hexColor = new XSSFColor(java.awt.Color.decode(hexColorStr), null);
    font.setColor(hexColor);
    font.setFontHeightInPoints(fontSize);

    richText.append(buf, font);
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
      //匹配|r|c 把|r去掉
      Pattern singleR = Pattern.compile("\\|r(?=\\|c)");
      Matcher matcher = singleR.matcher(description);
      while (matcher.find()) {
        String group = matcher.group();
        description = description.replace(group, "");
      }
      System.out.println(description);
    }
    {
      //匹配孤单的|r 把|r去掉
      Pattern singleR = Pattern.compile("\\|r(?!\\|c)");
      Matcher matcher = singleR.matcher(description);
      while (matcher.find()) {
        String group = matcher.group();
        description = description.replace(group, "|cffffffff");
      }
      System.out.println(description);

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
