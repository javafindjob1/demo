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

public class FunctionSelect {

  private Map<String, List<DropInfo>> itemPoolMap;

  public FunctionSelect(Map<String, List<DropInfo>> itemPoolMap) {
    this.itemPoolMap = itemPoolMap;
  }

  public FunctionDetail parse(Function fun) {
    FunctionDetail functionDetail = new FunctionDetail();
    functionDetail.setRows(fun.getRows());
    functionDetail.setName(fun.getName());

    Pattern pattern = Pattern.compile(
        "call SaveItemHandle\\(YDLOC, GetHandleId\\(GetTriggeringTrigger\\(\\)\\) \\+ ydl_localvar_step \\* 0x\\w+, 0x\\w+, PlaceRandomItem\\(LoadItemPoolHandle\\(YDHT, GetHandleId\\(gg_unit_h00P_\\w+\\), (0x\\w+)\\)");
    fun.getRows().stream().filter(line -> line.contains("PlaceRandomItem")).findFirst().ifPresent(e -> {
      Matcher matcher = pattern.matcher(e);
      if (matcher.find()) {
        String poolId = matcher.group(1);

        List<DropInfo> items = MapUtil.getNotNull(functionDetail.getItemMap(), "n05R", ArrayList::new);
        List<DropInfo> poolItemList = itemPoolMap.get(poolId);
        assertNotNull("没有找到奖池", poolItemList);
        poolItemList.forEach(tmpItem -> {
          String itemstr = tmpItem.getItemId();
          DropInfo item = new DropInfo(itemstr);
          item.setRate(4.0/poolItemList.size());
          item.setDesc("选择装备");
          items.add(item);
        });
      }

    });
    ;

    return functionDetail;
  }
}
