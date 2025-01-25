package com.b4.function;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.b4.FunctionDetail;
import com.b4.FunctionDetail.DropInfo;
import com.b4.Function;

public class FunctionWaJue {

  Map<String, List<DropInfo>> itemPoolMap;

  public FunctionWaJue(Map<String, List<DropInfo>> itemPoolMap) {
    this.itemPoolMap = itemPoolMap;
  }

  public FunctionDetail parse(Function fun) {

    FunctionDetail functionDetail = new FunctionDetail();
    functionDetail.setName(fun.getName());
    functionDetail.setRows(fun.getRows());
    List<DropInfo> items = functionDetail.getWaJueList();

    // call SaveItemHandle(YDLOC, GetHandleId(GetTriggeringTrigger()) +
    // ydl_localvar_step * 0x80000, 0xFA1EE6F8,
    // PlaceRandomItem(LoadItemPoolHandle(YDHT, GetHandleId(gg_unit_h00P_0929),
    // 0x398933AC), LoadReal(YDLOC, GetHandleId(GetTriggeringTrigger()) +
    // ydl_localvar_step * 0x80000, 0xA99320FA), LoadReal(YDLOC,
    // GetHandleId(GetTriggeringTrigger()) + ydl_localvar_step * 0x80000,
    // 0xFDF65382)))
    Pattern pattern = Pattern.compile(
        "call SaveItemHandle\\(YDLOC, GetHandleId\\(GetTriggeringTrigger\\(\\)\\) \\+ ydl_localvar_step \\* 0x\\w+, 0x\\w+, PlaceRandomItem\\(LoadItemPoolHandle\\(YDHT, GetHandleId\\(gg_unit_h00P_\\w+\\), (0x\\w+)\\)");
    Iterator<String> iterator = fun.getRows().stream().filter(line -> line.contains("PlaceRandomItem")).iterator();
    if (iterator.hasNext()) {
      String line = iterator.next();
      Matcher matcher = pattern.matcher(line);
      assertTrue("没有匹配到奖池"+line, matcher.find());

      String poolId = matcher.group(1);
      List<DropInfo> poolItemList = itemPoolMap.get(poolId);
      assertNotNull("没有找到奖池" + poolId, poolItemList);
      double poolRate = 1.0/poolItemList.size();
      poolItemList.forEach(tmpItem -> {
        String itemstr = tmpItem.getItemId();
        DropInfo item = new DropInfo(itemstr);
        item.setRate(0.2*poolRate);
        items.add(item);
      });
    }
    
    while(iterator.hasNext()) {
      String line = iterator.next();
      Matcher matcher = pattern.matcher(line);
      assertTrue("没有匹配到奖池", matcher.find());

      String poolId = matcher.group(1);
      List<DropInfo> poolItemList = itemPoolMap.get(poolId);
      assertNotNull("没有找到奖池", poolItemList);
      double poolRate = 1.0/poolItemList.size();
      poolItemList.forEach(tmpItem -> {
        String itemstr = tmpItem.getItemId();
        DropInfo item = new DropInfo(itemstr);
        item.setRate(0.1*poolRate);
        items.add(item);
      });
    }
    
    return functionDetail;
  }
}
