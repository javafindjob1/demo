package com.mp.sqlite;

import java.awt.Component;
import java.awt.Graphics;
import java.net.URL;

import javax.swing.Icon;

public class ImageProxy implements Icon {

  State imageProxy1;
  State imageProxy2;

  State state;


  public ImageProxy(URL url) {
    imageProxy1 = new ImageNotLoadedState(this, url);
    imageProxy2 = new ImageLoadedState();

    state = imageProxy1;
  }

  @Override
  public void paintIcon(Component c, Graphics g, int x, int y) {
    state.paintIcon(c, g, x, y);
  }

  @Override
  public int getIconWidth() {
    return state.getIconWidth();
  }

  @Override
  public int getIconHeight() {
    return state.getIconHeight();
  }

}
