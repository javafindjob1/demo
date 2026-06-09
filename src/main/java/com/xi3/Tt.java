package com.xi3;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tt {
  public static void main(String[] args) {

    Pattern p = Pattern.compile("\\|cff\\w{3,6}(.+)\\|r", Pattern.CASE_INSENSITIVE);
    Matcher matcher = p.matcher("【|cff9900ff血魔刻印|r】合成卷轴");
    System.out.println(matcher.find());
  }
}
