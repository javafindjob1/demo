package com.chess.busi.man;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.chess.busi.Man;
import com.chess.busi.Position;

public class ManZ1  extends Man {

  @Override
  public List<Position> bl() {
    int x = getPos().getX();
    int y = getPos().getY();
    String[][] map = desk.getMap();
    Map<String,Man> mans = desk.getMans();
    int my = getMy();

    List<Position> d = new ArrayList<>();
		//上
		if (y - 1 >= 0 && (map[y - 1][x]==null || map(y - 1,x).getMy() != my)) d.add(new Position(x, y - 1));
		//右
		if (x + 1 <= 8 && y <= 4 && (map[y][x + 1]==null || map(y,x + 1).getMy() != my)) d.add(new Position(x + 1, y));
		//左
		if (x - 1 >= 0 && y <= 4 && (map[y][x - 1]==null || map(y,x - 1).getMy() != my)) d.add(new Position(x - 1, y));
		
		return d;
  }
}