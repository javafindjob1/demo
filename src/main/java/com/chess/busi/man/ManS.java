package com.chess.busi.man;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.chess.busi.Man;
import com.chess.busi.Position;

public class ManS extends Man {

  @Override
  public List<Position> bl() {
    int x = getPos().getX();
    int y = getPos().getY();
    String[][] map = desk.getMap();
    Map<String,Man> mans = desk.getMans();
    int my = getMy();

    List<Position> d = new ArrayList<>();
    
    //4点半
    if (y + 1 <= 2 && x + 1 <= 5 && (map[y + 1][x + 1] == null || map(y + 1,x + 1).getMy() != my)) d.add(new Position(x + 1, y + 1));
    //7点半
    if (y + 1 <= 2 && x - 1 >= 3 && (map[y + 1][x - 1] == null || map(y + 1,x - 1).getMy() != my)) d.add(new Position(x - 1, y + 1));
    //1点半
    if (y - 1 >= 0 && x + 1 <= 5 && (map[y - 1][x + 1] == null || map(y - 1,x + 1).getMy() != my)) d.add(new Position(x + 1, y - 1));
    //10点半
    if (y - 1 >= 0 && x - 1 >= 3 && (map[y - 1][x - 1] == null || map(y - 1,x - 1).getMy() != my)) d.add(new Position(x - 1, y - 1));
		return d;

	}

}
