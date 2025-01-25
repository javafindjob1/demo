package com.chess.busi.man;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.chess.busi.Man;
import com.chess.busi.Position;

public class ManJ extends Man {

  private boolean isNull(Map<String,Man> mans, String[][] map){
    int y1 = mans.get("j0").getPos().getY();
    int x1 = mans.get("J0").getPos().getX();
    int y2 = mans.get("J0").getPos().getY();
    for (int i = y1 - 1; i > y2; i--) {
      if (map[i][x1] == null) return false;
    }
    return true;
  }
  @Override
  public List<Position> bl() {
    int x = getPos().getX();
    int y = getPos().getY();
    String[][] map = desk.getMap();
    Map<String,Man> mans = desk.getMans();
    int my = getMy();

    List<Position> d = new ArrayList<>();
		boolean isNull = isNull(mans, map);
    
    //下
    if (y + 1 <= 2 && (map[y + 1][x]==null || map(y + 1,x).getMy() != my)) d.add(new Position(x, y + 1));
    //上
    if (y - 1 >= 0 && (map[y - 1][x]==null || map(y - 1,x).getMy() != my)) d.add(new Position(x, y - 1));
    //老将对老将的情况
    if (mans.get("j0").getPos().getX() == mans.get("J0").getPos().getX() && isNull) d.add(new Position(mans.get("j0").getPos().getX(), mans.get("j0").getPos().getY()));
		
    //右
		if (x + 1 <= 5 && (map[y][x + 1] ==null || map(y,x + 1).getMy() != my)) d.add(new Position(x + 1, y));
		//左
		if (x - 1 >= 3 && (map[y][x - 1] ==null || map(y,x - 1).getMy() != my)) d.add(new Position(x - 1, y));
		return d;
	}
}
