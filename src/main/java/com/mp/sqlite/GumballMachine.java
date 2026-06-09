package com.mp.sqlite;

public class GumballMachine {
  final static int SOLD_OUT = 0;
  final static int NO_QUARTER = 1;
  final static int HAS_QUARTER = 2;
  final static int SOLD = 3;

  int state = SOLD_OUT;
  int count = 0;

  public GumballMachine(int count) {
    this.count = count;
    if (count > 0) {
      state = NO_QUARTER;
    }
  }

  public void insertQuarter() {
    // 插入硬币
  }

  public void ejectQuarter() {
    // 退出硬币
  }

  public void turnCrank() {
    // 转动曲柄
  }

  public void dispense() {
    // 释放糖果
    new Thread(new Runnable() {

      @Override
      public void run() {
        count = 13;
      }

    });
  }

  public static void main(String[] args) {

  }
}
