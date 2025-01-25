package com.atiguigu;


public class Test {
  public static void main(String[] args) {
    String s =  "-1";
    int[] chars = s.chars().toArray();

    int result = 0;
    for (int i = 0; i < chars.length; i++) {
      int chars2 = chars[i];
      int base = chars2 - 48;
      int q = chars.length-i;
      int qr = 1;
      for (int j = 1; j < q; j++) {
        qr = qr * 10;
      }

      result += base * qr;
    }
    System.out.println(result);


  }
  
}
