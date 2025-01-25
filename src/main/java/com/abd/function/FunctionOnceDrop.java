package com.abd.function;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.abd.Function;
import com.abd.FunctionDetail;
import com.abd.FunctionDetail.DropInfo;

public class FunctionOnceDrop {

  public static List<DropInfo> parse(Function fun){
    // if ( canDrop ) then
		// call RandomDistReset()
		// call RandomDistAddItem('I00V', 100)
		// set itemID=RandomDistChoose()

    Pattern canDropPattern = Pattern.compile("if \\( canDrop \\) then");
		Pattern randomDistResetPattern = Pattern.compile("call RandomDistReset\\(\\)");
		Pattern randomDistAddItemPattern = Pattern.compile("call RandomDistAddItem\\('(\\w{4})', (\\d+)\\)");
		Pattern randomDistChoosePattern = Pattern.compile("set itemID=RandomDistChoose\\(\\)");


    List<DropInfo> items = new ArrayList<>();


    for (String row : fun.getRows()) {
      Matcher matcher = null;
      if (canDropPattern.matcher(row).find()) {
        System.out.println("canDrop");
        // do nothing
      } else if (randomDistResetPattern.matcher(row).find()) {
        System.out.println("randomDistReset");
        // do nothing
      } else if ((matcher=randomDistAddItemPattern.matcher(row)).find()) {
        String itemStr = matcher.group(1);
        String random = matcher.group(2);

        DropInfo item = new DropInfo(itemStr);
        item.setRate(Double.parseDouble(random)/100);
        item.setDesc("首次");
        items.add(item);
        System.out.println("itemStr:" + itemStr + " random:" + random);

        // do nothing
      } else if (randomDistChoosePattern.matcher(row).find()) {
        System.out.println("randomDistChoose");
      }
  
    }
    // 如果只有1个随机物品,概率值总和不为百分百,按照百分百来算
    double total = 0;
    for(DropInfo item : items){
      total += item.getRate();
    }
    for(DropInfo item : items){
      item.setRate(item.getRate()/total);
    }
    
    return items;
    
  }
}
