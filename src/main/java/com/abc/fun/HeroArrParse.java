package com.abc.fun;

import static org.junit.Assert.assertFalse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.abc.Function;

/*
 * 
set SJ=CreateUnit(Player(15),'H000',25039.4,29658.4,289.8)
set SK=SJ

set bb[20]=Sc

 */
public class HeroArrParse {

  public static Map<String,String> parse(List<Function> functions, String arrName) throws Exception {
    Pattern createUnitPattern =  Pattern.compile("set (\\w+)=CreateUnit\\(Player\\(15\\),'(\\w+)',");
    Map<String,String> unitNameMap = new HashMap<>();
    for (Function function : functions) {
      List<String> rows = function.getRows();
      for (int i = 0; i < rows.size(); i++) {
        String row = rows.get(i);
        Matcher matcher = createUnitPattern.matcher(row);
        if (matcher.find()) {
          String varName = matcher.group(1);
          String unitId = matcher.group(2);

          String nextLine = rows.get(i+1);
          if(nextLine.contains("="+varName)){
            String globalName = nextLine.substring(4, nextLine.indexOf("="));
            unitNameMap.put(globalName, unitId);
          }else{
            // assertFalse("单位全局名称解析错误"+row, true);
          }
        }
      }
    }


    Pattern unitArrPattern =  Pattern.compile("set ("+arrName+"\\[\\d+\\])=(\\w+)");

    // 数组名称关联
    for (Function function : functions) {
      List<String> rows = function.getRows();
      for (int i = 0; i < rows.size(); i++) {
        String row = rows.get(i);
        Matcher matcher = unitArrPattern.matcher(row);
        if (matcher.find()) {
          String varName = matcher.group(1);
          String globalName = matcher.group(2);

          String unitId = unitNameMap.get(globalName);

          unitNameMap.put(varName, unitId);
        }
      }
    }


    return unitNameMap;
  }
  
}
