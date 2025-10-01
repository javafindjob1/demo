package com.abd.function;

import static org.junit.Assert.assertTrue;

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

public class FunctionOnceDropByUnit {
  private Map<String, Function> funMap;

  public FunctionOnceDropByUnit(Map<String, Function> funMap) {
    this.funMap = funMap;
  }

  public FunctionDetail parse(Function fun) {
    Map<String, List<DropInfo>> map = new HashMap<>();

    List<String> rows = fun.getRows();
    // set u=CreateUnit(Player(10), 'e00P', - 24179.2, - 28914.2, 18.2)
    // set t=CreateTrigger()
    // call TriggerRegisterUnitEvent(t, u, EVENT_UNIT_DEATH)
    // call TriggerRegisterUnitEvent(t, u, EVENT_UNIT_CHANGE_OWNER)
    // call TriggerAddAction(t, function Unit000041_DropItems)

    Pattern unitPattern = Pattern.compile("set \\w+=CreateUnit\\(Player\\(10\\), '(\\w{4})',");
    Pattern triggerPattern = Pattern.compile("set t=CreateTrigger\\(\\)");
    Pattern eventUnitDeathPattern = Pattern.compile("call TriggerRegisterUnitEvent\\(t, u, EVENT_UNIT_DEATH\\)");
    Pattern eventUnitChangeOwnerPattern = Pattern.compile("call TriggerRegisterUnitEvent\\(t, u, EVENT_UNIT_CHANGE_OWNER\\)");
    Pattern funPattern = Pattern.compile("call TriggerAddAction\\(t, function (\\w+DropItems)\\)");

    String unitName = null;
    for (int i = 0; i < rows.size(); i++) {
      String rowString = rows.get(i);
      Matcher matcher = null;
      if((matcher = unitPattern.matcher(rowString)).find()){
        unitName = matcher.group(1);
        System.out.print("create unit " + unitName);
        continue;
      }
      
      if(triggerPattern.matcher(rowString).find()){
        System.out.println("create trigger");
      }else if(eventUnitDeathPattern.matcher(rowString).find()){
        System.out.println("create eventUnitDeath");
      }else if(eventUnitChangeOwnerPattern.matcher(rowString).find()){
        System.out.println("create eventUnitChangeOwner");
      }else if((matcher=funPattern.matcher(rowString)).find()){
        assertTrue("unitName未匹配到,line=" + i + ",function=" + fun.getName(), unitName!=null);

        String funName = matcher.group(1);
        Function dropItemFun = funMap.get(funName);
        System.out.println("\ncreate funPattern " + funName);
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
