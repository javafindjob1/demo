package com.common.parse;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;

public class AbstractSheetTest {

  AbstractSheet abstractSheet;

  @Before
  public void before() {
    XSSFWorkbook workbook = new XSSFWorkbook();

    abstractSheet = new AbstractSheet(workbook) {
    };
  }

  @Test
  public void appendRichTextTest() throws FileNotFoundException, IOException {

    XSSFSheet sheet = abstractSheet.workbook.createSheet("test");
    int rowNumer = -1;
    {
      XSSFRow row = sheet.createRow(++rowNumer);
      XSSFCell cell = row.createCell(0);
      XSSFRichTextString richTextString = abstractSheet.createRichTextString(10, "#FF0000", "你好" + rowNumer);
      cell.setCellValue(richTextString);
    }
    {
      XSSFRow row = sheet.createRow(++rowNumer);
      XSSFCell cell = row.createCell(0);
      XSSFRichTextString richTextString = abstractSheet.createRichTextString(10, "#FF0000", "你好" + rowNumer);
      richTextString.append("append");
      cell.setCellValue(richTextString);
    }

    {
      XSSFRow row = sheet.createRow(++rowNumer);
      XSSFCell cell = row.createCell(0);
      XSSFRichTextString richTextString = abstractSheet.createRichTextString(10, "#FF0000", "你好" + rowNumer);
      richTextString.setString("setString");
      cell.setCellValue(richTextString);
    }
    {
      XSSFRow row = sheet.createRow(++rowNumer);
      XSSFCell cell = row.createCell(0);
      XSSFRichTextString richTextString = abstractSheet.createRichTextString(10, "#FF0000", "你好" + rowNumer);
      abstractSheet.appendRichText(richTextString, 20, "#140101ff", "你好");
      cell.setCellValue(richTextString);

      XSSFCell cell1 = row.createCell(1);
      richTextString.setString(null);
      cell1.setCellValue(richTextString);

    }
    {
      XSSFRow row = sheet.createRow(++rowNumer);
      XSSFCell cell = row.createCell(0);
      XSSFRichTextString richTextString = abstractSheet.createRichTextString(10, "#FF0000", "你好" + rowNumer);
      abstractSheet.appendRichText(richTextString, 20, "#140101ff", "你好");
      cell.setCellValue(richTextString);

      XSSFCell cell1 = row.createCell(1);
      richTextString.setString(null);
      abstractSheet.appendRichText(richTextString, 20, "#140101ff", "你好");
      cell1.setCellValue(richTextString);

    }
    abstractSheet.writeTo("test.xlsx");
  }
}
