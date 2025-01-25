package com.abc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class TestLock {

  public static void main(String[] args) throws IOException {
    InputStream file = UnitParse.class.getResourceAsStream("unitDrop.txt");
    try(
      BufferedReader bufferedReader = new BufferedReader(
            new InputStreamReader(file, StandardCharsets.UTF_8))
    ){

    }
    System.in.read();
  }
}
