package com.b4.function;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.b4.Function;
import com.b4.FunctionDetail;
import com.b4.MapUtil;
import com.b4.FunctionDetail.DropInfo;

public class FunctionAllDrop {
  public List<FunctionDetail> parse(Map<String, Function> funMap) {

    List<FunctionDetail> list = new ArrayList<>();
    for (Entry<String, Function> entrySet : funMap.entrySet()) {
      String funName = entrySet.getKey();
      Function fun = entrySet.getValue();
      FunctionDetail functionDetail = new FunctionDetail();

      functionDetail.setName(funName);
      functionDetail.setRows(fun.getRows());

      Pattern unitpattern = Pattern.compile(
          "call SaveUnitHandle\\(YDLOC, GetHandleId\\(GetTriggeringTrigger\\(\\)\\) \\+ ydl_localvar_step \\* \\w+, \\w+, gg_unit_(\\w{4})_\\w+\\)");

      Pattern itemPattern = Pattern.compile(
          "call SaveItemHandle\\(YDLOC, GetHandleId\\(GetTriggeringTrigger\\(\\)\\) \\+ ydl_localvar_step \\* \\w+, \\w+, CreateItem\\('(\\w{4})',");

      if (funName.endsWith("uActions")) {
        String unitId = null;
        Map<String, List<DropInfo>> itemMap = functionDetail.getItemMap();
        for (String row : fun.getRows()) {
          Matcher matcher = null;
          if ((matcher = unitpattern.matcher(row)).find()) {
            unitId = matcher.group(1);
          } else if (unitId!=null && (matcher = itemPattern.matcher(row)).find()) {
            List<DropInfo> list2 = MapUtil.getNotNull(itemMap, unitId, ArrayList::new);
            list2.add(new DropInfo(matcher.group(1)));
          } else if (functionDetail.getItemMap().size()>0 && row.contains("梦想里程点 + ")) {
            list.add(functionDetail);
          }
        }
        
        // 森林古主没有梦想里程点单独加上
        if(funName.equals("Trig_RE16_9_____________________uActions")){
          list.add(functionDetail);
        }
      }

    }
    return list;
  }
}
