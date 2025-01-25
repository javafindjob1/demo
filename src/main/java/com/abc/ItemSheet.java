package com.abc;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

import com.abc.sqlite.SqLiteJDBC;

import java.io.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ItemSheet extends AbstractSheet {

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
        sheet.setColumnWidth(colIndex++, 2 * 256); // 单位为1/256个字符宽度
        sheet.setColumnWidth(colIndex++, 6 * 256); // 单位为1/256个字符宽度
        sheet.setColumnWidth(colIndex++, 10 * 256); // 单位为1/256个字符宽度
        sheet.setColumnWidth(colIndex++, 60 * 256); // 单位为1/256个字符宽度
        sheet.setColumnWidth(colIndex++, 2 * 256); // 单位为1/256个字符宽度
        sheet.setColumnWidth(colIndex++, 40 * 256); // 单位为1/256个字符宽度
        sheet.setColumnWidth(colIndex++, 20 * 256); // 单位为1/256个字符宽度
        sheet.setColumnWidth(colIndex++, 20 * 256); // 单位为1/256个字符宽度
        sheet.setColumnWidth(colIndex++, 20 * 256); // 单位为1/256个字符宽度
        sheet.setColumnWidth(colIndex++, 40 * 256); // 单位为1/256个字符宽度

        Row row0 = sheet.createRow(0);
        colIndex = 0;
        row0.createCell(colIndex++).setCellValue("ID");
        row0.createCell(colIndex++).setCellValue("名称");
        row0.createCell(colIndex++).setCellValue("品质");
        row0.createCell(colIndex++).setCellValue("属性");
        row0.createCell(colIndex++).setCellValue("获取途径");
        row0.createCell(colIndex++).setCellValue("获取途径");
        row0.createCell(colIndex++).setCellValue("神秘商店是否出售");
        row0.createCell(colIndex++).setCellValue("锻造材料");
        row0.createCell(colIndex++).setCellValue("专属");
        row0.createCell(colIndex++).setCellValue("备注");
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 4, 5)); // 行从0开始，列从0开始

        // 参数分别是冻结的列数、冻结的行数、冻结前的列数、冻结前的行数
        sheet.createFreezePane(2, 1, 2, 1);
        final int LAST_COL = 9;

        {
            Row row = sheet.createRow(1);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 2, LAST_COL)); // 行从0开始，列从0开始
            {
                Cell cell = row.createCell(2);
                XSSFRichTextString richText = new XSSFRichTextString();
                appendRichText(workbook, richText, (short) 13, "#000000",
                        "常规概念：同一类装备随机掉落源，可以爆出的装备（举例：史诗城巨兽能掉的所有A都属于常规装，独角兽能掉的所有S都属于常规装）注意：常规装和随机商店是否出售完全无关联。");
                cell.setCellValue(richText);
            }

            Row row1 = sheet.createRow(2);
            sheet.addMergedRegion(new CellRangeAddress(2, 2, 2, LAST_COL)); // 行从0开始，列从0开始
            {
                Cell cell = row1.createCell(2);
                XSSFRichTextString richText = new XSSFRichTextString();
                appendRichText(workbook, richText, (short) 13, "#000000",
                        "特殊概念：所有非常规的装备一律属于特殊装备，需要作者设置指定掉落。特殊装备，大多情况不能靠常规怪刷获取（部分例外，不绝对）。注意特殊装也有可能商店随机购买。");
                cell.setCellValue(richText);
            }

            Row row2 = sheet.createRow(3);
            sheet.addMergedRegion(new CellRangeAddress(3, 3, 2, LAST_COL)); // 行从0开始，列从0开始
            {
                Cell cell = row2.createCell(2);
                XSSFRichTextString richText = new XSSFRichTextString();
                appendRichText(workbook, richText, (short) 13, "#ff0000",
                        "闪避，法术暴击，法术抗性，暴击率，暴击伤害，法术暴击伤害，冷却，绝对闪避，以上叠加办法都是直接加法叠加，但是如果携带同名装备，第二件以后的都只有一半效果。");
                cell.setCellValue(richText);
            }

        }

        // 创建单元格样式并设置背景颜色
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFillForegroundColor(IndexedColors.BLACK.getIndex()); // 设置黄色背景
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND); // 设置填充模式

        // 设置边框颜色
        cellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex()); // 用默认颜色替换
        cellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex()); // 用默认颜色替换
        cellStyle.setBottomBorderColor(IndexedColors.YELLOW.getIndex()); // 用默认颜色替换
        cellStyle.setTopBorderColor(IndexedColors.YELLOW.getIndex()); // 用默认颜色替换

        // // 设置边框样式
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        // 设置水平居中
        cellStyle.setAlignment(HorizontalAlignment.LEFT); // 设置为左对齐

        // 设置垂直居中
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER); // 设置为垂直居中
        cellStyle.setWrapText(true);

        XSSFFont font = workbook.createFont();
        font.setFontName("Microsoft YaHei");
        // 设置字体颜色为16进制颜色
        XSSFColor hexColor = new XSSFColor(java.awt.Color.decode("#cccccc"), null);
        font.setColor(hexColor);
        cellStyle.setFont(font);

        int dataIndex = 3;
        for (Entry<String, List<ItemDetail>> entry : mapList.entrySet()) {
            List<ItemDetail> list = entry.getValue();
            String title = entry.getKey();
            if(mapList.size() > 1){
                // 添加标题
                Row row = sheet.createRow(++dataIndex);
                row.setHeight((short) 800);
                sheet.addMergedRegion(new CellRangeAddress(dataIndex, dataIndex, 2, LAST_COL-1)); // 行从0开始，列从0开始
                insertDescription(row, 0, cellStyle, workbook, "|cffC0D9D9" + "");
                insertDescription(row, 1, cellStyle, workbook, "|cffC0D9D9" + "");
                insertDescription(row, LAST_COL, cellStyle, workbook, "|cffC0D9D9" + "");
                {
                    Cell cell = row.createCell(2);
                    XSSFRichTextString richText = new XSSFRichTextString();
                    appendRichText(workbook, richText, (short) 17, "#999999", title);
                    cell.setCellValue(richText);
                    cell.setCellStyle(cellStyle);
                }

            }


            for (int i = 0; i < list.size(); i++) {
                ItemDetail item = list.get(i);
                if(item ==null)continue;
                Row row = sheet.createRow(++dataIndex);
                row.setHeight((short) 4100);
                // 保存一下行号,供更新记录使用
                item.setRowNum(dataIndex+1);
                
                int col = 0;
                insertDescription(row, col++, cellStyle, workbook, "|cff000000" + item.getId());
                insertDescription(row, col++, cellStyle, workbook, "|cffcc99ff" + item.getName());
                String meta = "  常规";
                if ("0".equals(item.getPickRandom())) {
                    meta = "  特殊";
                }
                insertDescription(row, col++, cellStyle, workbook, "|cff99cc00" + item.getLevel() + meta);
                insertDescription(row, col++, cellStyle, workbook, item.getDescription());
                insertDescription(row, col++, cellStyle, workbook, "");
                insertDescription(row, col++, cellStyle, workbook,
                        "|cff99cc00" + (item.getDropPlace() == null ? "" : item.getDropPlace()));
                insertShop(row, col++, cellStyle, workbook, item.getShop());
                insertDescription(row, col++, cellStyle, workbook, item.getSynthesisFormula());
                insertDescription(row, col++, cellStyle, workbook, item.getHeroExclusive());
                String mark = item.getMark() == null ? "" : item.getMark();
                if(mark.startsWith("①")){
                    mark = "|cffcc99ff" + mark;
                }else {
                    mark = "|cffcccccc" + mark;
                }
                insertDescription(row, col++, cellStyle, workbook, mark);
                try {
                    ExcelImageInsert.drawingBlp(sheet, item.getIcon(), 0, dataIndex, 2, 1, 1);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("error:" + item.getName());
                }

                // 重新设置行高
                if(this.newHeight > 0){
                    row.setHeight((short)(this.newHeight));
                    this.newHeight = 0;
                }

            }
        }

    }

    public void insertShop(Row row, int column, CellStyle cellStyle, XSSFWorkbook workbook, String description) {
        Cell cell = row.createCell(column);
        cell.setCellStyle(cellStyle);

        // 创建富文本字符串
        XSSFRichTextString richText = new XSSFRichTextString();
        if(description == null){
            description = "";
        }
        String[] ss = description.split("\\s+");
        if(ss[0].equals("")){
            ss[0] = " ";
        }
        appendRichText(workbook, richText, (short) 77, null, ss[0]);
        if (ss.length > 1) {
            appendRichText(workbook, richText, (short) 12, null, " " + ss[1]);
        }
        cell.setCellValue(richText);
    }

    public static void main(String[] args) throws IOException {

        List<String[]> text = parseText(
                "|cff99ccff攻击力+2000|n智力+200|n生命上限+1200|n降低6%技能冷却时间|n|r|cff999999真神专属：|n法术暴击+9%|r|cff99cc00|n等级：D+|r|n|cffff9900类型：武器|r|n|n西海鱼人族用女性鱼人尾鳍做成的魔法兵器。");

        for(String[] s:text){
            System.out.println(s[0] + s[1]);
        }
    }

}