package com.b4;

import org.apache.poi.xssf.usermodel.*;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ExcelImageInsert {
    private static String assetPath = "";

    public static void set(String assetPath) {
        ExcelImageInsert.assetPath = assetPath;
    }

    public static void main(String[] args) {
        try {
            System.out.println("1234");
            // 创建一个新的 XSSFWorkbook
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("美图");

            // ExcelImageInsert.drawing(sheet, XSSFWorkbook.PICTURE_TYPE_GIF,
            // getBytes("莉莉丝.gif"), 0, 0, 13, 50, 1);
            // ExcelImageInsert.drawing(sheet, XSSFWorkbook.PICTURE_TYPE_GIF,
            // getBytes("轮回使者.gif"), 0, 1 * 50, 13, 2 * 50, 1);
            // ExcelImageInsert.drawing(sheet, XSSFWorkbook.PICTURE_TYPE_GIF,
            // getBytes("圣十字天使.gif"), 0, 2 * 50, 13, 3 * 50,1);
            ExcelImageInsert.drawingBlp(sheet, "ff24678-zhuangbei-wucaijian.blp", 0, 0, 2, 2, 1);

            // 保存工作簿
            FileOutputStream fos = new FileOutputStream("output.xlsx");
            workbook.write(fos);
            fos.close();

            // 关闭工作簿
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String combineFullPath(String imagePath)
            throws FileNotFoundException {
        String[] basePath = {
                assetPath + "resource\\",
                "D:\\war5-jass\\jass_plugin\\w3x2lni_zhCN_v2.5.2\\B4_刀剑传说-初\\",
                "D:\\war5-jass\\jass_plugin\\w3x2lni_zhCN_v2.5.2\\B4_刀剑传说-中\\",
                "D:\\war5-jass\\jass_plugin\\w3x2lni_zhCN_v2.5.2\\B4_刀剑传说-下\\",
                "D:\\war5-jass\\jass_plugin\\w3x2lni_zhCN_v2.5.2\\war3mpq\\",
                "D:\\war5-jass\\jass_plugin\\w3x2lni_zhCN_v2.5.2\\war3xmpq\\" };

        String fullPath = null;
        for (int i = 0; i < basePath.length; i++) {
            File tmp = new File(basePath[i] + imagePath);
            if (tmp.exists() && tmp.isFile()) {
                fullPath = basePath[i] + imagePath;
                break;
            }else{
                // System.out.println("不存在" + tmp.getAbsolutePath() + "tmp.exists()==" + tmp.exists() + "tmp.isfile()==" + tmp.isFile());
            }
        }
        if (fullPath == null) {
            String fileName = imagePath;
            if(imagePath.contains("\\")){
                fileName = imagePath.substring(imagePath.lastIndexOf("\\")+1);
            }
            System.out.println("File not found: " + imagePath);
            if(UnitParse.class.getResource("custom/" + fileName)==null){
                // System.out.println("custom中也没有找到: " + imagePath);
                fileName = "5-zhuangbei-huanzimoyan.blp";
                throw new FileNotFoundException("File not found: " + imagePath);
            }
            //     /C:/Users/76769/Desktop/demo/demo/target/classes/com/abd/custom/5-zhuangbei-huanzimoyan.blp
            return UnitParse.class.getResource("custom/" + fileName).getFile().substring(1);
            // throw new FileNotFoundException("File not found: " + imagePath);
        }

        return fullPath;

    }

    /**
     * 将 BufferedImage 转换为指定格式的字节数组。
     *
     * @param img    BufferedImage 对象
     * @param format 图像格式 (例如 "jpg", "png")
     * @return 图像的字节数组
     * @throws IOException 如果转换过程中出现错误
     */
    public static byte[] convertImageToPNGBytes(String blpPath) throws IOException {
        // File blpFile = new File(blpPath);
        // BufferedImage img = ImageIO.read(blpFile);
        File blpFile = new File(blpPath);
        BufferedImage img = null;
        img = BlpFile.read(blpFile);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(img, "png", baos);
            baos.flush();
            byte[] imageBytes = baos.toByteArray();
            return imageBytes;
        }
    }

    public static byte[] getBytes(String gifPath) throws IOException {
        try (FileInputStream fis = new FileInputStream(gifPath)) {
            byte[] bytes = new byte[fis.available()];
            fis.read(bytes);
            return bytes;
        }
    }

    public static void drawingBlp(XSSFSheet sheet, String blpMapIcon, int firstCol, int firstRow, int width, int hight,
            double scale) throws FileNotFoundException, IOException {
        if (blpMapIcon == null)
            return;
        if (blpMapIcon.endsWith(".tga")) {
            blpMapIcon = blpMapIcon.replace(".tga", ".blp");
        }
        String fullPath = combineFullPath(blpMapIcon);
        drawing(sheet, XSSFWorkbook.PICTURE_TYPE_PNG, convertImageToPNGBytes(fullPath), firstCol, firstRow, width,
                hight, scale);
    }

    public static void drawing(XSSFSheet sheet, int format, String imgPath, int firstCol, int firstRow, int width,
            int hight,
            double scale) throws FileNotFoundException, IOException {
        drawing(sheet, format, getBytes(imgPath), firstCol, firstRow, width, hight, scale);
    }

    public static void drawing(XSSFSheet sheet, int format, byte[] bytes, int firstCol, int firstRow, int width,
            int hight,
            double scale)
            throws FileNotFoundException, IOException {
        if (bytes == null) {
            return;
        }

        XSSFWorkbook workbook = sheet.getWorkbook();
        XSSFDrawing drawing = sheet.getDrawingPatriarch();
        if (drawing == null) {
            drawing = sheet.createDrawingPatriarch();
        }

        // 添加图片到工作簿
        int pictureIdx = workbook.addPicture(bytes, format);

        // 创建一个 ClientAnchor 对象
        XSSFClientAnchor anchor = new XSSFClientAnchor(0, 0, 0, 0, firstCol, firstRow, firstCol+(width-1), firstRow+(hight-1));
        // 创建一个 Picture 对象
        XSSFPicture picture = drawing.createPicture(anchor, pictureIdx);

        // 设置图片大小
        picture.resize(); // 缩放比例
    }
}