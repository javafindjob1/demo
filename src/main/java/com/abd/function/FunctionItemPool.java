package com.abd.function;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.abd.Function;
import com.abd.MapUtil;
import com.abd.FunctionDetail.DropInfo;

import lombok.Data;

@Data
public class FunctionItemPool { 
  
  private double totalRandom;
  private List<DropInfo> itemList = new ArrayList<>();
  
  public static Map<String, List<DropInfo>> parse(Function fun) {
    Pattern itemPoolPattern = Pattern
        .compile(
            "call ItemPoolAddItemType\\(LoadItemPoolHandle\\(YDLOC, GetHandleId\\(GetTriggeringTrigger\\(\\)\\) \\+ ydl_localvar_step \\* 0x\\w{5}, (\\w{10})\\), '(\\w{4})', (\\d+(\\.\\d+)?)\\)");
    Pattern itemPoolRenamePattern = Pattern
        .compile(
            "call SaveItemPoolHandle\\(YDHT, GetHandleId\\(gg_unit_h00P_\\w+\\), (\\w+), LoadItemPoolHandle\\(YDLOC, GetHandleId\\(GetTriggeringTrigger\\(\\)\\) \\+ ydl_localvar_step \\* \\w+, (\\w+)\\)\\)");

    Map<String, FunctionItemPool> map = new HashMap<>();
    for (String row : fun.getRows()) {
      {
        Matcher matcher = itemPoolPattern.matcher(row);
        if (matcher.find()) {
          String poolId = matcher.group(1);
          String itemId = matcher.group(2);
          String random = matcher.group(3);
          System.out.println("物品池ID:" + poolId + ", ItemId:" + itemId +  ",random:" + random);
          FunctionItemPool itemPool = MapUtil.getNotNull(map, poolId, FunctionItemPool::new);
          DropInfo item = new DropInfo(itemId);
          item.setRate(Double.parseDouble(random));
          itemPool.itemList.add(item);
          itemPool.totalRandom+=item.getRate();
          continue;
        }
      }

      // 物品池改名
      {
        Matcher matcher = itemPoolRenamePattern.matcher(row);
        if (matcher.find()) {
          String newPoolId = matcher.group(1);
          String poolId = matcher.group(2);
          System.out.println("物品池newPoolId:" + newPoolId + ", poolId:" + poolId);
          FunctionItemPool itemPool = MapUtil.getNotNull(map, poolId, FunctionItemPool::new);
          map.put(newPoolId, itemPool);
          continue;
        }
      }
    }

    map.forEach((poolId,pool)->{
      pool.getItemList().forEach(item->{
        item.setRate(item.getRate()/pool.totalRandom);
      });
    });

    return map.entrySet().stream().collect(Collectors.toMap(e->e.getKey(), e->e.getValue().getItemList()));
  }
}
