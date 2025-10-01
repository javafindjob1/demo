package com.abd.function.hero;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.abd.Function;

/**
 * 
 * 英雄主要属性解析
 * Trig_UI_03Actions
 */
public class FunctionHeroMainProp {
    public Map<String, String> parse(Map<String, Function> funMap) {
    Function fun = funMap.get("Trig_UI_03Actions");
    Map<String, String> udgHeroMap = new HashMap<>();

    //
    //	if ( ( ( udg_Hero[LoadInteger(YDLOC, GetHandleId(GetTriggeringTrigger()) + ydl_localvar_step * 0x80000, 0x380D82FF)] == udg_ROCK ) or ( udg_Hero[LoadInteger(YDLOC, GetHandleId(GetTriggeringTrigger()) + ydl_localvar_step * 0x80000, 0x380D82FF)] == udg_TLFL ) or ( udg_Hero[LoadInteger(YDLOC, GetHandleId(GetTriggeringTrigger()) + ydl_localvar_step * 0x80000, 0x380D82FF)] == udg_TLFLP ) or ( udg_Hero[LoadInteger(YDLOC, GetHandleId(GetTriggeringTrigger()) + ydl_localvar_step * 0x80000, 0x380D82FF)] == udg_ONE ) ) ) then
		//  call SaveStr(YDLOC, GetHandleId(GetTriggeringTrigger()) + ydl_localvar_step * 0x80000, 0x9008A24D, "|cfff4606c力量|r")

    Pattern pattern = Pattern.compile("\\( udg_Hero\\[LoadInteger\\(YDLOC, GetHandleId\\(GetTriggeringTrigger\\(\\)\\) \\+ ydl_localvar_step \\* 0x80000, 0x380D82FF\\)\\] == (udg_\\w+) \\)");
    Pattern propPattern = Pattern.compile("\"(.+)\"\\)$");
    
    List<String> heroList = new ArrayList<>();
    for(String row : fun.getRows()){
      if(row.contains("if ( ( ( udg_Hero")){
        Matcher matcher = pattern.matcher(row);
        while (matcher.find()) {
          String heroName = matcher.group(1);
          heroList.add(heroName);
        }
      }

      if(heroList.size()>0 && row.contains("call SaveStr(")){
        Matcher matcher = propPattern.matcher(row);
        assertTrue("未找到属性 row=" + row, matcher.find());

        String prop = matcher.group(1);
        Map<String, String> heroMap = heroList.stream().collect(Collectors.toMap((k)->k, k-> prop));
        udgHeroMap.putAll(heroMap);
        heroList.clear();
      }
    }
    return udgHeroMap;
  }
}
