package com.abc;

import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

import com.abc.sqlite.SqLiteJDBC;

import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UnitSheet extends AbstractSheet {
    
    private XSSFCreationHelper creationHelper;
    private Map<String,ItemDetail> idItemMap;


    public UnitSheet(XSSFWorkbook workbook, Map<String,ItemDetail> idItemMap) {
        this.workbook = workbook;
        this.creationHelper = workbook.getCreationHelper();;
        this.idItemMap = idItemMap;
    }

    public void insertHead(String sheetName) throws IOException {
        XSSFSheet sheet = workbook.createSheet(sheetName);
        workbook.setSheetOrder(sheetName, 0); // 将 Sheet3 移动到第一个位置
        sheet.setColumnWidth(1, 60 * 256); // 单位为1/256个字符宽度

        {
            final int LAST_COL = 19;
            short height = (short) (sheet.getDefaultRowHeight() * 3);
            {
                Row row = sheet.createRow(0);
                row.setHeight(height);
                sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, LAST_COL)); // 行从0开始，列从0开始
                Cell cell = row.createCell(0);
                XSSFRichTextString richText = new XSSFRichTextString();
                appendRichText(workbook, richText, (short) 30, "#FF0000",
                        "此文档特别鸣谢：");
                cell.setCellValue(richText);
            }

            {
                Row row = sheet.createRow(1);
                row.setHeight(height);
                sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, LAST_COL)); // 行从0开始，列从0开始
                Cell cell = row.createCell(0);
                XSSFRichTextString richText = new XSSFRichTextString();
                appendRichText(workbook, richText, (short) 30, "#FF0000",
                        "高音王子（装备剪辑，贴图，文本介绍）");
                cell.setCellValue(richText);
            }

            {
                Row row = sheet.createRow(2);
                row.setHeight(height);
                sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, LAST_COL)); // 行从0开始，列从0开始
                Cell cell = row.createCell(0);
                XSSFRichTextString richText = new XSSFRichTextString();
                appendRichText(workbook, richText, (short) 30, "#FF0000",
                        "冰冰爱球球、世界之铠(备注，点评)");
                cell.setCellValue(richText);
            }

        }
        
        {
            // 创建一个新的行
            Row row = sheet.createRow(4);
            // 创建一个新的单元格
            Cell cell = row.createCell(1);
            // 设置单元格值为超链接
            XSSFRichTextString richText = new XSSFRichTextString();
            appendRichText(workbook, richText, (short) 10, "#000000",
                    "【腾讯文档】西7装备介绍_目录");
            cell.setCellValue(richText);
        }
        
        {
            // 创建一个新的行
            Row row = sheet.createRow(5);

            // 创建一个新的单元格
            Cell cell = row.createCell(1);

            // 创建超链接
            Hyperlink link = creationHelper.createHyperlink(HyperlinkType.URL);
            link.setAddress("https://docs.qq.com/sheet/DZEJqV2lCVWdKY1hq?tab=BB08J2");
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
            // 设置单元格值为超链接
            XSSFRichTextString richText = new XSSFRichTextString();
            appendRichText(workbook, richText, (short) 14, "#FF0000",
                    "近期（与" + SqLiteJDBC.getBaseVersion()+"比较）更新记录（具体内容在下方标签页查看）：");
            cell.setCellValue(richText);
            try (SqLiteJDBC db = new SqLiteJDBC()) {
                // 连接SQLite数据库，数据库文件是test.db，如果文件不存在，会自动创建
                AtomicInteger dataI = new AtomicInteger(8);
                updateInfo(sheet, dataI, "新增：", db.queryAddDiffSql());
                updateInfo(sheet, dataI, "修改：", db.queryPropDiffSql());
                updateInfo(sheet, dataI, "删除：", db.queryDelDiffSql());
                updateInfo(sheet, dataI, "物品获取方式变化：", db.queryDropPlaceDiffSql());
                List<ItemDetail> list = db.queryShopDiffSql();
                Iterator<ItemDetail> iterator = list.iterator();
                // 神秘商店新增出售
                List<ItemDetail> newList = new ArrayList<>();
                // 神秘商店不在出售
                List<ItemDetail> delList = new ArrayList<>();
                while(iterator.hasNext()){
                    ItemDetail next = iterator.next();
                    if(next.getShop2().indexOf("√")>=0){
                        // 之前版本出售
                        if(next.getShop().indexOf("√")>=0){
                            // 当前版本也出售
                        }else{
                            // 当前版本不出售
                            delList.add(next);
                        }
                    }else{
                        // 之前版本不出售
                        if(next.getShop().indexOf("√")>=0){
                            // 当前版本出售
                            newList.add(next);
                        }else{
                            // 当前版本不出售
                        }
                    }
                }
                updateInfo(sheet, dataI, "神秘商店新增出售：", newList);
                updateInfo(sheet, dataI, "神秘商店不再出售：", delList);
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
            link.setAddress("'"+item.getType() + "'!B" + idItemMap.get(item.getId()).getRowNum());
            cell.setHyperlink(link);
    
            XSSFRichTextString richText = new XSSFRichTextString();
            StringBuffer buf = new StringBuffer();
            buf.append(message).append("[").append(item.getLevel()).append(item.getType()).append("]").append(item.getName());
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
        sheet.setColumnWidth(ii++, 3 * 256); // 单位为1/256个字符宽度
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
        sheet.setColumnWidth(ii++, 40 * 256); // 单位为1/256个字符宽度
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
        row0.createCell(ii++).setCellValue("掉落物品");
        row0.createCell(ii++).setCellValue("备注");

        // 参数分别是冻结的列数、冻结的行数、冻结前的列数、冻结前的行数
        sheet.createFreezePane(2, 1, 2, 1);

        {
            final int LAST_COL = 13;
            Row row = sheet.createRow(1);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 2, LAST_COL)); // 行从0开始，列从0开始
            {
                Cell cell = row.createCell(2);
                XSSFRichTextString richText = new XSSFRichTextString();
                appendRichText(workbook, richText, (short) 13, "#000000",
                        "表中为单人难1数据,难2 生命/攻击力 x 1.25 难3 x 1.65  难4 x 2  难5 x 2.7 ");
                appendRichText(workbook, richText, (short) 13, "#FF0000",
                        "  (4人难5 血量x 4.32 攻击力x3 boss护甲 x1.8)");
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
                final int LAST_COL = 13;
                Row row = sheet.createRow(dataI);
                row.setHeight((short) 800);
                sheet.addMergedRegion(new CellRangeAddress(dataI, dataI, 2, LAST_COL-1)); // 行从0开始，列从0开始

                insertDescription(row, 0, cellStyle, workbook, "|cffC0D9D9" + "");
                insertDescription(row, 1, cellStyle, workbook, "|cffC0D9D9" + "");
                insertDescription(row, LAST_COL, cellStyle, workbook, "|cffC0D9D9" + "");

                Cell cell = row.createCell(2);
                cell.setCellStyle(cellStyle2);
                XSSFRichTextString richText = new XSSFRichTextString();
                appendRichText(workbook, richText, (short) 18, "#999999", key);
                cell.setCellValue(richText);

            }

            List<UnitDetail> list = e.getValue();

            for (int i = 0; i < list.size(); i++) {
                UnitDetail unit = list.get(i);
                if (unit == null)
                    continue;
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
                            buf.append(item.getName());
                            buf.append("、");
                        });
                        // 删除末尾的顿号
                        if(buf.length()>0){
                            buf.setLength(buf.length()-1);
                        }
                    }

                    insertDescription(row, col++, cellStyle2, workbook, "|cff99cc00" + buf.toString());
                    insertDescription(row, col++, cellStyle2, workbook, "|cffcccccc" + " ");
                    // ExcelImageInsert.drawingBlp(sheet, unit.getArt(), 0, dataI, 2, 1, 1);

                } catch (Exception e2) {
                    e2.printStackTrace();
                    System.out.println("error:" + unit.getName());
                    System.out.println(unit.getScoreScreenIcon());
                }

            }
        }

    }

    public static void main(String[] args) throws IOException {

        List<String[]> text = parseText(
                "|cffff9900当前照片：|r无|cff99ccff|n|r|cffff9900已拍摄女性：|r0|cff99cc00|n|r|cffff99cc类型：饰品|n|r|cff99cc00|n|r似乎是用拉索姆科技材料打造的照相机，可以将眼前景象转换为图片。随着拍摄内容增加，还能随机提升属性。|n");
        System.out.println(text);
    }

}