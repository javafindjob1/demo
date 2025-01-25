package com.chess.busi.man;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.chess.busi.Man;
import com.chess.busi.Position;

public class ManX1  extends Man {

  @Override
  public List<Position> bl() {
    int x = getPos().getX();
    int y = getPos().getY();
    String[][] map = desk.getMap();
    Map<String,Man> mans = desk.getMans();
    int my = getMy();

    List<Position> d = new ArrayList<>();
		//4点半
		if (y + 2 <= 9 && x + 2 <= 8 && map[y + 1][x + 1] ==null && (map[y + 2][x + 2]==null || map(y + 2,x + 2).getMy() != my)) d.add(new Position(x + 2, y + 2));
		//7点半this.com.
		if (y + 2 <= 9 && x - 2 >= 0 && map[y + 1][x - 1] ==null && (map[y + 2][x - 2]==null || map(y + 2,x - 2).getMy() != my)) d.add(new Position(x - 2, y + 2));
		//1点半this.com.
		if (y - 2 >= 5 && x + 2 <= 8 && map[y - 1][x + 1] ==null && (map[y - 2][x + 2]==null || map(y - 2,x + 2).getMy() != my)) d.add(new Position(x + 2, y - 2));
		//10点半this.com.
		if (y - 2 >= 5 && x - 2 >= 0 && map[y - 1][x - 1] ==null && (map[y - 2][x - 2]==null || map(y - 2,x - 2).getMy() != my)) d.add(new Position(x - 2, y - 2));
		
		return d;
	}
}
