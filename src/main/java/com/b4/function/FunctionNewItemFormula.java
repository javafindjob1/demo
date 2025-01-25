package com.b4.function;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.b4.Function;
import com.b4.FunctionDetail;
import com.b4.FunctionDetail.ItemAccessories;

public class FunctionNewItemFormula {

  public FunctionDetail parse(Function fun) {
    FunctionDetail functionDetail = new FunctionDetail();
    functionDetail.setName(fun.getName());
    functionDetail.setRows(fun.getRows());

    // call SaveInteger(YDLOC, GetHandleId(GetTriggeringTrigger()) +
    // ydl_localvar_step * 0x80000, 0xB3FFE7B6, GetRandomInt(1, 100))
    // if ( ( LoadInteger(YDLOC, GetHandleId(GetTriggeringTrigger()) +
    // ydl_localvar_step * 0x80000, 0xB3FFE7B6) <= 50 ) ) then
    // call YDWENewItemsFormula('I00C' , 1 , 'I00J' , 1 , 'afac' , 0 , 'afac' , 0 ,
    // 'I01L' , 1 , 'I02A' , 1 , 'I09Q')

    Pattern formulaPattern = Pattern.compile("'(\\w{4})' , (\\d+) ,");
    Pattern itemPattern = Pattern.compile("'(\\w{4})'\\)");

    Map<String, List<ItemAccessories>> itemFormulaMap = new HashMap<>();
    for (String row : fun.getRows()) {
      if(row.contains("YDWENewItemsFormula")){
        
        List<ItemAccessories> list = new ArrayList<>();
        Matcher matcher = formulaPattern.matcher(row);
        while(matcher.find()){
          String itemId = matcher.group(1);
          int count = Integer.parseInt(matcher.group(2));
          if(count>0){
            list.add(new ItemAccessories(itemId, count));
          }
        }

        if((matcher=itemPattern.matcher(row)).find()){
          String itemId = matcher.group(1);
          itemFormulaMap.put(itemId, list);
        }
      }
    }
    
    functionDetail.setNewItemFormulaMap(itemFormulaMap);
    return functionDetail;
  }
}
