package com.chess.busi.man;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.chess.busi.Man;
import com.chess.busi.Position;


//车
public class ManC extends Man {
	@Override
	public String toString(){
		return text;
	}
  @Override
  public List<Position> bl() {
    int x = getPos().getX();
    int y = getPos().getY();
    String[][] map = desk.getMap();
    Map<String,Man> mans = desk.getMans();
    int my = getMy();

    List<Position> d = new ArrayList<>();
    // 左侧检索
    for (int i = x - 1; i >= 0; i--) {
      if (map[y][i] != null) {
        if (map(y,i).getMy() != my)
          d.add(new Position(i, y));
        break;
      } else {
        d.add(new Position(i, y));
      }
    }
    // 右侧检索
    for (int i = x + 1; i <= 8; i++) {
      if (map[y][i] != null) {
        if (map(y,i).getMy() != my)
          d.add(new Position(i, y));
        break;
      } else {
        d.add(new Position(i, y));
      }
    }
    // 上检索
    for (int i = y - 1; i >= 0; i--) {
      if (map[i][x] != null) {
        if (map(i,x).getMy() != my)
          d.add(new Position(x, i));
        break;
      } else {
        d.add(new Position(x, i));
      }
    }
    // 下检索
    for (int i = y + 1; i <= 9; i++) {
      if (map[i][x] != null) {
        if (map(i,x).getMy() != my)
          d.add(new Position(x, i));
        break;
      } else {
        d.add(new Position(x, i));
      }
    }
    return d;
  }

}
