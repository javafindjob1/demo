package com.xi4;

public class Demage {

  public double demage(double s, double r) {
    double d = s * r;
    if (d > 1.0) {
      return d;
    } else if (r > 1.0) {
      return d;
    } else if (s > 1.0) {
      return 1.0;
    } else {
      return s;
    }
  }

  public double calcOne(double s, double... rs) {
    double ra = 1.0;
    for (double r : rs) {
      ra *= r;
    }
    return demage(s, ra);
  }

  public double calcAll(double s, double... rs) {
    double d = s;
    for (double r : rs) {
      d = demage(d, r);
    }
    return d;
  }

  public void test(double s, double... rs) {
    double r1 = calcOne(s*0.7, rs);
    System.out.println(s + "," + r1 );
  }

  public static void main(String[] args) {
    Demage demage = new Demage();
    // 魔法攻击，水甲50
    // demage.test(0.55, 1.3, 1.0, 0.5);
    // demage.test(1.35, 1.3, 1.0, 0.5);
    // demage.test(1.45, 1.3, 1.0, 0.5);
    // demage.test(2.15, 1.3, 1.0, 0.5);
    // demage.test(2.25, 1.3, 1.0, 0.5);

    // // 魔法攻击，暗甲
    demage.test(0.55, 1.0, 1.3, 1.71);
    demage.test(1.35, 1.0, 1.3, 1.71);
    demage.test(1.45, 1.0, 1.3, 1.71);
    demage.test(2.15, 1.0, 1.3, 1.71);
    demage.test(2.25, 1.0, 1.3, 1.71);

  }
}
