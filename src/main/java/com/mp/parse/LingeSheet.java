package com.mp.parse;

import static org.junit.Assert.assertNotNull;

import java.util.*;
import java.util.Map.Entry;
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
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.xi7.MapUtil;
import com.common.ini.IniRead;
import com.common.parse.AbstractSheet;
import com.mp.Client;

public class LingeSheet extends AbstractSheet {
  private XSSFCreationHelper creationHelper;
  private Map<String, AbilityDetail> abiMap;

  public LingeSheet(XSSFWorkbook workbook, Map<String, AbilityDetail> abiMap) {
    this.workbook = workbook;
    this.creationHelper = workbook.getCreationHelper();
    this.abiMap = abiMap;
  }

  public void insert(String sheetName) throws Exception {

    Map<String, List<AbilityDetail>> lingeAbiMap = new LinkedHashMap<>();
    Map<String, Map<String, String>> lingeMap = IniRead.read2(Client.class.getResourceAsStream("custom/灵格.ini"));
    for (Entry<String, Map<String, String>> entry : lingeMap.entrySet()) {
      String subKey = entry.getKey();
      Map<String, String> data = entry.getValue();
      List<String> subList = new ArrayList<>(data.keySet());
      List<AbilityDetail> subAbiList = MapUtil.getNotNull(lingeAbiMap, subKey.split("-")[1], ArrayList::new);
      for (String abiId : subList) {
        AbilityDetail abi = abiMap.get(abiId);
        if (abi != null) {
          subAbiList.add(abi);
        } else {
          System.out.println("灵格未找到对应技能 " + abiId);
        }
      }
    }

    XSSFSheet sheet = workbook.createSheet(sheetName);

    int colIndex = -1;
    {
      sheet.setColumnWidth(++colIndex, 2 * 256 + 60); // 单位为1/256个字符宽度
      sheet.setColumnWidth(++colIndex, 6 * 256); // 单位为1/256个字符宽度
      sheet.setColumnWidth(++colIndex, 20 * 256); // 单位为1/256个字符宽度
      sheet.setColumnWidth(++colIndex, 60 * 256); // 单位为1/256个字符宽度
      sheet.setColumnWidth(++colIndex, 40 * 256); // 单位为1/256个字符宽度
    }
    final int LAST_COL = colIndex;
    int dataI = -1;
    {
      {
        Row row = sheet.createRow(++dataI);
        sheet.addMergedRegion(new CellRangeAddress(dataI, dataI, 0, LAST_COL)); // 行从0开始，列从0开始
        {
          Cell cell = row.createCell(0);
          XSSFRichTextString richText = createRichTextString((short) 11, "#f70808",
              "疾风暴击倍率就是当你携带装备带有疾风暴击的物品时会使其暴击伤害增高(红字)");
          cell.setCellValue(richText);
        }
      }
      {
        Row row = sheet.createRow(++dataI);
        sheet.addMergedRegion(new CellRangeAddress(dataI, dataI, 0, LAST_COL)); // 行从0开始，列从0开始
        {
          Cell cell = row.createCell(0);
          XSSFRichTextString richText = createRichTextString((short) 11, "#f70808",
              "护甲穿透就是在原本的物理伤害基础上增加百分比的伤害，多个按照物理增伤相加计算");
          cell.setCellValue(richText);
        }
      }

      {
        Row row = sheet.createRow(++dataI);
        sheet.addMergedRegion(new CellRangeAddress(dataI, dataI, 0, LAST_COL)); // 行从0开始，列从0开始
        {
          Cell cell = row.createCell(0);
          XSSFRichTextString richText = createRichTextString((short) 11, "#f70808",
              "法术穿透就是在原本的法术伤害基础上增加百分比的伤害，多个按照法术增伤相加计算");
          cell.setCellValue(richText);
        }
      }

      {
        Row row = sheet.createRow(++dataI);
        sheet.addMergedRegion(new CellRangeAddress(dataI, dataI, 0, LAST_COL)); // 行从0开始，列从0开始
        {
          Cell cell = row.createCell(0);
          XSSFRichTextString richText = createRichTextString((short) 11, "#f70808", "属性强化同时包括护甲穿透和法术穿透");
          cell.setCellValue(richText);
        }
      }

    }

    {
      colIndex = -1;
      Row row = sheet.createRow(++dataI);
      row.createCell(++colIndex).setCellValue("ID");
      row.createCell(++colIndex).setCellValue("品质");
      row.createCell(++colIndex).setCellValue("名称");
      row.createCell(++colIndex).setCellValue("效果");
      row.createCell(++colIndex).setCellValue("备注");
    }

    // 参数分别是冻结的列数、冻结的行数、冻结前的列数、冻结前的行数
    sheet.createFreezePane(0, 5, 0, 5);

    // 创建单元格样式并设置背景颜色
    CellStyle cellStyleCenter = workbook.createCellStyle();
    cellStyleCenter.setFillForegroundColor(IndexedColors.BLACK.getIndex()); // 设置黄色背景
    cellStyleCenter.setFillPattern(FillPatternType.SOLID_FOREGROUND); // 设置填充模式

    // 设置边框颜色
    cellStyleCenter.setLeftBorderColor(IndexedColors.BLACK.getIndex()); // 用默认颜色替换
    cellStyleCenter.setRightBorderColor(IndexedColors.BLACK.getIndex()); // 用默认颜色替换
    cellStyleCenter.setBottomBorderColor(IndexedColors.YELLOW.getIndex()); // 用默认颜色替换
    cellStyleCenter.setTopBorderColor(IndexedColors.YELLOW.getIndex()); // 用默认颜色替换

    // // 设置边框样式
    cellStyleCenter.setBorderLeft(BorderStyle.THIN);
    cellStyleCenter.setBorderRight(BorderStyle.THIN);
    cellStyleCenter.setBorderTop(BorderStyle.THIN);
    cellStyleCenter.setBorderBottom(BorderStyle.THIN);
    // 设置水平居中
    cellStyleCenter.setAlignment(HorizontalAlignment.LEFT); // 设置为左对齐

    // 设置垂直居中
    cellStyleCenter.setVerticalAlignment(VerticalAlignment.CENTER); // 设置为垂直居中
    cellStyleCenter.setWrapText(true);

    Pattern colorPat = Pattern.compile("^.*(\\|\\w{9}).*$");
    for (Entry<String, List<AbilityDetail>> entry : lingeAbiMap.entrySet()) {
      String level = entry.getKey();
      List<AbilityDetail> abiList = entry.getValue();
      for (AbilityDetail abi : abiList) {

        Row row = sheet.createRow(++dataI);
        row.setHeight((short) 2400);
        try {
          int col = -1;
          String colorName = "";
          Matcher macher = colorPat.matcher(abi.getName());
          if (macher.find()) {
            colorName = macher.group(1);
          }
          insertDescription(row, ++col, cellStyleCenter, workbook, "|cff000000" + abi.getId());
          insertDescription(row, ++col, cellStyleCenter, workbook, colorName + level);
          insertDescription(row, ++col, cellStyleCenter, workbook, abi.getName());
          insertDescription(row, ++col, cellStyleCenter, workbook, abi.getUbertip());
          insertDescription(row, ++col, cellStyleCenter, workbook, "");

          ExcelImageInsert.drawingBlp(sheet, abi.getArt(), 0, dataI, 2, 1, 1);

        } catch (Exception e) {
          System.out.println();
        }
      }
    }

  }
}
