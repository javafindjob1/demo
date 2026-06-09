package com.xi7.fun;

import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.xi7.Function;
import com.xi7.MapUtil;

/*
 * 
if GetTriggerUnit()==bb[22] and GetUnitLevel(GetTriggerUnit())>=15 and GetUnitAbilityLevel(GetTriggerUnit(),'A0LJ')==0 then
call UnitAddAbility(GetTriggerUnit(),'A0LJ')
call DisplayTimedTextToPlayer(GetOwningPlayer(GetTriggerUnit()),0,0,5.,"圣十字天使领悟了终极技能|cffff0000加百列光辉|r")

 */
public class HeroUltimateParse {
  public static Map<String, List<String>> parse(List<Function> functions, Map<String, String> heroNameArrMap)
      throws Exception {
    Map<String, List<String>> ultimateMap = new HashMap<>();
    Pattern unitIdPattern = Pattern.compile("GetTriggerUnit\\(\\)==(\\w+\\[\\d+\\])");
    Pattern ultimatePattern = Pattern.compile("call UnitAddAbility\\(GetTriggerUnit\\(\\),'(\\w+)'\\)");
    for (Function function : functions) {
      List<String> rows = function.getRows();
      for (int i = 0; i < rows.size(); i++) {
        String row = rows.get(i);
        if (row.contains("领悟了终极")) {
          if(row.contains("银河皇帝"))System.out.println(row);
          String ultimateStr = rows.get(i - 1);
          int j = 2;
          while(!ultimateStr.contains("UnitAddAbility")){
            ultimateStr = rows.get(i - j++);
          }
          Matcher matcher = ultimatePattern.matcher(ultimateStr);
          if(matcher.find()) {
            String ultimate = matcher.group(1);

            int startIndex = i;
            while (startIndex > 0 && !rows.get(startIndex).startsWith("if")) {
              startIndex--;
            }
            String unitStr = rows.get(startIndex);
            matcher = unitIdPattern.matcher(unitStr);
            if (matcher.find()) {
              String unitName = matcher.group(1);
              List<String> ultimateList = MapUtil.getNotNull(ultimateMap, unitName, ArrayList::new);
              ultimateList.add(ultimate);
            } else {
              assertFalse("大招解析失败" + unitStr, true);
            }
          }
        }
      }
    }

    // 将bb[12]改成E018
    System.out.println(heroNameArrMap.get("fQ[35]"));
    System.out.println(ultimateMap.get("fQ[35]"));
    for (Entry<String, String> unitNameEntry : heroNameArrMap.entrySet()) {
      String key = unitNameEntry.getKey();
      String value = unitNameEntry.getValue();

      List<String> ultimates = ultimateMap.remove(key);
      if (ultimates != null) {
        ultimateMap.put(value, ultimates);
      }
    }
    System.out.println(ultimateMap.get("N02R"));

    return ultimateMap;
  }
}