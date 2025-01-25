package com.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageMerger {

    public static void main(String[] args) {

        String basePath = "html\\javafindjob1.github.io\\x7\\x7-imgs\\";
        // 图片路径数组
        String[][] imagePaths = {
                { "int.png",
                        "int.png" },
                {
                        "str.png"
                }
        };

        // 输出合并后的图片路径
        String outputPath = "out.png";
        merge(imagePaths, outputPath, 64, 64, basePath);
    }

    public static void merge(String[][] imagePaths, String outputPath, int gapx, int gapy, String basePath) {
        try {
            BufferedImage[][] images = readPath(imagePaths, basePath);
            BufferedImage mergedImage = mergeImages(images, gapx, gapy);

            // 保存合并后的图片
            ImageIO.write(mergedImage, outputPath.substring(outputPath.indexOf(".")+1), new File(basePath+outputPath));
            System.out.println("图片合并成功！");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static BufferedImage[][] readPath(String[][] imagePaths, String basePath) throws IOException {
        BufferedImage[][] images = new BufferedImage[imagePaths.length][];
        for (int i = 0; i < imagePaths.length; i++) {
            images[i] = new BufferedImage[imagePaths[i].length];
            for (int j = 0; j < imagePaths[i].length; j++) {
                if (imagePaths[i][j] == null) {
                    images[i][j] = null;
                } else {
                    File img = new File(basePath+imagePaths[i][j]);
                    images[i][j] = ImageIO.read(img);
                }
            }
        }
        return images;
    }

    public static BufferedImage readFile(File file) throws IOException {
        if(file == null || !file.exists()){
            return null;
        }
        return ImageIO.read(file);
    }

    public static BufferedImage mergeImages(BufferedImage[][] images, int gapx, int gapy) throws IOException {
        // 读取所有图片
        int totalWidth = 0;
        int totalHeight = 0;
        for (int i = 0; i < images.length; i++) {
            int maxHeight = 0;
            int maxWidth = 0;
            for (int j = 0; j < images[i].length; j++) {
                if (images[i][j] == null) {
                    maxWidth += gapx + 6;
                    maxHeight = Math.max(maxHeight, gapy);
                } else {
                    maxWidth += images[i][j].getWidth() + 6;
                    maxHeight = Math.max(maxHeight, images[i][j].getHeight());
                }
            }
            totalHeight += maxHeight + 6;
            totalWidth = Math.max(totalWidth, maxWidth);
        }
        totalWidth -= 6;
        totalHeight -= 6;

        // 创建一个新的 BufferedImage 来保存合并后的图片
        BufferedImage mergedImage = new BufferedImage(totalWidth, totalHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = mergedImage.createGraphics();

        int y = 0;
        for (int i = 0; i < images.length; i++) {
            int x = 0;
            int maxHeight = 0;
            for (int j = 0; j < images[i].length; j++) {
                BufferedImage image = images[i][j];
                if (image == null) {
                    x += gapx + 6;
                    maxHeight = Math.max(maxHeight, gapy);
                } else {
                    g2d.drawImage(image, x, y, null);
                    x += image.getWidth() + 6;
                    maxHeight = Math.max(maxHeight, image.getHeight());
                }
            }
            y += maxHeight + 6;
        }

        g2d.dispose();
        return mergedImage;
    }
}