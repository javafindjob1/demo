package com.abc;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Function {
  private List<String> rows = new ArrayList<>();

  public void addRow(String row) {
    rows.add(row);
  }
}
