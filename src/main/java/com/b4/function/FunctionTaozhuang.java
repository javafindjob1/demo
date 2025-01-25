package com.b4.function;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.b4.Function;
import com.b4.FunctionDetail;

import lombok.Data;

@Data
public class FunctionTaozhuang {
  private String taozhuangName;
  private String color;
  private String taozhuangDesc;
  private List<String> itemList = new ArrayList<>();
  
  public FunctionDetail parse(Function fun){

    FunctionDetail functionDetail = new FunctionDetail();
    functionDetail.setRows(fun.getRows());
    functionDetail.setName(fun.getName());
    // if ( ( YDWEUnitHasItemOfTypeBJNull(LoadUnitHandle(YDLOC, GetHandleId(GetTriggeringTrigger()) + ydl_localvar_step * 0x80000, 0xB06AB615) , 'I00K') == true ) and ( YDWEUnitHasItemOfTypeBJNull(LoadUnitHandle(YDLOC, GetHandleId(GetTriggeringTrigger()) + ydl_localvar_step * 0x80000, 0xB06AB615) , 'I01M') == true ) ) then
		// call StarMsg_DisplayTimedTextToPlayer(LoadPlayerHandle(YDLOC, GetHandleId(GetTriggeringTrigger()) + ydl_localvar_step * 0x80000, 0x48E3E75F) , 0.0 , 0.0 , 8.00 , "|cff00ff00生机勃发|r" , 0.00)
		// call StarMsg_DisplayTimedTextToPlayer(LoadPlayerHandle(YDLOC, GetHandleId(GetTriggeringTrigger()) + ydl_localvar_step * 0x80000, 0x48E3E75F) , 0.0 , 0.0 , 8.00 , "|cffe6ceac所需装备组件：|r" , 0.00)
		// call StarMsg_DisplayTimedTextToPlayer(LoadPlayerHandle(YDLOC, GetHandleId(GetTriggeringTrigger()) + ydl_localvar_step * 0x80000, 0x48E3E75F) , 0.0 , 0.0 , 8.00 , "|cff00ff00常青藤片手斧|r" , 0.00)
		// call StarMsg_DisplayTimedTextToPlayer(LoadPlayerHandle(YDLOC, GetHandleId(GetTriggeringTrigger()) + ydl_localvar_step * 0x80000, 0x48E3E75F) , 0.0 , 0.0 , 8.00 , "常青藤木戒" , 0.00)
		// call StarMsg_DisplayTimedTextToPlayer(LoadPlayerHandle(YDLOC, GetHandleId(GetTriggeringTrigger()) + ydl_localvar_step * 0x80000, 0x48E3E75F) , 0.0 , 0.0 , 8.00 , "|cffe6ceac效果：|r" , 0.00)
		// call StarMsg_DisplayTimedTextToPlayer(LoadPlayerHandle(YDLOC, GetHandleId(GetTriggeringTrigger()) + ydl_localvar_step * 0x80000, 0x48E3E75F) , 0.0 , 0.0 , 8.00 , "|cffbee7e9生命值 + 1000|r" , 0.00)
		// call StarMsg_DisplayTimedTextToPlayer(LoadPlayerHandle(YDLOC, GetHandleId(GetTriggeringTrigger()) + ydl_localvar_step * 0x80000, 0x48E3E75F) , 0.0 , 0.0 , 8.00 , "|cffbeedc7抗性提升·中（被动）：降低15%自身受到的法术伤害。|r" , 0.00)

    Pattern itemNamePattern = Pattern.compile("YDWEUnitHasItemOfTypeBJNull\\(LoadUnitHandle\\(YDLOC, GetHandleId\\(GetTriggeringTrigger\\(\\)\\) \\+ ydl_localvar_step \\* \\w+, \\w+\\) , '(\\w{4})'");
    Pattern propPattern = Pattern.compile("call StarMsg_DisplayTimedTextToPlayer\\(LoadPlayerHandle\\(YDLOC, GetHandleId\\(GetTriggeringTrigger\\(\\)\\) \\+ ydl_localvar_step \\* \\w+, \\w+\\) , 0\\.0 , 0\\.0 , 8\\.00 , \"(.+)\" , 0\\.00\\)");

    List<FunctionTaozhuang> resultList = functionDetail.getTaozhuangList();
    FunctionTaozhuang taozhuang =null;
    for (String row : fun.getRows()) {
      if(row.contains("if")){
        Matcher matcher = itemNamePattern.matcher(row);
        boolean find = false;
        while (matcher.find()) {
          if(find==false){
            find = true;
            taozhuang = new FunctionTaozhuang();
            resultList.add(taozhuang);
          }
          String itemId = matcher.group(1);
          taozhuang.itemList.add(itemId);
          System.out.println("itemId:"+itemId);
        }
        continue;
      }

      Matcher matcher = propPattern.matcher(row);
   
      while(matcher.find()){
        String prop = matcher.group(1);
        if(taozhuang.taozhuangName == null){
          if(prop.startsWith("|cff")){
            taozhuang.color = prop.substring(0,10);
            prop = prop.substring(10);
          }
          taozhuang.taozhuangName = prop.replace("|r", "") +"·套装";
        }
        if(prop.contains("效果")){
          taozhuang.taozhuangDesc = "";
        }
        if(taozhuang.taozhuangDesc!=null){
          taozhuang.taozhuangDesc += prop.replace("|r", "") + "|n";
        }
      }
      
    }

    return functionDetail;
    
  }
}
