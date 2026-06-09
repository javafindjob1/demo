package com.mp.sqlite;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;

public class ImageLoadedState implements State {

  private Icon imageIcon;

  public ImageLoadedState() {
    
  }

  public void setImageIcon(Icon imageIcon) {
    this.imageIcon = imageIcon;
  }

  @Override
  public void paintIcon(Component c, Graphics g, int x, int y) {
    imageIcon.paintIcon(c, g, x, y);;
  }

  @Override
  public int getIconWidth() {
    return imageIcon.getIconWidth();
  }

  @Override
  public int getIconHeight() {
    return imageIcon.getIconHeight();
  }

}
