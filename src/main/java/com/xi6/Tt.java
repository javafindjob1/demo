package com.xi6;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Tt {
  public static void main(String[] args) {
    System.out.println(new Date());
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd ");
    String time = LocalDateTime.now().format(dtf);
    System.out.println(time);
  }
}
