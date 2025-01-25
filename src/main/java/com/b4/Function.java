package com.b4;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Function {
  private String name;
  private List<String> rows = new ArrayList<>();

  public void addRow(String row) {
    rows.add(row);
  }
}
