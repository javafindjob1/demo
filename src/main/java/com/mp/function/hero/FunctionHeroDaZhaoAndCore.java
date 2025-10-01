package com.mp.function.hero;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.common.parse.Function;

/**
 * 英雄大招
 * 
 * 大招解锁 男 111 以及魔核
 * Trig_H3____________________________uActions
 * 大招解锁 女 222 以及魔核
 * Trig_H4____________________________uActions
 */
public class FunctionHeroDaZhaoAndCore {
  public Map<String, String> parse(Map<String, Function> funMap) {
    Map<String, String> heroMap = new HashMap<>();

    heroMap.putAll(parse(funMap.get("Trig_H3____________________________uActions")));
    heroMap.putAll(parse(funMap.get("Trig_H4____________________________uActions")));
    return heroMap;
  }

  private Map<String, String> parse(Function fun) {

    // if ( ( LoadUnitHandle(YDLOC, GetHandleId(GetTriggeringTrigger()) +
    // ydl_localvar_step * 0x80000, 0xC303079D) == udg_NATURE ) ) then
    // call SaveInteger(YDLOC, GetHandleId(GetTriggeringTrigger()) +
    // ydl_localvar_step * 0x80000, 0x384C9D86, 'A06P')
    // else

    // if ( ( LoadUnitHandle(YDLOC, GetHandleId(GetTriggeringTrigger()) +
    // ydl_localvar_step * 0x80000, 0xC303079D) == gg_unit_H00H_1051 ) ) then
    // call SaveInteger(YDLOC, GetHandleId(GetTriggeringTrigger()) +
    // ydl_localvar_step * 0x80000, 0xF374F3BD, 'A0Q1')
    // else

    // if ( ( LoadUnitHandle(YDLOC, GetHandleId(GetTriggeringTrigger()) +
    // ydl_localvar_step * 0x80000, 0xC303079D) == udg_BLD ) ) then
    // if ( ( ( LoadUnitHandle(YDLOC, GetHandleId(GetTriggeringTrigger()) +
    // ydl_localvar_step * 0x80000, 0xC303079D) == udg_YGSS ) or (
    // LoadUnitHandle(YDLOC, GetHandleId(GetTriggeringTrigger()) + ydl_localvar_step
    // * 0x80000, 0xC303079D) == udg_YGSSV ) ) ) then

    Pattern heroNamePattern = Pattern.compile(
        "\\( LoadUnitHandle\\(YDLOC, GetHandleId\\(GetTriggeringTrigger\\(\\)\\) \\+ ydl_localvar_step \\* \\w+, \\w+\\) == ((gg_unit_(\\w+)_\\w+)|(udg_\\w+)) \\)");
    Pattern propPattern = Pattern.compile(
        "call SaveInteger\\(YDLOC, GetHandleId\\(GetTriggeringTrigger\\(\\)\\) \\+ ydl_localvar_step \\* \\w+, \\w+, '(\\w+)'\\)");

    Map<String, String> heroDazhaoMap = new HashMap<>();
    // 0:大招
    List<String> heros = new ArrayList<>();
    List<String> rows = fun.getRows();
    for (int i = 0; i < rows.size() - 1; i++) {
      String row = rows.get(i);
      String rowNext = rows.get(i + 1);
      if (row.contains("if ( ( ") && rowNext.contains("call SaveInteger(YDLOC")) {
        Matcher matcher = heroNamePattern.matcher(row);
        while (matcher.find()) {
          String heroName = matcher.group(3);
          if (heroName == null) {
            heroName = matcher.group(4);
          }
          heros.add(heroName);
        }
        if(heros.isEmpty()){
          // 未解析出英雄
          continue;
        }

        Matcher matcherNext = propPattern.matcher(rowNext);
        if (matcherNext.find()) {
          String prop = matcherNext.group(1);
          for (String heroName : heros) {
            heroDazhaoMap.put(heroName, prop);
          }
        }
        // 重置 开始下一轮匹配
        heros.clear();
      }
    }

    return heroDazhaoMap;
  }
}
