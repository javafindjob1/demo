package com.mp.sqlite;

import java.awt.Component;
import java.awt.Graphics;
import java.net.URL;

import javax.swing.ImageIcon;

public class ImageNotLoadedState implements State {
  boolean retrieving = false;
  ImageProxy imageProxy;

  URL url;

  public ImageNotLoadedState(ImageProxy imageProxy, URL url) {
    this.imageProxy = imageProxy;
    this.url = url;
  }

  @Override
  public void paintIcon(Component c, Graphics g, int x, int y) {
    g.drawString("Loding CD cover, pleaser wait...", x + 300, y + 190);

    if (retrieving == false) {
      retrieving = true;
      new Thread(() -> {
        ((ImageLoadedState) imageProxy.imageProxy2).setImageIcon(new ImageIcon(url, "CD Cover"));
        imageProxy.state = imageProxy.imageProxy2;
        c.repaint();
      }).start();
    }

  }

  @Override
  public int getIconWidth() {
    return 800;
  }

  @Override
  public int getIconHeight() {
    return 600;

  }

}
