package com.mp.parse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.common.parse.DropInfo;
import com.common.parse.ItemAccessories;
import com.mp.function.FunctionTaozhuang;
import com.mp.function.hero.Hero;

import lombok.Data;

@Data
public class FunctionDetail {
  private List<String> rows;
  private String name;

  private Map<String, List<DropInfo>> itemMap = new HashMap<>();
  private List<DropInfo> waJueList = new ArrayList<>();
  private Map<String, List<DropInfo>> juanzhouMap = new HashMap<>();
  private List<FunctionTaozhuang> taozhuangList = new ArrayList<>();
  private Map<String, List<ItemAccessories>> newItemFormulaMap = new HashMap<>();

  private List<Hero> heroList = new ArrayList<>();

}
