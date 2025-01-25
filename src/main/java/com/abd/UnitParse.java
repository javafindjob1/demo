package com.abd;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.Collator;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.abd.FunctionDetail.DropInfo;

import lombok.Data;

@Data
public class UnitParse extends AbstractParse {

  private Map<String, UnitDetail> idUnitMap;

  public Map<String, UnitDetail> parse(List<Unit> units) {

    this.idUnitMap = units.stream().map(e -> {
      UnitDetail uDetail = new UnitDetail();

      // 生成代码将 e转换成uDetail 对象
      uDetail.setId(e.getId());
      uDetail.setName(trimName(e.getName()));
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
      uDetail.setPoints(e.getPoints());
      uDetail.setAbilList(e.getAbilList());
      uDetail.setHeroAbilList(e.getHeroAbilList());

      uDetail.setFile(e.getFile());

      uDetail.setArmor(e.getArmor());

      uDetail.setUbertip(e.getUbertip());

      uDetail.setSellitems(e.getSellitems());

      return uDetail;
    }).collect(Collectors.toMap(UnitDetail::getId, Function.identity()));

    return this.idUnitMap;
  }

  public void wrapDropString(Map<String, FunctionDetail> funDetailList, Map<String, ItemDetail> idItemMap) {
    // 汇总单位掉落信息 UNITID,SET<ITEMDETAIL>
    Map<String, Set<FunctionDetail.DropInfo>> unitIdAndItemMap = new HashMap<>();
    {
      Optional.ofNullable(funDetailList).ifPresent(list -> list.values().forEach(f -> {
        f.getItemMap().forEach((unitId, itemList) -> {
          Set<FunctionDetail.DropInfo> itemSet = MapUtil.getNotNull(unitIdAndItemMap, unitId, HashSet::new);
          itemSet.addAll(itemList);
        });
        f.getJuanzhouMap().forEach((unitId, juanzhouList) -> {
          Set<FunctionDetail.DropInfo> itemSet = MapUtil.getNotNull(unitIdAndItemMap, unitId, HashSet::new);
          itemSet.addAll(juanzhouList);
        });

      }));

    }

    // 添加掉落物品
    this.idUnitMap.values().forEach(uDetail -> {

      if (uDetail.getSellitems() != null) {
        List<FunctionDetail.DropInfo> list = Arrays.asList(uDetail.getSellitems().split(",")).stream().map(itemid -> {
          FunctionDetail.DropInfo item = new FunctionDetail.DropInfo(itemid);
          ItemDetail itemDetail = idItemMap.get(itemid);
          item.setItemName(itemDetail.getName());
          return item;
        }).collect(Collectors.toList());
        uDetail.setDropString(list);
      }

      Set<FunctionDetail.DropInfo> itemStrs = unitIdAndItemMap.get(uDetail.getId());

      if (itemStrs != null) {
        List<DropInfo> dropString = uDetail.getDropString();
        if (dropString == null) {
          uDetail.setDropString(itemStrs.stream().collect(Collectors.toList()));
        } else {
          dropString.addAll(itemStrs.stream().collect(Collectors.toList()));
        }
      }

    });

    // 备注信息归纳
    Map<String, Map<String, String>> allMap = getMark();
    Map<String, String> markMap = MapUtil.getNotNull(allMap, "备注", HashMap::new);

    idUnitMap.forEach((id,unit)->{
      String mark = markMap.get(id);
      if(mark !=null){
        unit.setMark(mark);
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
    divine("光"),
    small("风"),
    none("土"),
    large("水"),
    medium("火");

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
    chaos("水"),
    normal("土"),
    siege("火"),
    pierce("风");

    private String desc;

    private AtkType(String desc) {
      this.desc = desc;
    }

    public String getDesc() {
      return this.desc;
    }

  }

  public Map<String, List<UnitDetail>> getDropUnitOrder() {
    Map<String, List<UnitDetail>> map = new LinkedHashMap<>();
    {

      List<UnitDetail> unitCanDropList = idUnitMap.values().stream().filter(e -> {
        return e.getDropString() != null && e.getDropString().size() > 0;
      }).collect(Collectors.toList());

      Collections.sort(unitCanDropList, (o1, o2) -> {
        return Integer.parseInt(o1.getHp()) - Integer.parseInt(o2.getHp());
      });
      unitCanDropList.add(idUnitMap.get("n072"));
      unitCanDropList.add(idUnitMap.get("O00F"));
      map.put("按照血量排序", unitCanDropList);
    }

    {
      List<UnitDetail> unitCanDropList = idUnitMap.values().stream().filter(e -> {
        return (("111".equals(e.getPoints()) || "222".equals(e.getPoints()))
            && e.getPrimary().equals(UnitDetail.Primary.STR.getValue()));
      }).map(e -> {
        UnitDetail e2 = deepCopy(e);
        e2.setName(trimName(e2.getPropernames()));
        return e2;
      }).collect(Collectors.toList());

      Collator collator = Collator.getInstance(new Locale("zh", "CN"));
      Collections.sort(unitCanDropList, (o1, o2) -> {
        int i = o1.getDefType().compareTo(o2.getDefType());
        if (i != 0) {
          return i;
        }
        i = Double.parseDouble(o1.getCool1()) - Double.parseDouble(o2.getCool1()) > 0 ? 1 : -1;
        if (i != 0) {
          return i;
        }
        return collator.compare(o1.getName(), o2.getName());
      });
      map.put("冒险者-力量", unitCanDropList);
    }

    {
      List<UnitDetail> unitCanDropList = idUnitMap.values().stream().filter(e -> {
        return (("111".equals(e.getPoints()) || "222".equals(e.getPoints()))
            && e.getPrimary().equals(UnitDetail.Primary.AGI.getValue()));
      }).map(e -> {
        UnitDetail e2 = deepCopy(e);
        e2.setName(trimName(e2.getPropernames()));
        return e2;
      }).collect(Collectors.toList());

      Collator collator = Collator.getInstance(new Locale("zh", "CN"));
      Collections.sort(unitCanDropList, (o1, o2) -> {
        int i = o1.getDefType().compareTo(o2.getDefType());
        if (i != 0) {
          return i;
        }
        i = Double.parseDouble(o1.getCool1()) - Double.parseDouble(o2.getCool1()) > 0 ? 1 : -1;
        if (i != 0) {
          return i;
        }
        return collator.compare(o1.getName(), o2.getName());
      });
      map.put("冒险者-敏捷", unitCanDropList);
    }

    {
      List<UnitDetail> unitCanDropList = idUnitMap.values().stream().filter(e -> {
        return (("111".equals(e.getPoints()) || "222".equals(e.getPoints()))
            && e.getPrimary().equals(UnitDetail.Primary.INT.getValue()));
      }).map(e -> {
        UnitDetail e2 = deepCopy(e);
        e2.setName(trimName(e2.getPropernames()));
        return e2;
      }).collect(Collectors.toList());

      Collator collator = Collator.getInstance(new Locale("zh", "CN"));
      Collections.sort(unitCanDropList, (o1, o2) -> {
        int i = o1.getDefType().compareTo(o2.getDefType());
        if (i != 0) {
          return i;
        }
        i = Double.parseDouble(o1.getCool1()) - Double.parseDouble(o2.getCool1()) > 0 ? 1 : -1;
        if (i != 0) {
          return i;
        }
        return collator.compare(o1.getName(), o2.getName());
      });
      map.put("冒险者-智力", unitCanDropList);
    }
    return map;
  }

  @SuppressWarnings("unchecked")
  public <T extends Serializable> T deepCopy(T srcDetail) {
    try {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      ObjectOutputStream out = new ObjectOutputStream(bos);
      out.writeObject(srcDetail);

      ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
      ObjectInputStream in = new ObjectInputStream(bis);
      return (T) in.readObject();
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
      return null;
    }
  }

  public Map<String, List<UnitDetail>> getDropUnit() {
    Map<String, List<UnitDetail>> unitCanDropList = new LinkedHashMap<>();
    try {

      // 获取所有单位
      List<UnitDetail> unitCanDropList2 = idUnitMap.values().stream().filter(e -> {
        return e.getDropString() != null && e.getDropString().size() > 0;
      }).collect(Collectors.toList());

      Collections.sort(unitCanDropList2, (o1, o2) -> {
        return Integer.parseInt(o1.getHp()) - Integer.parseInt(o2.getHp());
      });
      unitCanDropList2.add(idUnitMap.get("n072"));
      unitCanDropList2.add(idUnitMap.get("O00F"));

      Map<String, UnitDetail> idUnitMap2 = new LinkedHashMap<>();
      unitCanDropList2.forEach(e -> {
        idUnitMap2.put(e.getId(), e);
      });

      // 归类单位信息
      InputStream file = UnitParse.class.getResourceAsStream("custom/unit.txt");
      Map<String, Map<String, String>> units = IniRead.read2(file);
      for (Entry<String, Map<String, String>> e : units.entrySet()) {
        String placeId = e.getKey();
        for (String unitId : e.getValue().keySet()) {
          List<UnitDetail> unitss = MapUtil.getNotNull(unitCanDropList, placeId, ArrayList::new);
          UnitDetail remove = idUnitMap2.remove(unitId);
          if (remove == null) {
            remove = idUnitMap.get(unitId);
          }
          unitss.add(remove);
        }
      }

      // 未归类的单位
      if (idUnitMap2.size() > 0) {
        List<UnitDetail> unitss = MapUtil.getNotNull(unitCanDropList, "未归类", ArrayList::new);
        unitss.addAll(idUnitMap2.values());
      }

    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("读取unit.txt文件异常" + e.getMessage());
    }

    return unitCanDropList;

  }

  public static void main(String[] args) throws Exception {
    List<Unit> list = new IniRead().read("template/Custom/unit.ini", "unit.ini", Unit.class);
    Map<String, UnitDetail> map = new UnitParse().parse(list);
    System.out.println(map.get("O009"));

    DefType def = DefType.valueOf("hero");
    System.out.println(def.name());

    InputStream file = UnitParse.class.getResourceAsStream("unit.txt");
    Map<String, Map<String, String>> units = IniRead.read2(file);
    System.out.println(units);

  }
}
