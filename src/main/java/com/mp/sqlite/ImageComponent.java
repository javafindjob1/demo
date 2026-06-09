package com.mp.sqlite;

import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.JComponent;

public class ImageComponent extends JComponent {
  private Icon icon;

  public ImageComponent(Icon icon) {
    this.icon = icon;
  }

  public void setIcon(Icon icon) {
    this.icon = icon;
  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);

    int iconWidth = icon.getIconWidth();
    int iconHeight = icon.getIconHeight();
    int componentWidth = getWidth();
    int componentHeight = getHeight();

    // 计算缩放比例，保持宽高比
    double scaleX = (double) componentWidth / iconWidth;
    double scaleY = (double) componentHeight / iconHeight;
    double scale = Math.min(scaleX, scaleY);

    // 计算缩放后的图标尺寸
    int scaledWidth = (int) (iconWidth * scale);
    int scaledHeight = (int) (iconHeight * scale);

    // 计算居中位置
    int x = (componentWidth - scaledWidth) / 2;
    int y = (componentHeight - scaledHeight) / 2;

    // 使用Graphics2D进行缩放绘制
    if (g instanceof java.awt.Graphics2D) {
      java.awt.Graphics2D g2d = (java.awt.Graphics2D) g;
      g2d.scale(scale, scale);
      icon.paintIcon(this, g2d, (int) (x / scale), (int) (y / scale));
      // 恢复原始缩放比例
      g2d.scale(1.0 / scale, 1.0 / scale);
    } else {
      // 如果不支持Graphics2D，使用默认绘制
      icon.paintIcon(this, g, x, y);
    }
  }

}
