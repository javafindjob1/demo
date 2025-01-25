package com.chess.busi.man;

import java.util.ArrayList;
import java.util.List;

import com.chess.busi.Man;
import com.chess.busi.Position;

public class ManM  extends Man {

  @Override
  public List<Position> bl() {
    int x = getPos().getX();
    int y = getPos().getY();
    String[][] map = desk.getMap();
    int my = getMy();

    List<Position> d = new ArrayList<>();
    //1点
		if (y - 2 >= 0 && x + 1 <= 8 && map[y - 1][x] ==null && (map[y - 2][x + 1] == null || map(y - 2,x + 1).getMy() != my)) d.add(new Position(x + 1, y - 2));
		//2点
		if (y - 1 >= 0 && x + 2 <= 8 && map[y][x + 1] ==null && (map[y - 1][x + 2] == null || map(y - 1,x + 2).getMy() != my)) d.add(new Position(x + 2, y - 1));
		//4点
		if (y + 1 <= 9 && x + 2 <= 8 && map[y][x + 1] ==null && (map[y + 1][x + 2] == null || map(y + 1,x + 2).getMy() != my)) d.add(new Position(x + 2, y + 1));
		//5点
		if (y + 2 <= 9 && x + 1 <= 8 && map[y + 1][x] ==null && (map[y + 2][x + 1] == null || map(y + 2,x + 1).getMy() != my)) d.add(new Position(x + 1, y + 2));
		//7点
		if (y + 2 <= 9 && x - 1 >= 0 && map[y + 1][x] ==null && (map[y + 2][x - 1] == null || map(y + 2,x - 1).getMy() != my)) d.add(new Position(x - 1, y + 2));
		//8点
		if (y + 1 <= 9 && x - 2 >= 0 && map[y][x - 1] ==null && (map[y + 1][x - 2] == null || map(y + 1,x - 2).getMy() != my)) d.add(new Position(x - 2, y + 1));
		//10点
		if (y - 1 >= 0 && x - 2 >= 0 && map[y][x - 1] ==null && (map[y - 1][x - 2] == null || map(y - 1,x - 2).getMy() != my)) d.add(new Position(x - 2, y - 1));
		//11点
		if (y - 2 >= 0 && x - 1 >= 0 && map[y - 1][x] ==null && (map[y - 2][x - 1] == null || map(y - 2,x - 1).getMy() != my)) d.add(new Position(x - 1, y - 2));

		return d;
	}
}
