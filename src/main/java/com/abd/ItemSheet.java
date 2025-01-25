package com.abd;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

import com.abd.sqlite.SqLiteJDBC;

import static org.junit.Assert.assertNotNull;

import java.io.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ItemSheet extends AbstractSheet{
    
    public ItemSheet(XSSFWorkbook workbook) {
        this.workbook = workbook;
    }

    public void insert(String sheetName, Map<String, List<ItemDetail>> mapList) throws IOException, SQLException {

        if(sheetName.equals("未归类")){
            int total = 0;
            for(String key: mapList.keySet()){
                total += mapList.get(key).size();
            }
            if(total == 0){
                return;
            }
        }else{
            // 入库方便对比
            try(SqLiteJDBC db = new SqLiteJDBC();){
                for (Entry<String, List<ItemDetail>> entry : mapList.entrySet()) {
                    List<ItemDetail> list = entry.getValue();
                    for(ItemDetail item: list){
                        if(item==null)continue;
                        try{
                            db.insertData(item);
                        }catch(SQLException e){
                            // 主键冲突
                            System.out.println("主键冲突");
                        }
                    }
                }
            }
        }

        XSSFSheet sheet = workbook.createSheet(sheetName);

        int colIndex = 0;
        sheet.setColumnWidth(colIndex++, 2 * 256 + 60); // 单位为1/256个字符宽度
        sheet.setColumnWidth(colIndex++, 6 * 256); // 单位为1/256个字符宽度
        sheet.setColumnWidth(colIndex++, 60 * 256); // 单位为1/256个字符宽度
        sheet.setColumnWidth(colIndex++, 2 * 256); // 单位为1/256个字符宽度
        sheet.setColumnWidth(colIndex++, 40 * 256); // 单位为1/256个字符宽度
        sheet.setColumnWidth(colIndex++, 40 * 256); // 单位为1/256个字符宽度
        sheet.setColumnWidth(colIndex++, 40 * 256); // 单位为1/256个字符宽度

        Row row0 = sheet.createRow(0);

        colIndex = 0;
        row0.createCell(colIndex++).setCellValue("ID");
        row0.createCell(colIndex++).setCellValue("名称");
        row0.createCell(colIndex++).setCellValue("属性");
        row0.createCell(colIndex++).setCellValue("");
        row0.createCell(colIndex++).setCellValue("获取途径");
        row0.createCell(colIndex++).setCellValue("合成材料");
        row0.createCell(colIndex++).setCellValue("备注");

        // 参数分别是冻结的列数、冻结的行数、冻结前的列数、冻结前的行数
        sheet.createFreezePane(2, 1, 2, 1);

        final int LAST_COL = 6;
        {
            Row row = sheet.createRow(1);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 2, LAST_COL)); // 行从0开始，列从0开始
            {
                Cell cell = row.createCell(2);
                XSSFRichTextString richText = new XSSFRichTextString();
                appendRichText(workbook, richText, (short) 13, "#000000",
                        "");
                cell.setCellValue(richText);
            }

            Row row1 = sheet.createRow(2);
            sheet.addMergedRegion(new CellRangeAddress(2, 2, 2, LAST_COL)); // 行从0开始，列从0开始
            {
                Cell cell = row1.createCell(2);
                XSSFRichTextString richText = new XSSFRichTextString();
                appendRichText(workbook, richText, (short) 13, "#000000",
                        "暗属性火属性土属性水属性木属性光属性强化意思是造成法术和物理伤害时额外造成伤害");
                cell.setCellValue(richText);
            }

            Row row2 = sheet.createRow(3);
            sheet.addMergedRegion(new CellRangeAddress(3, 3, 2, LAST_COL)); // 行从0开始，列从0开始
            {
                Cell cell = row2.createCell(2);
                XSSFRichTextString richText = new XSSFRichTextString();
                appendRichText(workbook, richText, (short) 13, "#ff0000",
                        "皮肤新增装备的合成卷轴在魔王城抽奖旁边的npc");
                cell.setCellValue(richText);
            }

        }

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
        {
            
            XSSFFont font = workbook.createFont();
            font.setFontName("Microsoft YaHei");
            // 设置字体颜色为16进制颜色
            XSSFColor hexColor = new XSSFColor(java.awt.Color.decode("#999999"), null);
            font.setColor(hexColor);
            cellStyleCenter.setFont(font);
        }
        
        

        // 创建单元格样式并设置背景颜色
        CellStyle cellStyleBottom = workbook.createCellStyle();
        cellStyleBottom.setFillForegroundColor(IndexedColors.WHITE.getIndex()); // 设置黄色背景
        cellStyleBottom.setFillPattern(FillPatternType.SOLID_FOREGROUND); // 设置填充模式

        // 设置边框颜色
        cellStyleBottom.setLeftBorderColor(IndexedColors.BLACK.getIndex()); // 用默认颜色替换
        cellStyleBottom.setRightBorderColor(IndexedColors.BLACK.getIndex()); // 用默认颜色替换
        cellStyleBottom.setBottomBorderColor(IndexedColors.YELLOW.getIndex()); // 用默认颜色替换
        cellStyleBottom.setTopBorderColor(IndexedColors.YELLOW.getIndex()); // 用默认颜色替换

        // // 设置边框样式
        cellStyleBottom.setBorderLeft(BorderStyle.THIN);
        cellStyleBottom.setBorderRight(BorderStyle.THIN);
        cellStyleBottom.setBorderTop(BorderStyle.THIN);
        cellStyleBottom.setBorderBottom(BorderStyle.THIN);
        // 设置水平居中
        cellStyleBottom.setAlignment(HorizontalAlignment.CENTER); // 设置为左对齐

        // 设置垂直居中
        cellStyleBottom.setVerticalAlignment(VerticalAlignment.BOTTOM); // 设置为垂直居中
        cellStyleBottom.setWrapText(true);
        {
            
            XSSFFont font = workbook.createFont();
            font.setFontName("Microsoft YaHei");
            // 设置字体颜色为16进制颜色
            XSSFColor hexColor = new XSSFColor(java.awt.Color.decode("#999999"), null);
            font.setColor(hexColor);
            cellStyleBottom.setFont(font);
        }
        

        int dataIndex = 3;
        for (Entry<String, List<ItemDetail>> entry : mapList.entrySet()) {
            String title = entry.getKey();
            List<ItemDetail> list = entry.getValue();

            if(mapList.size() > 1){
                // 添加标题
                dataIndex++;
                Row row = sheet.createRow(dataIndex);
                row.setHeight((short) 800);
                sheet.addMergedRegion(new CellRangeAddress(dataIndex, dataIndex, 2, LAST_COL-1)); // 行从0开始，列从0开始
                insertDescription(row, 0, cellStyleCenter, workbook, "|cffC0D9D9" + " ");
                insertDescription(row, 1, cellStyleCenter, workbook, "|cffC0D9D9" + " ");
                insertDescription(row, LAST_COL, cellStyleCenter, workbook, "|cffC0D9D9" + " ");
                {
                    Cell cell = row.createCell(2);
                    XSSFRichTextString richText = new XSSFRichTextString();
                    appendRichText(workbook, richText, (short) 17, "#999999", title);
                    cell.setCellValue(richText);
                    cell.setCellStyle(cellStyleCenter);
                }

            }

            for (int i = 0; i < list.size(); i++) {
                ItemDetail item = list.get(i);
                dataIndex++;
                Row row = sheet.createRow(dataIndex);
                row.setHeight((short) 4100);
                int col = 0;
                assertNotNull("物品无能为空, 单元表是" + sheetName + ",list位置:" + i + " list.size=" + list.size(), item);
                try {
                    // 存一下物品在工作表中的位置,供更新记录表使用
                    item.setRowNum(dataIndex+1);
                    insertDescription(row, col++, cellStyleCenter, workbook, "|cff000000" + item.getId());
                    insertDescription(row, col++, cellStyleCenter, workbook, "|cffcc99ff" + item.getName());
                    insertDescription(row, col++, cellStyleCenter, workbook, item.getDescription());
                    insertDescription(row, col++, cellStyleCenter, workbook, "|cff99cc00" + "");
                    insertDescription(row, col++, cellStyleCenter, workbook,
                            "|cff99cc00" + item.getDropPlace() == null ? "" : item.getDropPlace());
                    insertDescription(row, col++, cellStyleCenter, workbook,
                            "|cff99cc00" + item.getSynthesisFormula() == null ? "" : item.getSynthesisFormula());
                    insertDescription(row, col++, cellStyleCenter, workbook,
                            "|cffff0000" + item.getMark() == null ? "" : item.getMark());
                    ExcelImageInsert.drawingBlp(sheet, item.getIcon(), 0, dataIndex, 2, 1, 1);

                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("error:" + item.getName());
                }

                for (int j = 0; j < row.getLastCellNum(); j++) {
                    Cell cell = row.getCell(j);
                    if (cell != null) {
                        cell.setCellStyle(cellStyleCenter);
                    }
                }
            }

        }

    }


    public void insertShop(Row row, int column, CellStyle cellStyle, XSSFWorkbook workbook, String description) {
        Cell cell = row.createCell(column);
        cell.setCellStyle(cellStyle);

        // 创建富文本字符串
        XSSFRichTextString richText = new XSSFRichTextString();
        String[] ss = description.split("\\s+");
        appendRichText(workbook, richText, (short) 77, null, ss[0]);
        if (ss.length > 1) {
            appendRichText(workbook, richText, (short) 12, null, " " + ss[1]);
        }
        cell.setCellValue(richText);
    }

    public static void main(String[] args) throws IOException {

        // List<String[]> text =
        // parseText("|cffff9900当前照片：|r无|cff99ccff|n|r|cffff9900已拍摄女性：|r0|cff99cc00|n|r|cffff99cc类型：饰品|n|r|cff99cc00|n|r似乎是用拉索姆科技材料打造的照相机，可以将眼前景象转换为图片。随着拍摄内容增加，还能随机提升属性。|n");
        List<String[]> text = parseText("|cffffcc00|r|n|cffff99cc应急治疗药水|r × 1|n|cffff0000小恶魔的魔法书|r × 1|n|n|cffffcc00");

        text.stream().forEach(e -> System.out.println(e[0] + " --- " + e[1]));
    }


}