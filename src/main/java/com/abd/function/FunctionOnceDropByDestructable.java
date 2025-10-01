package com.abd.function;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.abd.Function;
import com.abd.FunctionDetail;
import com.abd.MapUtil;
import com.abd.FunctionDetail.DropInfo;

public class FunctionOnceDropByDestructable {
  private Map<String, Function> funMap;

  public FunctionOnceDropByDestructable(Map<String, Function> funMap) {
    this.funMap = funMap;
  }
  public FunctionDetail parse(Function fun) {
    // set d=CreateDestructable('LTbx', - 5472.0, - 23136.0, 303.0, 1.5, 0)
    // set gg_dest_B00E_15380=CreateDestructable('B00E', - 11488.0, - 17888.0, 154.0, 0.8, 1)

    // set t=CreateTrigger()
    // call TriggerRegisterDeathEvent(t, d)
    // call TriggerAddAction(t, function SaveDyingWidget)
    // call TriggerAddAction(t, function ItemTable_000002_DropItems)
    Map<String, List<DropInfo>> map = new HashMap<>();

    List<String> rows = fun.getRows();
    Pattern destructableNamePattern = Pattern
        .compile("set \\w+=CreateDestructable\\('(\\w{4})',");
    Pattern triggerPattern = Pattern.compile("set t=CreateTrigger\\(\\)");
    Pattern eventDeathPattern = Pattern.compile("call TriggerRegisterDeathEvent\\(t, d\\)");
    Pattern saveDyingWidgetPattern = Pattern.compile("call TriggerAddAction\\(t, function SaveDyingWidget\\)");
    Pattern dropItemPattern = Pattern.compile("call TriggerAddAction\\(t, function (\\w+DropItems)\\)");

    String unitName = null;
    for (int i = 0; i < rows.size(); i++) {
      String rowString = rows.get(i);
      Matcher matcher = null;
      if((matcher = destructableNamePattern.matcher(rowString)).find()){
        unitName = matcher.group(1);
        System.out.println("create destructable " + unitName);
        continue;
      }
      
      if(triggerPattern.matcher(rowString).find()){
        System.out.println("create trigger");
      }else if(eventDeathPattern.matcher(rowString).find()){
        System.out.println("create eventDeath");
      }else if(saveDyingWidgetPattern.matcher(rowString).find()){
        System.out.println("create saveDyingWidget");
      }else if((matcher=dropItemPattern.matcher(rowString)).find()){
        assertNotNull("unitName不应该为空,line=" + i + ",function=" + fun.getName(), unitName);

        String funName = matcher.group(1);
        System.out.println("dropItemFunName " + funName);
        Function dropItemFun = funMap.get(funName);
        List<DropInfo> dropItemList = FunctionOnceDrop.parse(dropItemFun);
        List<DropInfo> allList = MapUtil.getNotNull(map, unitName, ArrayList::new);
        allList.addAll(dropItemList);

        unitName = null;
      }
    

    }

    FunctionDetail functionDetail = new FunctionDetail();
    functionDetail.setRows(rows);
    functionDetail.setName(fun.getName());
    functionDetail.setItemMap(map);
    return functionDetail;
  }
}
