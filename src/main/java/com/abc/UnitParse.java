package com.abc;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.Data;

@Data
public class UnitParse {

  private Map<String, UnitDetail> idUnitMap;

  public Map<String, UnitDetail> parse(List<Unit> units) {

    this.idUnitMap = units.stream().map(e -> {
      UnitDetail uDetail = new UnitDetail();

      // 生成代码将 e转换成uDetail 对象
      uDetail.setId(e.getId());
      uDetail.setName(e.getName());
      uDetail.setNameReal(e.getName());
      uDetail.setLevel(e.getLevel());

      uDetail.setPropernames(e.getPropernames());
      uDetail.setHp(e.getHP());
      uDetail.setManaN(e.getManaN());
      uDetail.setPrimary(e.getPrimary());
      uDetail.setSTR(e.getSTR());
      uDetail.setAGI(e.getAGI());
      uDetail.setINT(e.getINT());

      uDetail.setDefType(convertDefType(e.getDefType()));
      uDetail.setDefTypeOri(e.getDefType());
      uDetail.setDef(e.getDef());
      if(e.getDef().contains(".")){
        uDetail.setDef(e.getDef().split("\\.")[0]);
      }
      uDetail.setDamageReduce(damageReduce(e.getDef()));
      uDetail.setSpd(e.getSpd());
      uDetail.setSight(e.getSight());
      uDetail.setNsight(e.getNsight());

      uDetail.setAtkType1(convertAtkType(e.getAtkType1()));
      uDetail.setAtkType1Ori(e.getAtkType1());
      uDetail.setDmgplus1(e.getDmgplus1());
      uDetail.setDice1(e.getDice1());
      uDetail.setSides1(e.getSides1());
      uDetail.setCool1(e.getCool1());
      uDetail.setRangeN1(e.getRangeN1());
      uDetail.setAcquire(e.getAcquire());

      uDetail.setArt(e.getArt());
      uDetail.setScoreScreenIcon(e.getScoreScreenIcon());
      uDetail.setType(e.getType());
      uDetail.setAbilList(e.getAbilList());
      uDetail.setHeroAbilList(e.getHeroAbilList());

      uDetail.setFile(e.getFile());

      return uDetail;
    }).collect(Collectors.toMap(UnitDetail::getId, Function.identity()));

    return this.idUnitMap;
  }

  public void wrapDropString(Map<String, FunctionDetail> funDetailList) {
    // 汇总单位掉落信息 UNITID,SET<ITEMDETAIL>
    Map<String, Set<FunctionDetail.Item>> unitIdAndItemMap = new HashMap<>();
    {
      Optional.ofNullable(funDetailList).ifPresent(list -> list.values().forEach(f -> {
        f.getItemMap().forEach((unitId, itemList) -> {
          Set<FunctionDetail.Item> itemSet = MapUtil.getNotNull(unitIdAndItemMap, unitId, HashSet::new);
          itemSet.addAll(itemList);
        });

      }));

    }

    // 添加掉落物品
    this.idUnitMap.values().forEach(uDetail -> {
      Set<FunctionDetail.Item> itemStrs = unitIdAndItemMap.get(uDetail.getId());
      if (itemStrs != null) {
        uDetail.setDropString(itemStrs.stream().collect(Collectors.toList()));
      }
    });

  }

  public String damageReduce(String def) {
    double def2 = Double.parseDouble(def);
    double a = 0.03;
    double d = a * def2 / (1 + a * def2);
    DecimalFormat decimalFormat = new DecimalFormat("0.00%");
    // 格式化百分比
    return decimalFormat.format(d);
  }

  public String convertDefType(String defType) {
    try {
      return DefType.valueOf(defType).getDesc();
    } catch (IllegalArgumentException e) {
      return "未知";
    }
  }

  public String convertAtkType(String atkType) {
    try {
      return AtkType.valueOf(atkType).getDesc();
    } catch (IllegalArgumentException e) {
      return "未知";
    }
  }

  private static enum DefType {
    hero("暗"),
    fort("光"),
    small("火"),
    none("水"),
    large("地"),
    medium("木");

    private String desc;

    private DefType(String desc) {
      this.desc = desc;
    }

    public String getDesc() {
      return this.desc;
    }

  }

  private static enum AtkType {
    hero("暗"),
    magic("光"),
    chaos("火"),
    normal("水"),
    siege("地"),
    pierce("木");

    private String desc;

    private AtkType(String desc) {
      this.desc = desc;
    }

    public String getDesc() {
      return this.desc;
    }

  }

  public Map<String, List<UnitDetail>> getDropUnitOrder() {
    List<UnitDetail> unitCanDropList = idUnitMap.values().stream().filter(e -> {
      return e.getDropString() != null && e.getDropString().size() > 0;
    }).collect(Collectors.toList());

    Collections.sort(unitCanDropList, (o1, o2) -> {
      return Integer.parseInt(o1.getHp()) - Integer.parseInt(o2.getHp());
    });
    unitCanDropList.remove(idUnitMap.get("U008"));
    unitCanDropList.add(idUnitMap.get("U008"));
    Map<String, List<UnitDetail>> map = new LinkedHashMap<>();
    map.put("按照血量排序", unitCanDropList);
    return map;
  }

  public Map<String, List<UnitDetail>> getDropUnit() {
    Map<String, List<UnitDetail>> unitCanDropList = new LinkedHashMap<>();
    try {

      Map<String, UnitDetail> getDropUnitOrder = getDropUnitOrder().get("按照血量排序").stream()
          .collect(Collectors.toMap(UnitDetail::getId, java.util.function.Function.identity()));

      InputStream file = UnitParse.class.getResourceAsStream("custom/unit.txt");
      Map<String, Map<String, String>> units = IniRead.read2(file);
      for (Entry<String, Map<String, String>> e : units.entrySet()) {
        String placeId = e.getKey();
        for (String unitId : e.getValue().keySet()) {
          List<UnitDetail> unitss = MapUtil.getNotNull(unitCanDropList, placeId, ArrayList::new);
          UnitDetail remove = getDropUnitOrder.remove(unitId);
          if (remove == null) {
            remove = idUnitMap.get(unitId);
          }
          unitss.add(remove);
        }
      }

      if (getDropUnitOrder.size() > 0) {

        List<UnitDetail> unitss = MapUtil.getNotNull(unitCanDropList, "未归类", ArrayList::new);
        unitss.addAll(getDropUnitOrder.values());
      }

    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("读取unit.txt文件异常" + e.getMessage());
    }

    return unitCanDropList;

  }

  public Map<String, UnitDetail> getHero() {
    Map<String, UnitDetail> map = idUnitMap.values().stream().filter(e -> {
      return e.getAbilList() != null && e.getAbilList().contains("A02Z") && e.getAbilList().contains("AInv");
    }).collect(Collectors.toMap(UnitDetail::getId, Function.identity()));

    return map;
  }

  public static void main(String[] args) throws Exception {
    List<Unit> list = new IniRead().read("template/Custom/unit.ini",
        UnitParse.class.getResource("custom/unit.ini").getPath(), Unit.class);
    UnitParse unitParse = new UnitParse();
    unitParse.parse(list);
    Map<String, UnitDetail> hero = unitParse.getHero();
    System.out.println(hero.size());
    System.out.println(hero.keySet());

    // DefType def = DefType.valueOf("hero");
    // System.out.println(def.name());

    // InputStream file = UnitParse.class.getResourceAsStream("unit.txt");
    // Map<String, Map<String, String>> units = IniRead.read2(file);
    // System.out.println(units);

  }
}
