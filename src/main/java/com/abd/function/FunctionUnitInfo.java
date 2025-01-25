package com.abd.function;

import java.util.Map;

import com.abc.FunctionDetail;
import com.abd.Function;

public class FunctionUnitInfo {
  
  public FunctionDetail parse(Map<String, Function> funMap){
    Function fun = funMap.get("Trig_8__________________________________uActions");
    FunctionDetail functionDetail = new FunctionDetail();
    fun.setName(fun.getName());
    fun.setRows(fun.getRows());


    
    

    return functionDetail;
  }
}
