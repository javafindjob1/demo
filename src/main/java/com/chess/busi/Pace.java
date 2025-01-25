package com.chess.busi;

import com.alibaba.fastjson.JSON;

import lombok.Data;

@Data
public class Pace {
  /** 第几手 */
  private int index;
  private Position pos;
  private Position newPos;

  public Pace(String str) {
    String[] strs = str.split("");
    this.pos = new Position(Integer.parseInt(strs[0]), Integer.parseInt(strs[1]));
    this.newPos = new Position(Integer.parseInt(strs[2]), Integer.parseInt(strs[3]));
  }

  public static void main(String[] args) {
    String a = "6633";
    System.out.println(a.split("")[0]);
    System.out.println(a.split("")[3]);

            Desk req = JSON.parseObject("1234", Desk.class);
            System.out.println(req.getDeskId());

  }
}
