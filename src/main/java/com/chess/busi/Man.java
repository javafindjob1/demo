package com.chess.busi;

import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.Data;

@Data
public abstract class Man {
	@JSONField(serialize = false)
	protected Desk desk;
	
	protected String text;
	protected Position pos;
	protected int my;

	@Override
	public String toString(){
		return text;
	}

	public void move(int x, int y){
		this.pos = new Position(x, y);
	}

	public Man map(int y, int x){
		return desk.getMans().get(desk.getMap()[y][x]);
	}
	// 棋子们
	// private static args = {
	// //红子 中文/图片地址/阵营/权重
	// 'c': { text: "车", img: 'r_c', my: 1, bl: "c"},
	// 'm': { text: "马", img: 'r_m', my: 1, bl: "m"},
	// 'x': { text: "相", img: 'r_x', my: 1, bl: "x"},
	// 's': { text: "仕", img: 'r_s', my: 1, bl: "s"},
	// 'j': { text: "帅", img: 'r_j', my: 1, bl: "j"},
	// 'p': { text: "炮", img: 'r_p', my: 1, bl: "p"},
	// 'z': { text: "兵", img: 'r_z', my: 1, bl: "z"},

	// //蓝子
	// 'C': { text: "車", img: 'b_c', my: -1, bl: "c"},
	// 'M': { text: "馬", img: 'b_m', my: -1, bl: "m"},
	// 'X': { text: "象", img: 'b_x', my: -1, bl: "x"},
	// 'S': { text: "士", img: 'b_s', my: -1, bl: "s"},
	// 'J': { text: "将", img: 'b_j', my: -1, bl: "j"},
	// 'P': { text: "炮", img: 'b_p', my: -1, bl: "p"},
	// 'Z': { text: "卒", img: 'b_z', my: -1, bl: "z"}
	// }

	public abstract List<Position> bl();

	public void move(Position newPos) {
		this.pos = newPos;
	}

	public boolean indexOfPs(Position newPos) {
		List<Position> ps = bl();
		return ps.contains(newPos);
	}
}
