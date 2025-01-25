package com.abc.fun;

import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.abc.Function;
import com.abc.ItemDetail;
import com.abc.MapUtil;

/**
 * 合成台
 * set aY[10]='uflg'
 */
public class HeroItemParse {

  /**
   * 
   * @param functions
   * @param heroNameArrMap
   * @return E018:[a1,a2,]
   * @throws Exception
   */
  public static Map<String, String[]> parse(List<Function> functions, Map<String, String> heroNameArrMap, Map<String, ItemDetail> idItemMap)
      throws Exception {
    Map<String, String> heroNoMap = new HashMap<>();

    for (String name : heroNameArrMap.keySet()) {
      if(name.contains("[")){
        String heroNo = name.substring(name.indexOf("[") + 1, name.indexOf("]"));
        heroNoMap.put(heroNo, heroNameArrMap.get(name));
      }
    }

    Map<String, String> juanzhouIdAndItemIdMap = new HashMap<>();
    for(ItemDetail item : idItemMap.values()){
      if(item.getJuanzhouId() !=null){
        juanzhouIdAndItemIdMap.put(item.getJuanzhouId(),  item.getId());
      }
    }

    Map<String, String[]> itemMap = new HashMap<>();
    Pattern pa = Pattern.compile("set \\w+\\[(\\d+)\\]='(\\w+)'");
    for (Function function : functions) {
      List<String> rows = function.getRows();
      for (int i = 0; i < rows.size(); i++) {
        String row = rows.get(i);
        // 定位到了英雄皮肤1描述的位置
        if (row.contains("[10]='uflg'")) {
          for (int j = i; j < rows.size(); j++) {
            Matcher matcher = pa.matcher(rows.get(j));
            if (matcher.find()) {
              Integer no = Integer.parseInt(matcher.group(1));

              String heroNo = no / 10 + "";
              Integer itemNo = no % 10;
              String juanzhouId = matcher.group(2);

              String unitId = heroNoMap.get(heroNo);
              String[] items = MapUtil.getNotNull(itemMap, unitId, () -> new String[4]);
              String itemId = juanzhouIdAndItemIdMap.get(juanzhouId);
              assertNotNull("未找到合成台里的卷轴" + juanzhouId +"对应的真实物品", itemId);
              items[itemNo] = itemId;
            }

          }

          return itemMap;
        }
      }
    }

    return itemMap;

  }
}
