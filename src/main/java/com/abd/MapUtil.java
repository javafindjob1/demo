package com.abd;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class MapUtil {

  public static <K, V> V getNotNull(Map<K, V> map, K key, Supplier<V> supplier) {
    V value = map.get(key);
    if (value == null) {
      value = supplier.get();

      // Class<?> supplierClass = supplier.getClass();
      // String supplierClassName = supplierClass.getName();
      // System.out.println("Supplier implementation class name: " + supplierClassName + " 地址:" + supplier);

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
  }

}