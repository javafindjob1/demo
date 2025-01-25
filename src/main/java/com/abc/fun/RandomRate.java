package com.abc.fun;

import static org.junit.Assert.assertTrue;

import lombok.Data;

@Data
public class RandomRate {
  private RandomRate pre;
  private RandomRate next;

  /** 种子的名字 */
  private String name;
  /** if的控制范围 */
  private int j;
  /** 种子最大值 */
  private int max;
  /** 种子值列表 从1开始 0:未占用,1:占用 */
  private int[] values;
  /** 当前概率 */
  private double rate;

  public RandomRate(String name, int max, int j) {
    this.name = name;
    this.max = max;
    this.j = j;
    this.values = new int[max + 1];
    this.rate = 1.0;
  }

  public double getRate() {
    return this.rate;
  }

  public int getJ() {
    return this.j;
  }

  public double calcRest() {
    int n = 0;
    for (int i = 1; i <= max; i++) {
      if (values[i] == 0) {
        n++;
      }
    }
    rate = n * 1.0 / max;// 使用上级的概率乘
    if (pre != null) {
      rate = pre.getRate() * rate;
    }
    return rate;
  }

  public double add(String fuhao, int k) {
    int n = 0;
    switch (fuhao) {
      case "==":
        values[k] = 1;
        n = 1;
        break;
      case "<=":
          for (int i = Math.min(k, max); i >= 1; i--) {
            if (values[i] == 0) {
              n++;
              values[i] = 1;
            }
          }
        break;
      case ">=":
          for (int i = k; i <= max; i++) {
            if (values[i] == 0) {
              n++;
              values[i] = 1;
            }
          }
        break;
      default:
        assertTrue("不支持的符号:" + fuhao, true);
        break;
    }

    rate = n * 1.0 / max;

    // 使用上级的概率乘
    if (pre != null) {
      rate = pre.getRate() * rate;
    }
    return rate;
  }

  public double add(String fuhao, int k, String fuhao2, int k2) {
    if(k>=values.length){
      return 0;
    }
    int n = 0;
    switch (fuhao) {
      case "==":
        values[k] = 1;
        n = 1;
        break;
      case "<=":
        if (fuhao2 == null) {
          for (int i = Math.min(k, max); i >= 1; i--) {
            if (values[i] == 0) {
              n++;
              values[i] = 1;
            }
          }
        } else {
          for (int i = Math.min(k, max); i >= k2; i--) {
            if (values[i] == 0) {
              n++;
              values[i] = 1;
            }
          }
        }
        break;
      case ">=":
        if (fuhao2 == null) {
          for (int i = k; i <= max; i++) {
            if (values[i] == 0) {
              n++;
              values[i] = 1;
            }
          }
        } else {
          for (int i = k; i <= k2; i++) {
            if (values[i] == 0) {
              n++;
              values[i] = 1;
            }
          }
        }

        break;
      default:
        assertTrue("不支持的符号:" + fuhao, true);
        break;
    }

    rate = n * 1.0 / max;

    // 使用上级的概率乘
    if (pre != null) {
      rate = pre.getRate() * rate;
    }
    return rate;
  }
}
