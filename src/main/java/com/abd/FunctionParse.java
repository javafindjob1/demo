package com.abd;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.abd.FunctionDetail.DropInfo;
import com.abd.function.FunctionAllDrop;
import com.abd.function.FunctionItemPool;
import com.abd.function.FunctionJuanZhou;
import com.abd.function.FunctionNewItemFormula;
import com.abd.function.FunctionOnceDropByDestructable;
import com.abd.function.FunctionOnceDropByUnit;
import com.abd.function.FunctionSelect;
import com.abd.function.FunctionSpecialJuanzhou;
import com.abd.function.FunctionTaozhuang;
import com.abd.function.FunctionUnitDrop;
import com.abd.function.FunctionWaJue;
import com.abd.function.hero.FunctionHero;

import lombok.Data;

@Data
public class FunctionParse {

  private Map<String, FunctionDetail> functionDetailMap;

  public void wrapItem(Map<String, ItemDetail> idItemMap) {

    functionDetailMap.values().forEach(fun -> {

      // 物品名称汉化
      fun.getItemMap().values().forEach(e -> {
        e.forEach(e2 -> {
          e2.setItemName(idItemMap.get(e2.getItemId()).getName());
        });
      });

      fun.getJuanzhouMap().forEach((unitId, listJuanzhou) -> {
        listJuanzhou.forEach(e2 -> {
          // 查找卷轴归哪个物品
          idItemMap.values().stream().filter(item -> e2.getItemId().equals(item.getJuanzhouId())).findFirst()
              .ifPresent(item -> {
                e2.setItemName(item.getName() + "合成卷轴");
                item.setUnitId(unitId);
              });
          if (e2.getItemName() == null) {
            // 查找是否是物品
            assertNotNull("卷轴信息未找到:" + e2.getItemId(), idItemMap.get(e2.getItemId()));
            e2.setItemName(idItemMap.get(e2.getItemId()).getName());
          }
        });
      });

    });
  }

  public Map<String, FunctionDetail> parse(List<Function> functions) {
    List<FunctionDetail> functionDetails = new ArrayList<>();

    // java.utils.Function
    Map<String, Function> mapFun = functions.stream()
        .collect(Collectors.toMap(Function::getName, java.util.function.Function.identity()));

    // 物品池
    Map<String, List<DropInfo>> itemPoolMap = FunctionItemPool.parse(mapFun.get("Trig_I0_____________________uActions"));
    FunctionUnitDrop function = new FunctionUnitDrop(itemPoolMap);
    // M4-[怪物掉落]-普通
    functionDetails.add(function.parse(mapFun.get("Trig_M4______________________uActions")));
    // M4-[怪物掉落]-精英
    functionDetails.add(function.parse(mapFun.get("Trig_M5______________________uActions")));
    // M4-[怪物掉落]-霸主
    functionDetails.add(function.parse(mapFun.get("Trig_M6______________________uActions")));
    // M4-[怪物掉落]-材料
    functionDetails.add(function.parse(mapFun.get("Trig_M7______________________uActions")));

    // 单位首次掉落随机物品
    FunctionOnceDropByUnit FunctionOnceDropByUnit = new FunctionOnceDropByUnit(mapFun);
    functionDetails.add(FunctionOnceDropByUnit.parse(mapFun.get("CreateUnits")));

    // 开箱子获得随机物品
    FunctionOnceDropByDestructable FunctionOnceDropByDestructable = new FunctionOnceDropByDestructable(mapFun);
    functionDetails.add(FunctionOnceDropByDestructable.parse(mapFun.get("CreateDestructables")));

    // 卷轴出售信息
    FunctionJuanZhou FunctionJuanZhou = new FunctionJuanZhou();
    functionDetails.add(FunctionJuanZhou.parse(mapFun.get("Trig_I13_______________uActions")));
    // 特殊英雄卷轴出售信息
    FunctionSpecialJuanzhou FunctionSpecialJuanzhou = new FunctionSpecialJuanzhou();
    functionDetails.add(FunctionSpecialJuanzhou.parse(mapFun.get("Trig_I13____________________________uActions")));

    // 挖地随机爆食物
    FunctionWaJue functionWaJue = new FunctionWaJue(itemPoolMap);
    functionDetails.add(functionWaJue.parse(mapFun.get("Trig_YX_2______________________uActions")));

    // 套装信息
    FunctionTaozhuang functionTaozhuang = new FunctionTaozhuang();
    functionDetails.add(functionTaozhuang.parse(mapFun.get("Trig_TZ_3_______________uActions")));
    
    // 物品合成(会与物品本身提供的合成相同)
    FunctionNewItemFormula functionNewItemFormula = new FunctionNewItemFormula();
    functionDetails.add(functionNewItemFormula.parse(mapFun.get("Trig_I1_______________uActions")));

    // 概率boss掉落
    FunctionAllDrop functionAllDrop = new FunctionAllDrop();
    functionDetails.addAll(functionAllDrop.parse(mapFun));
    
    // 暗之黑狮选择装备
    FunctionSelect functionSelect = new FunctionSelect(itemPoolMap);
    functionDetails.add(functionSelect.parse(mapFun.get("Trig_SQ2_19___________________________uActions")));
    

    FunctionHero FunctionHero = new FunctionHero();
    functionDetails.add(FunctionHero.parse(mapFun));


    this.functionDetailMap = functionDetails.stream()
        .filter(e -> true)
        .collect(Collectors.toMap(FunctionDetail::getName, java.util.function.Function.identity()));
    return functionDetailMap;

  }

  public static void main(String[] args) throws IOException {
    List<Function> list = new FunctionRead().read(URLDecoder.decode(UnitParse.class.getResource("custom/war3map.j还原256.j").getPath(), "utf8"));
    Map<String, FunctionDetail> list2 = new FunctionParse().parse(list);
    // System.out.println(list2.size());
    // // list2.stream().filter(e ->
    // // "BOs".equals(e.getName())).forEach(System.out::println);
    // // list2.stream().filter(e ->
    // // "CGU".equals(e.getName())).forEach(System.out::println);
    // list2.stream().filter(e ->
    // "CGp".equals(e.getName())).forEach(System.out::println);

    // list2.stream().filter(e -> e.getSpecialRate() !=
    // null).forEach(System.out::println);

  }
}
