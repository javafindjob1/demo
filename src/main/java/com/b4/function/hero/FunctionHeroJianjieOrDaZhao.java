package com.b4.function.hero;


import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.b4.Function;
import com.b4.MapUtil;

/**
 * 英雄简介(也包含一些大招)
 * Trig_8__________________________________uActions
 */
public class FunctionHeroJianjieOrDaZhao {

  public Map<String, List<String>> parse(Map<String, Function> funMap) {
    Function fun = funMap.get("Trig_8__________________________________uActions");

    // if ( ( LoadUnitHandle(YDLOC, GetHandleId(GetTriggeringTrigger()) + ydl_localvar_step * 0x80000, 0xC303079D) == gg_unit_H00B_1274 ) ) then
    // call UnitAddAbility(LoadUnitHandle(YDLOC, GetHandleId(GetTriggeringTrigger()) + ydl_localvar_step * 0x80000, 0xC303079D), 'A119')
    Pattern heroPattern = Pattern.compile("if \\( \\( LoadUnitHandle\\(YDLOC, GetHandleId\\(GetTriggeringTrigger\\(\\)\\) \\+ ydl_localvar_step \\* \\w+, \\w+\\) == gg_unit_(\\w+)_\\w+ \\) \\) then");
    Pattern abilityPattern = Pattern.compile("call UnitAddAbility\\(LoadUnitHandle\\(YDLOC, GetHandleId\\(GetTriggeringTrigger\\(\\)\\) \\+ ydl_localvar_step \\* \\w+, \\w+\\), '(\\w+)'\\)");

    Map<String, List<String>> heroMap = new HashMap<>();
    String heroName = null;
    for (String row : fun.getRows()) {
      if (row.contains("if ( ( LoadUnitHandle(YDLOC,")) {
        Matcher matcher = heroPattern.matcher(row);
        assertTrue("英雄ID未匹配成功" + row, matcher.find());
        heroName = matcher.group(1);
      }

      if (heroName!=null && row.contains("call UnitAddAbility(LoadUnitHandle")) {
        Matcher matcher = abilityPattern.matcher(row);
        assertTrue("技能ID未匹配成功" + row, matcher.find());
        String ability = matcher.group(1);
        
        List<String> abilitys = MapUtil.getNotNull(heroMap, heroName, ArrayList::new);
        abilitys.add(ability);
        heroMap.put(heroName, abilitys);
      }

      // 遇到else就切换到下一个英雄了
      if(row.contains("else")){
        heroName = null;
      }
    }

    return heroMap;
  }

  private Map<String, String> getHeroMap(Function fun) {
    Map<String, String> heroMap = new HashMap<>();
    // set udg_BERSERKER=
    Pattern pattern = Pattern.compile("set (udg_\\w+)=gg_unit_(\\w+)_\\w+$");
    for (String row : fun.getRows()) {
      if (row.contains("set")) {
        Matcher matcher = pattern.matcher(row);
        if (matcher.find()) {
          String udgHeroName = matcher.group(1);
          String heroName = matcher.group(2);
          heroMap.put(heroName, udgHeroName);
        }
      }
    }
    return heroMap;
  }

}
