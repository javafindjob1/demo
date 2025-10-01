package com.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

import org.apache.poi.ss.formula.functions.T;

public class ObjectUtil {
  public static <T extends Serializable> List<T> deepCopy(List<T> src) {
    try {
      ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
      ObjectOutputStream out = new ObjectOutputStream(byteOut);
      out.writeObject(src);

      ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
      ObjectInputStream in = new ObjectInputStream(byteIn);

      @SuppressWarnings("unchecked")
      List<T> dest = (List<T>) in.readObject();
      return dest;
    } catch (IOException | ClassNotFoundException e) {
      throw new RuntimeException("深拷贝失败", e);
    }
  }
}
