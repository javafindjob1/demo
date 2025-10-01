package com.x4;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class MapUtil {
  private static Supplier ss;

  public static <K, V> V getNotNull(Map<K, V> map, K key, Supplier<V> supplier) {
    V value = map.get(key);
    if (value == null) {
      value = supplier.get();

      // 测试supplier
      // Class<?> supplierClass = supplier.getClass();
      // String supplierClassName = supplierClass.getName();
      // System.out.println("Supplier implementation class name: " + supplierClassName + " 地址:" + supplier);

      // if (ss == null) {
      //   ss = supplier;
      // } else {
      //   System.out.println("比较supplier:" + (ss == supplier));
      // }

      map.put(key, value);
    }
    return value;
  }

  public static void main(String[] args) {
    Map<String, String> map = new HashMap<>();
    for (int i = 0; i < 2; i++) {
      String key = i + "";
      MapUtil.getNotNull(map, key, () -> new String(System.currentTimeMillis() + ""));
    }

    for (int i = 2; i < 4; i++) {
      String key = i + "";
      MapUtil.getNotNull(map, key, () -> new String(System.currentTimeMillis() + ""));
    }
    System.out.println(new Object());
    System.out.println(new Object());
  }

}