package com.mp.wiki;

public abstract class Csv {
  public static String format(String str) {
    if (str == null) {
      return "";
    }
    str = str.replaceAll("\t", " ");
    str = str.replaceAll(",", "，");
    str = str.replaceAll(";", "；");
    return str.trim();
  }
}
