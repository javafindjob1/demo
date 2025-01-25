package com.abd.function.hero;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.abd.Function;

/**
 * 
 * 单位名称解析
 * 
 * [设置界面-定义英雄-设置称号]  英雄信息
 * Trig_1_________________________________________uActions
 * [创建英雄]-皮
 * Trig_9___________________uActions
 * 
 */
public class FunctionHeroName {
  public Map<String, String> parse(Map<String, Function> funMap) {
    

    Map<String, String> heroMap = new HashMap<>();
    heroMap.putAll(getHeroMap(funMap.get("Trig_1_________________________________________uActions")));
    heroMap.putAll(getHeroMap2(funMap.get("Trig_9___________________uActions")));

    return heroMap;
  }

  private Map<String, String> getHeroMap(Function fun) {
    Map<String, String> heroMap = new HashMap<>();
    //set udg_BERSERKER=
    Pattern pattern = Pattern.compile("set (udg_\\w+)=gg_unit_(\\w+)_\\w+$");
    for(String row : fun.getRows()){
      if(row.contains("set")){
        Matcher matcher = pattern.matcher(row);
        if(matcher.find()){
          String udgHeroName = matcher.group(1);
          String heroName = matcher.group(2);
          heroMap.put(heroName, udgHeroName);
        }
      }
    }
    return heroMap;
  }

  private Map<String, String> getHeroMap2(Function fun) {
    Map<String, String> heroMap = new HashMap<>();
    //call SaveUnitHandle(YDLOC, GetHandleId(GetTriggeringTrigger()) + ydl_localvar_step * 0x80000, 0xC303079D, gg_unit_E01K_1279)
    Pattern savePattern = Pattern.compile("call SaveUnitHandle\\(YDLOC, GetHandleId\\(GetTriggeringTrigger\\(\\)\\) \\+ ydl_localvar_step \\* \\w+, \\w+, gg_unit_(\\w+)_\\w+\\)$");
    //set udg_BERSERKER=
    Pattern setPattern = Pattern.compile("set (udg_\\w+)=");
    String heroName = null;
    for(String row : fun.getRows()){
      if(row.contains("call SaveUnitHandle")){
        Matcher matcher = savePattern.matcher(row);
        if(matcher.find()){
          heroName = matcher.group(1);
        }
      }

      if(heroName!=null && row.contains("set")){
        Matcher matcher = setPattern.matcher(row);
        if(matcher.find()){
          String udgHeroName = matcher.group(1);
          heroMap.put(heroName, udgHeroName);
          heroName = null;
        }
      }
    }
    return heroMap;
  }
}
