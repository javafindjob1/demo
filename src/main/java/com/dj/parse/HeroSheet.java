package com.xi42.parse;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.common.parse.AbstractSheet;
import com.xi42.function.Hero;

public class HeroSheet extends AbstractSheet {
  public HeroSheet(XSSFWorkbook workbook) {
    this.workbook = workbook;
  }

  public void insert(String sheetName, Map<String, Hero[]> heroList) throws IOException {
    XSSFSheet sheet = workbook.createSheet(sheetName);

    int ii = 0;
    sheet.setColumnWidth(ii++, 2 * 256); // 单位为1/256个字符宽度
    sheet.setColumnWidth(ii++, 6 * 256); // 单位为1/256个字符宽度
    sheet.setColumnWidth(ii++, 60 * 256); // 单位为1/256个字符宽度
    sheet.setColumnWidth(ii++, 20 * 256); // 单位为1/256个字符宽度
    sheet.setColumnWidth(ii++, 9 * 256); // 单位为1/256个字符宽度
    sheet.setColumnWidth(ii++, 60 * 256); // 单位为1/256个字符宽度
    sheet.setColumnWidth(ii++, 40 * 256); // 单位为1/256个字符宽度
    Row row0 = sheet.createRow(0);
    ii = 0;
    row0.createCell(ii++).setCellValue("");
    row0.createCell(ii++).setCellValue("名称");
    row0.createCell(ii++).setCellValue("详情");
    row0.createCell(ii++).setCellValue("获取方式");
    row0.createCell(ii++).setCellValue("名称");
    row0.createCell(ii++).setCellValue("详情/合成公式");
    row0.createCell(ii++).setCellValue("备注");
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
    cellStyle2.setBottomBorderColor(IndexedColors.YELLOW.getIndex()); // 用默认颜色替换
    cellStyle2.setTopBorderColor(IndexedColors.YELLOW.getIndex()); // 用默认颜色替换
    cellStyle2.setLeftBorderColor(IndexedColors.BLACK.getIndex()); // 用默认颜色替换
    cellStyle2.setRightBorderColor(IndexedColors.BLACK.getIndex()); // 用默认颜色替换

    // // 设置边框样式
    cellStyle2.setBorderTop(BorderStyle.THIN);
    cellStyle2.setBorderBottom(BorderStyle.THIN);
    cellStyle2.setBorderLeft(BorderStyle.THIN);
    cellStyle2.setBorderRight(BorderStyle.THIN);
    // 设置水平居中
    cellStyle2.setAlignment(HorizontalAlignment.LEFT); // 设置为左对齐

    // 设置垂直居中
    cellStyle2.setVerticalAlignment(VerticalAlignment.CENTER); // 设置为垂直居中
    int dataI = 1;
    for (Hero[] heros : heroList.values()) {
      Hero hero1 = heros[0];
      Hero hero2 = heros[1];
      {
        String heroName = hero1.getUnit().getPropernames();

        Row row = sheet.createRow(++dataI);

        row.setHeight((short) 1000);

        insertDescription(row, 0, cellStyle, workbook, "|cff000000" + " ");
        insertDescription(row, 1, cellStyle, workbook, "|cffC0D9D9" + " ");
        insertDescription(row, 2, cellStyle, workbook, heroName);
        if (hero2 == null) {
          insertDescription(row, 3, cellStyle, workbook, "|cffC0D9D9" + " ");
          insertDescription(row, 4, cellStyle, workbook, "|cffC0D9D9" + " ");
          insertDescription(row, 5, cellStyle, workbook, "|cffC0D9D9" + " ");
          insertDescription(row, 6, cellStyle, workbook, "|cffC0D9D9" + " ");
          ExcelImageInsert.drawingBlp(sheet, hero1.getUnit().getArt(), 0, dataI, 2, 1, 1);
        } else {
          heroName = hero2.getUnit().getPropernames();
          insertDescription(row, 3, cellStyle, workbook, "|cffC0D9D9" + " ");
          insertDescription(row, 4, cellStyle, workbook, "|cffC0D9D9" + " ");
          insertDescription(row, 5, cellStyle, workbook, heroName);
          insertDescription(row, 6, cellStyle, workbook, "|cffC0D9D9" + " ");
          ExcelImageInsert.drawingBlp(sheet, hero1.getUnit().getArt(), 0, dataI, 2, 1, 1);
          ExcelImageInsert.drawingBlp(sheet, hero2.getUnit().getArt(), 4, dataI, 1, 1, 1);
        }
      }

      List<AbilityDetail> list = hero1.getAbilList();
      List<AbilityDetail> list2 = null;
      if (hero2 != null) {
        list2 = hero2.getAbilList();
      }

      for (int i = 0; i < list.size(); i++) {

        AbilityDetail ability = list.get(i);
        if (ability == null) {
          assertNotNull("技能不能为空" + hero1.getId(), ability);
        }
        try {
          if (ability.getName().contains("简介")) {
            Row row = sheet.createRow(++dataI);
            sheet.addMergedRegion(new CellRangeAddress(dataI, dataI, 2, 3)); // 行从0开始，列从0开始
            row.setHeight((short) 2100);

            insertDescription(row, 0, cellStyle, workbook, "|cff000000" + ability.getId());
            insertDescription(row, 1, cellStyle, workbook, ability.getName());
            insertDescription(row, 2, cellStyle, workbook, ability.getUbertip());

            if (list2 != null) {
              // 显示增强的内容
              AbilityDetail ability2 = list2.get(i);
              insertDescription(row, 4, cellStyle, workbook, "");
              insertDescription(row, 5, cellStyle, workbook, ability2.getUbertip());
            } else {
              insertDescription(row, 4, cellStyle, workbook, "");
              insertDescription(row, 5, cellStyle, workbook, "");
            }
            insertDescription(row, 6, cellStyle, workbook, "");

            row = sheet.createRow(++dataI);
            row.setHeight((short) 1000);
            insertDescription(row, 0, cellStyle, workbook, "");
            insertDescription(row, 1, cellStyle, workbook, "主要属性");
            insertDescription(row, 2, cellStyle, workbook, hero1.getMainPropDesc());
            insertDescription(row, 3, cellStyle, workbook, "");
            insertDescription(row, 4, cellStyle, workbook, "");
            insertDescription(row, 5, cellStyle, workbook, "");
            insertDescription(row, 6, cellStyle, workbook, "|cffC0D9D9" + " ");
          } else if (ability.getName().contains("剧情")) {
            Row row = sheet.createRow(++dataI);

            // 存一下英雄在工作表中的位置,供更新记录表使用
            if (hero1.getRowNum() == 0) {
              // 防止其他sheet页出现重复后，覆盖
              hero1.setSheetName(sheetName);
              hero1.setRowNum(dataI + 1);
            }

            sheet.addMergedRegion(new CellRangeAddress(dataI, dataI, 2, 5)); // 行从0开始，列从0开始
            row.setHeight((short) 1500);
            insertDescription(row, 0, cellStyle, workbook, "");
            insertDescription(row, 1, cellStyle, workbook, "专属");
            insertDescription(row, 2, cellStyle, workbook, 10, ability.getUbertip());
            insertDescription(row, 6, cellStyle, workbook, "|cffC0D9D9" + " ");
          } else {
            Row row = sheet.createRow(++dataI);
            row.setHeight((short) 4100);
            insertDescription(row, 0, cellStyle, workbook, "|cff000000" + ability.getId());
            insertDescription(row, 1, cellStyle, workbook,
                (ability.getHotkey() == null ? "" : ability.getHotkey() + "|n") + ability.getName());
            insertDescription(row, 2, cellStyle, workbook, ability.getUbertip());

            if (list2 == null) {
              insertDescription(row, 3, cellStyle, workbook, "");
              insertDescription(row, 4, cellStyle, workbook, "");
              insertDescription(row, 5, cellStyle, workbook, "");
              insertDescription(row, 6, cellStyle, workbook,
                  "|cffC0D9D9" + ability.getMark() == null ? "" : ability.getMark());
              ExcelImageInsert.drawingBlp(sheet, ability.getArt(), 0, dataI, 2, 1, 1);
            } else {
              AbilityDetail ability2 = list2.get(i);
              insertDescription(row, 3, cellStyle, workbook, "|cff000000" + ability2.getId());
              insertDescription(row, 4, cellStyle, workbook,
                  (ability2.getHotkey() == null ? "" : ability2.getHotkey() + "|n") + ability2.getName());
              insertDescription(row, 5, cellStyle, workbook, ability2.getUbertip());
              insertDescription(row, 6, cellStyle, workbook,
                  "|cffC0D9D9" + ability.getMark() == null ? "" : ability.getMark());
              ExcelImageInsert.drawingBlp(sheet, ability.getArt(), 0, dataI, 2, 1, 1);
              ExcelImageInsert.drawingBlp(sheet, ability2.getArt(), 4, dataI, 1, 1, 1);
            }
          }

        } catch (Exception e2) {
          e2.printStackTrace();
          System.out.println("error:" + ability.getName() + hero1.getId());
        }
      } // 技能完毕

      List<ItemDetail> itemList = hero1.getItemList();
      if (itemList.size() > 0) {
        Row row;

        for (ItemDetail item : itemList) {
          row = sheet.createRow(++dataI);
          row.setHeight((short) 4100);
          sheet.addMergedRegion(new CellRangeAddress(dataI, dataI, 3, 4)); // 行从0开始，列从0开始

          try {
            int col = 0;
            insertDescription(row, col++, cellStyle, workbook, "|cff000000" + " ");
            insertDescription(row, col++, cellStyle, workbook, "|cffcc99ff" + item.getName());
            insertDescription(row, col++, cellStyle, workbook, item.getDescription());
            insertDescription(row, col++, cellStyle, workbook, 11,
                "|cff99cc00" + (item.getDropPlace() == null ? "" : item.getDropPlace()));
            insertDescription(row, col++, cellStyle, workbook, "|cff99cc00" + "");
            insertDescription(row, col++, cellStyle, workbook, 11,
                "|cff99cc00" + (item.getSynthesisFormula() == null ? "" : item.getSynthesisFormula()));
            insertDescription(row, col++, cellStyle, workbook, 11,
                "|cfffdd017" + (item.getMark() == null ? "" : item.getMark()));
            ExcelImageInsert.drawingBlp(sheet, item.getIcon(), 0, dataI, 2, 1, 1);

          } catch (Exception e) {
            e.printStackTrace();
            System.out.println("error:" + item.getName());
          }
        }

      }

      ++dataI;

    }

  }

  public static void main(String[] args) throws IOException {

    // List<String[]> text =
    // parseText("|cffff9900当前照片：|r无|cff99ccff|n|r|cffff9900已拍摄女性：|r0|cff99cc00|n|r|cffff99cc类型：饰品|n|r|cff99cc00|n|r似乎是用拉索姆科技材料打造的照相机，可以将眼前景象转换为图片。随着拍摄内容增加，还能随机提升属性。|n");
    List<String[]> text = parseText("|cffffcc00|r|n|cffff99cc应急治疗药水|r × 1|n|cffff0000小恶魔的魔法书|r × 1|n|n|cffffcc00");

    text.stream().forEach(e -> System.out.println(e[0] + " --- " + e[1]));
  }

}
