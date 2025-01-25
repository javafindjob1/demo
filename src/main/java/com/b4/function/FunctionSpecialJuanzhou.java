package com.b4.function;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.abc.MapUtil;
import com.b4.Function;
import com.b4.FunctionDetail;
import com.b4.FunctionDetail.DropInfo;

public class FunctionSpecialJuanzhou {
  
  public FunctionDetail parse(Function fun) {

    FunctionDetail functionDetail = new FunctionDetail();
    functionDetail.setName(fun.getName());
    functionDetail.setRows(fun.getRows());

    // 		call AddItemToStockBJ('I084', gg_unit_n03P_0647, 1, 1)
    Pattern stockPattern = Pattern.compile("call AddItemToStockBJ\\('(\\w{4})', gg_unit_(\\w{4})");
    Map<String, List<DropInfo>> juanzhouMap = functionDetail.getJuanzhouMap();
    for(String rowString : fun.getRows()){
      Matcher matcher = stockPattern.matcher(rowString);
      if(matcher.find()){
        // call AddItemToStockBJ('I01L', gg_unit_n06P_1440, 1, 1)
        String itemId = matcher.group(1);
        String unitId = matcher.group(2);

        List<DropInfo> notNull = MapUtil.getNotNull(juanzhouMap, unitId, ArrayList::new);
        notNull.add(new DropInfo(itemId));
      }
    }

    
    return functionDetail;
  }

}
