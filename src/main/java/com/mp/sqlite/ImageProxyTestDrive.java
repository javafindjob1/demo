package com.mp.sqlite;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.awt.*;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import java.awt.event.*;

public class ImageProxyTestDrive {
  ImageComponent imageComponent;
  JFrame frame = new JFrame("CD Cover Viewer");

  JMenuBar menuBar;
  JMenu menu;

  Hashtable<String, String> cds = new Hashtable<>();

  public ImageProxyTestDrive() throws MalformedURLException {
    cds.put("1111", "http://localhost:3000/image.png");
    cds.put("2222", "http://localhost:3000/image.jpg");
    URL initialURL = new URL("http://localhost:3000/image.png");
    menuBar = new JMenuBar();
    frame.setJMenuBar(menuBar);
    menu = new JMenu("Favorite CDS");
    menuBar.add(menu);

    for (Enumeration<String> e = cds.keys(); e.hasMoreElements();) {
      String name = e.nextElement();
      JMenuItem menuItem = new JMenuItem(name);
      menu.add(menuItem);

      menuItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent event) {
          try {
            imageComponent.setIcon(new ImageProxy(new URL(cds.get(event.getActionCommand()))));
            imageComponent.repaint();
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      });
    }

    Icon icon = new ImageProxy(initialURL);
    imageComponent = new ImageComponent(icon);

    frame.getContentPane().add(imageComponent);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(800, 600);
    frame.setVisible(true);

  }

  public static void main(String[] args) throws Exception {
    new ImageProxyTestDrive();
  }

  static class ImagePanel extends JComponent {
    private Image image;

    public ImagePanel() {
      // 加载图片
      URL resourceUrl = ImagePanel.class.getResource("/image.png");
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
