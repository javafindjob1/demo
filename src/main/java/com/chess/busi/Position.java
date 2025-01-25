package com.chess.busi;

import lombok.Data;

/**
 * 棋子在棋盘中的坐标
 */
@Data
public class Position {
  private int x;
  private int y;

  public Position(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public boolean equals(Object x) {
    if (x instanceof Position) {
      Position p = (Position) x;
      if (p.x == this.x && p.y == this.y) {
        return true;
      }
    }
    return false;
  }

  public int hashCode() {
    return 100 + x + y;
  }
}
