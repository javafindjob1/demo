package com.abc;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public abstract class Ini {
  protected String _parent; // = "AEfk"
  protected Map<String, String> others;

  public void set(String key, String value) {
    try{
      Field f = this.getClass().getDeclaredField(key);
      f.setAccessible(true);
      f.set(this, value);
    }catch(NoSuchFieldException | IllegalAccessException e){
      Map<String, String> others = getOthers();
      if (others == null) {
        others = new HashMap<>();
        this.setOthers(others);
      }
      others.put(key, value);
    }

  }
}
