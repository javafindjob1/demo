package com.abc.fun;

import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.abc.Function;
import com.abc.MapUtil;

/*
 * 
set X8[1]="技能冷却-4%"
set X9[1]="法术暴击+7%"

 */
public class HeroAdditionalParse {

  public static Map<String, List<String>> parse(List<Function> functions, Map<String,String> heroNameArrMap) throws Exception {
    Map<String, List<String>> additionalMap = new HashMap<>();
    for (Function function : functions) {
      List<String> rows = function.getRows();
      for (int i = 0; i < rows.size(); i++) {
        String row = rows.get(i);
        // 定位到了英雄皮肤描述的位置
        if (row.contains("]=\"技能冷却")) {
          // 天赋1
          {
            Pattern effectPattern = Pattern.compile("set \\w+\\[(\\d+)\\]=\"(.*)\"");
            String flag = row.substring(0, row.indexOf("["));
            for (int j = i; j < rows.size(); j++) {
              String tmpRow = rows.get(j);
              if (tmpRow.startsWith(flag)) {
                Matcher matcher = effectPattern.matcher(tmpRow);
                if (matcher.find()) {
                  String unitNo = matcher.group(1);
                  String effectStr = matcher.group(2);

                  List<String> additionalList = MapUtil.getNotNull(additionalMap, unitNo, ArrayList::new);
                  additionalList.add(effectStr);
                }
              }
            }
          }
          // 天赋二
          {
            row = rows.get(i + 1);
            Pattern effectPattern = Pattern.compile("set \\w+\\[(\\d+)\\]=\"(.*)\"");
            String flag = row.substring(0, row.indexOf("["));
            for (int j = i + 1; j < rows.size(); j++) {
              String tmpRow = rows.get(j);
              if (tmpRow.startsWith(flag)) {
                Matcher matcher = effectPattern.matcher(tmpRow);
                if (matcher.find()) {
                  String unitNo = matcher.group(1);
                  String effectStr = matcher.group(2);

                  List<String> additionalList = MapUtil.getNotNull(additionalMap, unitNo, ArrayList::new);
                  additionalList.add(effectStr);
                }
              }
            }
          }

          // 将bb[12]改成E018
          for (Entry<String, String> unitNameEntry : heroNameArrMap.entrySet()) {
            String key = unitNameEntry.getKey();
            String value = unitNameEntry.getValue();

            String unitNo = key.substring(key.indexOf("[") + 1, key.length() - 1);
            List<String> additionals = additionalMap.remove(unitNo);
            if (additionals != null) {
              additionalMap.put(value, additionals);
            }
          }

          return additionalMap;
        }
      }
    }

    return additionalMap;
  }
}