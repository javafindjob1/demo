package com.mp.wiki;

import java.util.ArrayList;
import java.util.List;

import com.mp.parse.AbilityDetail;

import lombok.Data;

@Data
public class HeroJson {
  private int sortVal;
  private String _hjschema = "英雄";
  private String type = "英雄";
  private String role;
  private String primary;
  private String intro;
  private String guangjing;
  private String lingge;
  private List<Hero> heros = new ArrayList<>();

  @Data
  public static class Hero {
    private String name;
    private String propernames;
    /** 皮肤强化 */
    private String buff;
    private String atkType1;
    private double cool1;
    private int spd;
    private List<Ability> abils = new ArrayList<>();
    private List<String> items = new ArrayList<>();
  }

  @Data
  public static class Ability {
    private String name;
    private String hotKey;
    private String prop;
    private String mark;

    public Ability(AbilityDetail d) {
      this.name = d.getName();
      this.hotKey = d.getHotkey();
      if(d.getUnubertip()!=null && d.getUnubertip().trim().length()>0) {
        this.prop = d.getUnubertip();
      }else{
        this.prop = d.getUbertip();
      }
      this.mark = d.getMark();
    }
  }
}
