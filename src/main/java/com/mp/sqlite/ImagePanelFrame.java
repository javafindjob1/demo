package com.mp.sqlite;

import javax.swing.*;
import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;

public class ImagePanelFrame {
    public static void main(String[] args) throws MalformedURLException {
        // 创建JFrame
        JFrame frame = new JFrame("自定义图片绘制");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 创建自定义ImagePanel并添加到JFrame
        ImagePanel imagePanel = new ImagePanel();
        frame.getContentPane().add(imagePanel);

        // 设置窗口大小
        frame.setSize(100, 100);
        frame.setVisible(true);
    }

    // 自定义JPanel用于绘制图片
    static class ImagePanel extends JComponent {
        private Image image;

        public ImagePanel() throws MalformedURLException {
            // 加载图片
            URL resourceUrl = ImagePanel.class.getResource("/image.png");
            resourceUrl = new URL("http://localhost:3000/image.jpg");
            image = new ImageIcon(resourceUrl).getImage();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // 绘制图片（可以自定义位置和大小）
            if (image != null) {
                // 方式1：原始大小绘制
                // g.drawImage(image, 0, 0, this);

                // 方式2：缩放绘制以适应面板
                g.drawImage(image, 0, 0, getWidth(), getHeight(), this);

                // 方式3：指定位置和大小绘制
                // g.drawImage(image, 50, 50, 300, 200, this);
            }
        }
    }
}