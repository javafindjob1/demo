package com.abc.fun;

import java.util.HashMap;
import java.util.Map;

public class RandomRateManager {
  Map<String, RandomRate> randomRateMap = new HashMap<>();
  RandomRate current;
  private double rate = 1.0;

  private static Map<String,Integer[]> map = new HashMap<>();
  public void create(String name, int max, int j) {
    RandomRate randomRate = new RandomRate(name, max, j);
    randomRateMap.put(name, randomRate);
    map.put(name, new Integer[]{max,j});
  }

  public void updateIfLoad(int j, String name, String fuhao, int k, String fuhao2, int k2) {
    if(this.current==null){
      this.current = randomRateMap.get(name);
      if(this.current==null){
        // 使用了一个之前的种子
        Integer[] arr = map.get(name);
        RandomRate randomRate = new RandomRate(name, arr[0], arr[1]);
        randomRateMap.put(name, randomRate);
        this.current = randomRate;
      }
      // 重新调整层数
      this.current.setJ(j-1);
    }else{
      RandomRate tmp = randomRateMap.get(name);
      if(tmp == this.current){

      }else{
        tmp.setPre(this.current);
        this.current.setNext(tmp);
        this.current = tmp;
        
        // 形成父子关系调整层数
        this.current.setJ(tmp.getPre().getJ()+1);
      }
    }
    this.rate = this.current.add(fuhao, k, fuhao2, k2);
  }
  public void updateElse(int j) {
    if(this.current == null){

    }else{
      this.rate = this.current.calcRest();
    }
  }
  public void updateEndif(int j) {
    if(this.current == null){

    }else if (j == this.current.getJ()) {
      this.current = this.current.getPre();
      if(this.current==null){
        this.rate = 1.0;
      }else{
        this.rate = this.current.getRate();
      }
    }
  }

  public double getRate() {
    return this.rate;
  }
}
