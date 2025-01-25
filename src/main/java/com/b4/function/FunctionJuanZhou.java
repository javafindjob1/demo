package com.b4.function;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.b4.Function;
import com.b4.FunctionDetail;
import com.b4.MapUtil;
import com.b4.FunctionDetail.DropInfo;

public class FunctionJuanZhou {

  public FunctionDetail parse(Function fun) {
    // Trigger: I13-[添加卷轴]
    // 自定义jass生成器 作者：007
    // 有bug到魔兽地图编辑器吧 @w4454962
    // bug反馈群：724829943 lua 技术交流3群：710331384
    // ===========================================================================
    // function Trig_I13_______________uActions takes nothing returns nothing
    // call DisableTrigger(GetTriggeringTrigger())
    FunctionDetail functionDetail = new FunctionDetail();
    functionDetail.setRows(fun.getRows());
    functionDetail.setName(fun.getName());
    // call AddItemToStockBJ('I01L', gg_unit_n06P_1440, 1, 1)
    Pattern stockPattern = Pattern.compile("call AddItemToStockBJ\\('(\\w{4})', gg_unit_(\\w{4})");

    List<String> rows = fun.getRows();
    Map<String, List<DropInfo>> juanzhouMap = new HashMap<>();
    for (int i = 0; i < rows.size(); i++) {
      String rowString = rows.get(i);
      Matcher matcher = stockPattern.matcher(rowString);
      if (matcher.find()) {
        // call AddItemToStockBJ('I01L', gg_unit_n06P_1440, 1, 1)
        String itemId = matcher.group(1);
        String unitId = matcher.group(2);

        List<DropInfo> list = MapUtil.getNotNull(juanzhouMap, unitId, ArrayList::new);
        list.add(new DropInfo(itemId));
      }
    }
    functionDetail.setJuanzhouMap(juanzhouMap);

    return functionDetail;
  }
}
