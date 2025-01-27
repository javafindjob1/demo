package com.chess.busi;

import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;

import io.netty.util.concurrent.ScheduledFuture;
import lombok.Data;

@Data
public class Desk {

  /** 桌子id */
  private String deskId;
  /** 桌子名称 */
  private String deskName;

  /** 座位是否有人 */
  private User[] seat;
  /** 选手身份标识channelId */
  private Map<String, Integer> players;

  /** 走的最后一步棋的信息， 表示第index手棋，红色【黑色】走了6633这步棋 */
  private Pace lastPace;

  @JSONField(serialize = false)
  private ScheduledFuture<?> timeoutSchedule;
  /** 棋子信息 */
  @JSONField(serialize = false)
  private Map<String, Man> mans;
  /** 实时盘面 */
  private String[][] map;
  /** 走棋记录 */
  private List<String> pace;

  public Desk() {
    System.out.println("desk new");
    restart();
  }

  public void restart() {
    seat = new User[2];
    players = new HashMap<>();
    lastPace = new Pace();

    pace = new ArrayList<>();
    mans = new HashMap<>();

    map = new String[][] {
        { "C0", "M0", "X0", "S0", "J0", "S1", "X1", "M1", "C1" },
        { null, null, null, null, null, null, null, null, null },
        { null, "P0", null, null, null, null, null, "P1", null },
        { "Z0", null, "Z1", null, "Z2", null, "Z3", null, "Z4" },
        { null, null, null, null, null, null, null, null, null },
        { null, null, null, null, null, null, null, null, null },
        { "z0", null, "z1", null, "z2", null, "z3", null, "z4" },
        { null, "p0", null, null, null, null, null, "p1", null },
        { null, null, null, null, null, null, null, null, null },
        { "c0", "m0", "x0", "s0", "j0", "s1", "x1", "m1", "c1" }
    };

    for (int y = 0; y < 10; y++) {
      for (int x = 0; x < 9; x++) {
        String key = map[y][x];
        if (key != null) {
          String className;
          char name = key.charAt(0);
          int my = 1;
          if ('a' <= name && 'z' >= name) {
            // 红棋
            className = "com.chess.busi.man.Man" + String.valueOf(name).toUpperCase() + "1";
          } else {
            my = -1;
            className = "com.chess.busi.man.Man" + String.valueOf(name);
          }
          try {
            // 1. 获取 Desk 类的 Class 对象
            Class<?> deskClass = Class.forName(className);
            // 2. 创建 Desk 类的实例
            Man instance = (Man) deskClass.getDeclaredConstructor().newInstance();
            instance.setText(key);
            instance.setPos(new Position(x, y));
            instance.setMy(my);
            instance.setDesk(this);
            mans.put(key, instance);
          } catch (Exception e) {
            System.out.println("new 棋子失败:" + key);
            e.printStackTrace();
          }
        }
      }
    }
  }

  public Man getManByPos(Position pos) {
    return mans.get(map[pos.getY()][pos.getX()]);
  }

  public void move(Pace pace) {
    Position pos = pace.getPos();
    Position newPos = pace.getNewPos();

    Man key = getManByPos(pos);
    map[pos.getY()][pos.getX()] = null;
    map[newPos.getY()][newPos.getX()] = key.getText();
    key.move(newPos);

    this.getPace().add(pace.getPace());
    
    lastPace.setIndex(this.pace.size());
    lastPace.setPace(pace.getPace());
    lastPace.setManKey(key.getText());
    lastPace.setUpdateTime(new Date().getTime());
  }

  public boolean sitDown(int my, User player) {
    this.players.put(player.getId(), my);
    if (my == 1 && seat[1] == null) {
      seat[1] = player;
      return true;
    } else if (my == -1 && seat[0] == null) {
      seat[0] = player;
      return true;
    }

    return false;
  }

  public boolean play() {
    if (seat[0] != null && seat[1] != null) {
      return true;
    }
    return false;
  }

  public void regret() {
    // 在红棋的回合内可以悔棋
    // 在黑棋的回合内可以悔棋
    for (int i = 0; i < 2; i++) {
      int index = this.pace.size() - 1;
      String pace = this.pace.remove(index);

      System.out.println("退回 " + pace + ",第 " + index + "手");
      String[] arr = pace.split("");

      String key = this.map[Integer.parseInt(arr[3])][Integer.parseInt(arr[2])];
      this.map[Integer.parseInt(arr[1])][Integer.parseInt(arr[0])] = this.map[Integer.parseInt(arr[3])][Integer
          .parseInt(arr[2])];
      this.mans.get(key).move(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]));
      if (pace.length() > 4) {
        String eatedMan = pace.substring(4);
        this.map[Integer.parseInt(arr[3])][Integer.parseInt(arr[2])] = eatedMan;
      } else {
        this.map[Integer.parseInt(arr[3])][Integer.parseInt(arr[2])] = null;
      }

      // 重新记录最后一步棋的信息
      String lastManKey = null;
      String lastPace = null;
      if (index > 0) {
        lastPace = pace;
        lastManKey = pace.substring(4);
      }
      this.lastPace.setIndex(index);
      this.lastPace.setPace(lastPace);
      this.lastPace.setManKey(lastManKey);
      this.lastPace.setUpdateTime(new Date().getTime());
    }

  }

  enum DeskState {
    STARTING, READYING, PLAYING, ENDING
  }

  public static void main(String[] args) {
    Desk s = new Desk();
    System.out.println(JSON.toJSONString(s));
  }
}
