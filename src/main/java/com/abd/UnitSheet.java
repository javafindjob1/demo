package com.abd;

import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

import com.abd.sqlite.SqLiteJDBC;

import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UnitSheet extends AbstractSheet{
    private XSSFCreationHelper creationHelper;
    private Map<String,ItemDetail> idItemMap;
    
    public UnitSheet(XSSFWorkbook workbook, Map<String,ItemDetail> idItemMap) {
        this.workbook = workbook;
        this.creationHelper = workbook.getCreationHelper();
        this.idItemMap = idItemMap;
    }

    public void insertHead(String sheetName) throws IOException {
        XSSFSheet sheet = workbook.createSheet(sheetName);
        workbook.setSheetOrder(sheetName, 0);
        sheet.setColumnWidth(1, 60 * 256); // 单位为1/256个字符宽度

        int dataI = -1;
        {
            final int LAST_COL = 19;
            short height = (short) (sheet.getDefaultRowHeight() * 3);
            {
                Row row = sheet.createRow(++dataI);
                row.setHeight(height);
                sheet.addMergedRegion(new CellRangeAddress(dataI, dataI, 0, LAST_COL)); // 行从0开始，列从0开始
                Cell cell = row.createCell(0);
                XSSFRichTextString richText = new XSSFRichTextString();
                appendRichText(workbook, richText, (short) 30, "#FF0000",
                        "此文档特别鸣谢：");
                cell.setCellValue(richText);
            }

            {
                Row row = sheet.createRow(++dataI);
                row.setHeight(height);
                sheet.addMergedRegion(new CellRangeAddress(dataI, dataI, 0, LAST_COL)); // 行从0开始，列从0开始
                Cell cell = row.createCell(0);
                XSSFRichTextString richText = new XSSFRichTextString();
                appendRichText(workbook, richText, (short) 30, "#FF0000",
                        "高音王子（装备剪辑，贴图，文本介绍）");
                cell.setCellValue(richText);
            }

            {
                Row row = sheet.createRow(++dataI);
                row.setHeight(height);
                sheet.addMergedRegion(new CellRangeAddress(dataI, dataI, 0, LAST_COL)); // 行从0开始，列从0开始
                Cell cell = row.createCell(0);
                XSSFRichTextString richText = new XSSFRichTextString();
                appendRichText(workbook, richText, (short) 30, "#FF0000",
                        "玉露、TFBOYS-王俊凯（备注，点评）");
                cell.setCellValue(richText);
            } 
            {
                Row row = sheet.createRow(++dataI);
                row.setHeight(height);
                sheet.addMergedRegion(new CellRangeAddress(dataI, dataI, 0, LAST_COL)); // 行从0开始，列从0开始
                Cell cell = row.createCell(0);
                XSSFRichTextString richText = new XSSFRichTextString();
                appendRichText(workbook, richText, (short) 30, "#FF0000",
                        "热心的梦群群友");
                cell.setCellValue(richText);
            }
        }

        {
            // 创建一个新的行
            Row row = sheet.createRow(++dataI);
            // 创建一个新的单元格
            Cell cell = row.createCell(1);
            // 设置单元格值为超链接
            XSSFRichTextString richText = new XSSFRichTextString();
            appendRichText(workbook, richText, (short) 10, "#000000",
            "【腾讯文档】梦想远景装备介绍_目录");
            cell.setCellValue(richText);
        }
        {
            // 创建一个新的行
            Row row = sheet.createRow(++dataI);
            // 创建一个新的单元格
            Cell cell = row.createCell(1);

            // 创建超链接
            Hyperlink link = creationHelper.createHyperlink(HyperlinkType.URL);
            link.setAddress("https://docs.qq.com/sheet/DZHlQcWxVVk1JV2lu");
            cell.setHyperlink(link);

            // 设置单元格内容
            XSSFRichTextString richText = new XSSFRichTextString();
            appendRichText(workbook, richText,
                    "返回目录", (short) 10, "#0000FF", XSSFFont.U_SINGLE);
            cell.setCellValue(richText);
        }

        {
            // 创建一个新的行
            Row row = sheet.createRow(8);
            // 创建一个新的单元格
            Cell cell = row.createCell(1);

            XSSFRichTextString richText = new XSSFRichTextString();
            appendRichText(workbook, richText, (short) 14, "#FF0000",
                    "近期更新记录（具体内容在下方标签页查看）：");
            cell.setCellValue(richText);

            try (SqLiteJDBC db = new SqLiteJDBC()) {
                // 连接SQLite数据库，数据库文件是test.db，如果文件不存在，会自动创建
                AtomicInteger dataIn = new AtomicInteger(8);
                updateInfo(sheet, dataIn, "新增：", db.queryAddDiffSql());
                updateInfo(sheet, dataIn, "删除：", db.queryDelDiffSql());
                updateInfo(sheet, dataIn, "修改：", db.queryPropDiffSql());
                updateInfo(sheet, dataIn, "物品获取方式变化：", db.queryDropPlaceDiffSql());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    private void updateInfo(XSSFSheet sheet, AtomicInteger dataI, String message, List<ItemDetail> list) {
        for (ItemDetail item : list) {
            // 新增S+武器(龙翼)
            Row row = sheet.createRow(dataI.incrementAndGet());
            // 创建一个新的单元格
            Cell cell = row.createCell(1);
            
            // 设置单元格值为超链接
            Hyperlink link = creationHelper.createHyperlink(HyperlinkType.DOCUMENT);
            link.setAddress("'"+item.getType()+"'!B" + idItemMap.get(item.getId()).getRowNum());
            cell.setHyperlink(link);

            XSSFRichTextString richText = new XSSFRichTextString();
            StringBuffer buf = new StringBuffer();
            buf.append(message).append("[").append(item.getLevel()).append(" ").append(item.getType()).append("]").append(item.getName());
            if (item.getHero().length() > 0) {
                buf.append("(").append(item.getHero()).append(")");
            }
            appendRichText(workbook, richText, (short) 11, "#000000",
                    buf.toString());
            cell.setCellValue(richText);
        }
    }
    public void insert(String sheetName, Map<String, List<UnitDetail>> mapList) throws IOException {
        XSSFSheet sheet = workbook.createSheet(sheetName);

        int ii = 0;
        sheet.setColumnWidth(ii++, 2 * 256+60); // 单位为1/256个字符宽度
        sheet.setColumnWidth(ii++, 6 * 256); // 单位为1/256个字符宽度
        sheet.setColumnWidth(ii++, 10 * 256); // 单位为1/256个字符宽度
        sheet.setColumnWidth(ii++, 15 * 256); // 单位为1/256个字符宽度
        sheet.setColumnWidth(ii++, 15 * 256); // 单位为1/256个字符宽度
        sheet.setColumnWidth(ii++, 15 * 256); // 单位为1/256个字符宽度
        sheet.setColumnWidth(ii++, 10 * 256); // 单位为1/256个字符宽度
        sheet.setColumnWidth(ii++, 10 * 256); // 单位为1/256个字符宽度
        sheet.setColumnWidth(ii++, 10 * 256); // 单位为1/256个字符宽度
        sheet.setColumnWidth(ii++, 10 * 256); // 单位为1/256个字符宽度
        sheet.setColumnWidth(ii++, 10 * 256); // 单位为1/256个字符宽度
        sheet.setColumnWidth(ii++, 10 * 256); // 单位为1/256个字符宽度
        sheet.setColumnWidth(ii++, 50 * 256); // 单位为1/256个字符宽度
        sheet.setColumnWidth(ii++, 60 * 256); // 单位为1/256个字符宽度
        Row row0 = sheet.createRow(0);
        ii = 0;
        row0.createCell(ii++).setCellValue("ID");
        row0.createCell(ii++).setCellValue("单位");
        row0.createCell(ii++).setCellValue("等级");
        row0.createCell(ii++).setCellValue("生命值");
        row0.createCell(ii++).setCellValue("主动攻击范围");
        row0.createCell(ii++).setCellValue("攻击距离");
        row0.createCell(ii++).setCellValue("护甲属性");
        row0.createCell(ii++).setCellValue("护甲值");
        row0.createCell(ii++).setCellValue("护甲减伤");
        row0.createCell(ii++).setCellValue("攻击属性");
        row0.createCell(ii++).setCellValue("攻击力");
        row0.createCell(ii++).setCellValue("攻击间隔");
        row0.createCell(ii++).setCellValue("掉落/出售物品");
        row0.createCell(ii++).setCellValue("备注");

        // 参数分别是冻结的列数、冻结的行数、冻结前的列数、冻结前的行数
        sheet.createFreezePane(2, 1, 2, 1);

        {
            final int LAST_COL = 12;
            Row row = sheet.createRow(1);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 1, LAST_COL)); // 行从0开始，列从0开始
            {
                Cell cell = row.createCell(1);
                XSSFRichTextString richText = new XSSFRichTextString();
                appendRichText(workbook, richText, (short) 13, "#000000",
                        " ");
                appendRichText(workbook, richText, (short) 13, "#FF0000",
                        " ");
                cell.setCellValue(richText);
            }
        }

        // 创建单元格样式并设置背景颜色
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFillForegroundColor(IndexedColors.BLACK.getIndex()); // 设置黄色背景
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND); // 设置填充模式

        // 设置边框颜色
        cellStyle.setBottomBorderColor(IndexedColors.YELLOW.getIndex()); // 用默认颜色替换
        cellStyle.setTopBorderColor(IndexedColors.YELLOW.getIndex()); // 用默认颜色替换
        cellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex()); // 用默认颜色替换
        cellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex()); // 用默认颜色替换

        // // 设置边框样式
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        // 设置水平居中
        cellStyle.setAlignment(HorizontalAlignment.RIGHT); // 设置为左对齐

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
        cellStyle2.setWrapText(true);

        XSSFFont font = workbook.createFont();
        font.setFontName("Microsoft YaHei");
        // 设置字体颜色为16进制颜色
        XSSFColor hexColor = new XSSFColor(java.awt.Color.decode("#cccccc"), null);
        font.setColor(hexColor);
        cellStyle.setFont(font);
        cellStyle2.setFont(font);

        int dataI = 1;
        for (Entry<String, List<UnitDetail>> e : mapList.entrySet()) {
            {
                String key = e.getKey();
                dataI++;
                final int LAST_COL = 12;
                Row row = sheet.createRow(dataI);
                row.setHeight((short) 800);
                sheet.addMergedRegion(new CellRangeAddress(dataI, dataI, 2, LAST_COL)); // 行从0开始，列从0开始

                insertDescription(row, 0, cellStyle, workbook, "|cffC0D9D9" + " ");
                insertDescription(row, 1, cellStyle, workbook, "|cffC0D9D9" + " ");
                insertDescription(row, 13, cellStyle, workbook, "|cffC0D9D9" + " ");

                Cell cell = row.createCell(2);
                cell.setCellStyle(cellStyle2);
                XSSFRichTextString richText = new XSSFRichTextString();
                appendRichText(workbook, richText, (short) 18, "#999999", key);
                cell.setCellValue(richText);

            }

            List<UnitDetail> list = e.getValue();

            for (int i = 0; i < list.size(); i++) {
                UnitDetail unit = list.get(i);
                if(unit==null)continue;
                dataI++;
                Row row = sheet.createRow(dataI);
                int col = 0;
                try {
                    insertDescription(row, col++, cellStyle, workbook, "|cff000000" + unit.getId());
                    insertDescription(row, col++, cellStyle, workbook, "|cffcc99ff" + unit.getName());
                    insertDescription(row, col++, cellStyle, workbook, "|cff999999" + unit.getLevel());

                    StringBuilder hpStr = new StringBuilder(unit.getHp());
                    int len = hpStr.length();
                    while ((len -= 4) > 0) {
                        hpStr.insert(len, ",");
                    }

                    insertDescription(row, col++, cellStyle, workbook, "|cff99cc00" + hpStr);
                    insertDescription(row, col++, cellStyle, workbook, "|cff99cc00" + unit.getAcquire());
                    insertDescription(row, col++, cellStyle, workbook, "|cff99cc00" + unit.getRangeN1());
                    insertDescription(row, col++, cellStyle, workbook, "|cff99cc00" + unit.getDefType());
                    insertDescription(row, col++, cellStyle, workbook, "|cff99cc00" + unit.getDef());
                    insertDescription(row, col++, cellStyle, workbook, "|cff99cc00" + unit.getDamageReduce());
                    insertDescription(row, col++, cellStyle, workbook, "|cff99cc00" + unit.getAtkType1());
                    insertDescription(row, col++, cellStyle, workbook, "|cff99cc00" + unit.getDmgplus1());
                    insertDescription(row, col++, cellStyle2, workbook, "|cff99cc00" + unit.getCool1());

                    StringBuilder buf = new StringBuilder();
                    if (unit.getDropString() != null) {
                        unit.getDropString().forEach(item -> {
                            if (item == null)
                                return;
                            buf.append(item.getItemName());
                            buf.append("、");
                        });
                        if(buf.length()>0){
                            buf.setLength(buf.length()-1);
                        }
                    }

                    insertDescription(row, col++, cellStyle2, workbook, "|cff99cc00" + buf.toString());
                    insertDescription(row, col++, cellStyle2, workbook, (short)11, "|cff99cc00" + unit.getMark() == null ? "" : unit.getMark());
                    // ExcelImageInsert.drawingBlp(sheet, unit.getArt(), 0, dataI, 1, 1, 1);

                } catch (Exception e2) {
                    e2.printStackTrace();
                    System.out.println("error:" + unit.getName());
                    System.out.println(unit.getScoreScreenIcon());
                }

                // for (int j = 0; j < row.getLastCellNum(); j++) {
                //     Cell cell = row.getCell(j);
                //     if (cell != null) {
                //         cell.setCellStyle(cellStyle);
                //     }
                // }

            }
        }

    }


    public static void main(String[] args) throws IOException {

        List<String[]> text = parseText(
                "|cffff9900当前照片：|r无|cff99ccff|n|r|cffff9900已拍摄女性：|r0|cff99cc00|n|r|cffff99cc类型：饰品|n|r|cff99cc00|n|r似乎是用拉索姆科技材料打造的照相机，可以将眼前景象转换为图片。随着拍摄内容增加，还能随机提升属性。|n");
        System.out.println(text);
    }

}