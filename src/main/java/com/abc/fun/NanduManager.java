package com.abc.fun;

import static org.junit.Assert.assertFalse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Data;

@Data
public class NanduManager {

  Pattern pattern = Pattern.compile("if \\w+([>=<]+)(\\d) then");

  // 控制的层数,也是控制退出的层数
  private int j;
  private String desc;
  private String desc1;
  private String desc2;

  public boolean updateIfLoad(int j, String row) {

    Matcher matcher = pattern.matcher(row);
    if(matcher.find()){
      String fuhao = matcher.group(1);
      int k = Integer.parseInt(matcher.group(2));

      switch (fuhao) {
        case ">=":
          this.desc1 = "(难度" + k + "及以上)";
          this.desc2 = "(难度" + (k-1) + "及以下)";
          this.desc = this.desc1;
          break;
        default:
          assertFalse("不支持的难度符号", true);
          break;
      }
      return true;
    }else if(row.matches("if \\w+([=]+)true then")){
      this.desc1 = "(难度" + 6 + ")";
      this.desc2 = "(难度" + 5 + "及以下)";
      this.desc = this.desc1;
      return true;
    }
    return false;
    
  }

  public void updateElse(int j) {
    if(j == this.j){
      this.desc = this.desc2;
    }
  }

  public void updateEndif(int j) {
    if(j == this.j){
      this.desc = null;
    }
  }
}
