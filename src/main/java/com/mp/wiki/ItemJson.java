package com.mp.wiki;

import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.Data;

@Data
public class ItemJson {
  private String _hjschema = "物品";
  private String type;
  private List<String> label;
  private String level;
  private int levelVal;
  private String name;
  private String prop;
  private String prop2;
  private List<String> changeLog;
  private String getFrom;
  private Formula formula;
  private String mark;
  @JSONField(name = "ID")
  private String ID;

  @Data
  public static class Formula {
    private List<Group> group;
    private boolean hasJuanzhou;
    private String seller;
  }

  @Data
  public static class Group {
    private String name;
    private int num = 1;
  }
}
