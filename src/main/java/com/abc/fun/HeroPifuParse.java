package com.abc.fun;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.abc.Function;
import com.abc.MapUtil;

/**
 * 皮肤技能增强解析
 * 
 * set Ws[11]="伤害+25%|n每段闪电回复生命+5%"
 */
public class HeroPifuParse {
  public static Map<String, Map<String, StringBuilder>>[] parse(List<Function> functions, Map<String,String> heroNameArrMap) throws Exception {
    Map<String, Map<String, StringBuilder>> pifuAbiMap = new HashMap<>();
    Map<String, Map<String, StringBuilder>> pifuAbiMap2 = new HashMap<>();
    Map<String, Map<String, StringBuilder>> pifuAbiMapArr[] = new Map[] { pifuAbiMap, pifuAbiMap2 };

    String pifuFlag = null;
    String funtionName = null;
    for (Function function : functions) {
      List<String> rows = function.getRows();
      for (int i = 0; i < rows.size(); i++) {
        String row = rows.get(i);
        // 定位到了英雄皮肤1描述的位置
        if (row.contains("[11]=\"伤害+25%")) {
          funtionName = function.getRows().get(0);
          Pattern effectPattern = Pattern.compile("set \\w+\\[(\\d+)\\]=\"(.*)\"");
          pifuFlag = row.substring(0, row.indexOf("["));
          for (int j = i; j < rows.size(); j++) {
            String pifuRow = rows.get(j);
            if (pifuRow.startsWith(pifuFlag)) {
              Matcher matcher = effectPattern.matcher(pifuRow);
              if (matcher.find()) {
                String unitIdAndLevel = matcher.group(1);
                String effectStr = matcher.group(2);

                build(unitIdAndLevel, effectStr, pifuAbiMap);
              }
            }
          }

          // 查找皮肤2的描述
          for (Function function2 : functions) {
            List<String> rows2 = function2.getRows();
            // 皮肤1的跳过
            if (rows2.get(0).equals(funtionName)) {
              continue;
            }
            for (int i2 = 0; i2 < rows2.size(); i2++) {
              String row2 = rows2.get(i2);
              // 定位到了英雄皮肤2描述的位置
              if (row2.startsWith(pifuFlag)) {
                Matcher matcher = effectPattern.matcher(row2);
                if (matcher.find()) {
                  String unitIdAndLevel = matcher.group(1);
                  String effectStr = matcher.group(2);

                  build(unitIdAndLevel, effectStr, pifuAbiMap2);
                }
              }
            }
          }

          // 将bb[12]改成E018
          for (Entry<String, String> unitNameEntry : heroNameArrMap.entrySet()) {
            String key = unitNameEntry.getKey();
            String value = unitNameEntry.getValue();

            String unitNo = key.substring(key.indexOf("[") + 1, key.length() - 1);

            // 皮肤1
            {
              Map<String, StringBuilder> ultimates = pifuAbiMapArr[0].remove(unitNo);
              if (ultimates != null) {
                pifuAbiMapArr[0].put(value, ultimates);
              }
            }
            // 皮肤2
            {
              Map<String, StringBuilder> ultimates = pifuAbiMapArr[1].remove(unitNo);
              if (ultimates != null) {
                pifuAbiMapArr[1].put(value, ultimates);
              }
            }
          }
          return pifuAbiMapArr;
        }
      }
    }

    return pifuAbiMapArr;
  }

  private static void build(String unitIdAndLevel, String effectStr, Map<String, Map<String, StringBuilder>> map) {
    int number = Integer.parseInt(unitIdAndLevel);
    String unitNo = number / 10 + "";
    int abilityNo = number % 10;
    if (abilityNo > 5) {
      abilityNo = 5;
    }
    String abString = abilityNo + "";

    Map<String, StringBuilder> abilityMap = MapUtil.getNotNull(map, unitNo, HashMap::new);
    StringBuilder buf = MapUtil.getNotNull(abilityMap, abString, StringBuilder::new);
    buf.append(effectStr);
  }
}
