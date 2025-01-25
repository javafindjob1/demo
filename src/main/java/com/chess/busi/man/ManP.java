package com.chess.busi.man;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.chess.busi.Man;
import com.chess.busi.Position;

public class ManP extends Man {

  @Override
  public List<Position> bl() {
    int x = getPos().getX();
    int y = getPos().getY();
    String[][] map = desk.getMap();
    Map<String,Man> mans = desk.getMans();
    int my = getMy();

    List<Position> d = new ArrayList<>();
    //左侧检索
		int n = 0;
		for (int i = x - 1; i >= 0; i--) {
			if (map[y][i]!=null) {
				if (n == 0) {
					n++;
					continue;
				} else {
					if (map(y,i).getMy() != my) d.add(new Position(i, y));
					break;
				}
			} else {
				if (n == 0) d.add(new Position(i, y));
			}
		}
		//右侧检索
		n = 0;
		for (int i = x + 1; i <= 8; i++) {
			if (map[y][i]!=null) {
				if (n == 0) {
					n++;
					continue;
				} else {
					if (map(y,i).getMy() != my) d.add(new Position(i, y));
					break;
				}
			} else {
				if (n == 0) d.add(new Position(i, y));
			}
		}
		//上检索
		n = 0;
		for (int i = y - 1; i >= 0; i--) {
			if (map[i][x]!=null) {
				if (n == 0) {
					n++;
					continue;
				} else {
					if (map(i,x).getMy() != my) d.add(new Position(x, i));
					break;
				}
			} else {
				if (n == 0) d.add(new Position(x, i));
			}
		}
		//下检索
		n = 0;
		for (int i = y + 1; i <= 9; i++) {
			if (map[i][x]!=null) {
				if (n == 0) {
					n++;
					continue;
				} else {
					if (map(i,x).getMy() != my) d.add(new Position(x, i));
					break;
				}
			} else {
				if (n == 0) d.add(new Position(x, i));
			}
		}
		return d;
	}

}
