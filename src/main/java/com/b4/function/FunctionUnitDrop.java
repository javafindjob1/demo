package com.b4.function;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.b4.Function;
import com.b4.FunctionDetail;
import com.b4.MapUtil;
import com.b4.FunctionDetail.DropInfo;

public class FunctionUnitDrop {
  Map<String, List<DropInfo>> itemPoolMap;

  public FunctionUnitDrop(Map<String, List<DropInfo>> itemPoolMap) {
    this.itemPoolMap = itemPoolMap;
  }

  public FunctionDetail parse(Function fun) {
    Pattern randomSeedPattern = Pattern.compile(
        "call SaveInteger\\(YDLOC, GetHandleId\\(GetTriggeringTrigger\\(\\)\\) \\+ ydl_localvar_step \\* 0x\\w{5}, 0x\\w{8}, GetRandomInt\\((1, 100)\\)\\)");
    Pattern randomPattern = Pattern.compile(
        "LoadInteger\\(YDLOC, GetHandleId\\(GetTriggeringTrigger\\(\\)\\) \\+ ydl_localvar_step \\* 0x\\w{5}, 0x\\w{8}\\) \\<= (\\d+)");
    Pattern unitNamePattern = Pattern
        .compile(
            "GetUnitTypeId\\(LoadUnitHandle\\(YDLOC, GetHandleId\\(GetTriggeringTrigger\\(\\)\\) \\+ ydl_localvar_step \\* 0x\\w{5}, 0x\\w{8}\\)\\) == '(\\w{4})'");
    Pattern itemNamePattern = Pattern.compile(
        "call SaveItemHandle\\(YDLOC, GetHandleId\\(GetTriggeringTrigger\\(\\)\\) \\+ ydl_localvar_step \\* 0x\\w{5}, 0x\\w{8}, CreateItem\\('(\\w{4})',");
    Pattern itemRandomPattern = Pattern
        .compile(
            "call SaveItemHandle\\(YDLOC, GetHandleId\\(GetTriggeringTrigger\\(\\)\\) \\+ ydl_localvar_step \\* 0x\\w{5}, 0x\\w{8}, PlaceRandomItem\\(LoadItemPoolHandle\\(YDHT, GetHandleId\\(gg_unit_h00P_0929\\), (\\w{10})\\),");

    FunctionDetail functionDetail = new FunctionDetail();
    functionDetail.setName(fun.getName());
    functionDetail.setRows(fun.getRows());

    Map<String, List<DropInfo>> itemMap = functionDetail.getItemMap();
    List<String> unitIdList = new ArrayList<>();
    Integer seedStr = null;
    String itemId = null;
    for (String row : fun.getRows()) {
      {
        Matcher matcher = randomSeedPattern.matcher(row);
        if (matcher.find()) {
          String seed = matcher.group(1);
          System.out.println("随机种子:" + seed);
          continue;
        }
      }
      {
        if (row.contains("if")) {
          Matcher matcher = unitNamePattern.matcher(row);
          boolean find = false;
          while (matcher.find()) {
           
            String seed = matcher.group(1);
            System.out.println("怪ID:" + seed);
            if(find==false){
              find = true;
              //上次的怪清除
              unitIdList.clear();
            }
            unitIdList.add(seed);
          }

          if (find) {
            // 每出现一个怪，清空概率池
            seedStr = null;
            itemId = null;

            // 处理if中连带概率判断的情况
            Matcher matcher2 = randomPattern.matcher(row);
            if (matcher2.find()) {
              String seed = matcher2.group(1);
              System.out.println("概率:" + seed);
              seedStr = Integer.parseInt(seed);
            }

            continue;
          }

        }
      }

      {
        if (row.contains("if")) {
          Matcher matcher = randomPattern.matcher(row);
          if (matcher.find()) {
            String seed = matcher.group(1);
  
            if (seedStr == null) {
              seedStr = Integer.parseInt(seed);
            } else {
              seedStr = Integer.parseInt(seed) - seedStr;
            }
            System.out.println("概率:" + seed + " 有效概率为:" + seedStr);
            continue;
          }
        }
       
      }

      {
        Matcher matcher = itemNamePattern.matcher(row);
        if (matcher.find()) {
          String seed = matcher.group(1);
          System.out.println("固定爆:" + seed + " seedStr:" + seedStr);
          itemId = seed;
          
          for (String unitId : unitIdList) {
            List<DropInfo> items = MapUtil.getNotNull(itemMap, unitId, ArrayList::new);
            DropInfo item = new DropInfo(itemId);
            
            if (seedStr == null) {
              item.setRate(1.0);
            } else {
              item.setRate(seedStr * 1.0 / 100);
            }
            items.add(item);
          }
          continue;
        }
      }
      {
        Matcher matcher = itemRandomPattern.matcher(row);
        if (matcher.find()) {
          String seed = matcher.group(1);
          System.out.println("随机爆:" + seed + " seedStr:" + seedStr);
          itemId = seed;

          for (String unitId : unitIdList) {
            List<DropInfo> items = MapUtil.getNotNull(itemMap, unitId, ArrayList::new);
            List<DropInfo> poolItemList = itemPoolMap.get(itemId);
            assertNotNull("未找到物品池" + itemId, poolItemList);
            final Integer tmpSeedStr = seedStr;
            poolItemList.forEach(tmpItem -> {
              String itemstr = tmpItem.getItemId();
              DropInfo item = new DropInfo(itemstr);
              if (tmpSeedStr == null) {
                item.setRate(tmpItem.getRate());
              } else {
                item.setRate(tmpSeedStr * 1.0 / 100 * tmpItem.getRate());
              }
              items.add(item);
            });
          }

          continue;
        }
      }
    }

    return functionDetail;
  }

}
