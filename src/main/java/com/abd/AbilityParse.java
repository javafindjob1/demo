package com.abd;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AbilityParse extends AbstractParse {

  public static Map<String, AbilityDetail> parse(List<Ability> abilities) {
    Map<String, AbilityDetail> abilityDetailMap = new LinkedHashMap<>();

    Map<String, Map<String, String>> mark = getMark();
    Map<String, String> markMap = mark.get("备注");

    for (Ability ability : abilities) {
      AbilityDetail ad = new AbilityDetail();
      ad.setId(ability.getId());
      ad.setHotkey(ability.getHotkey());
      ad.setName(ability.getName());
      ad.setArt(ability.getArt());
      String ubertip = ability.getUbertip();
      if (ubertip != null && ubertip.contains("@,@")) {
        ubertip = ubertip.substring(ubertip.lastIndexOf("@,@") + 3);
      }
      ad.setUbertip(ubertip);

      // 备注
      String markStr = markMap.get(ability.getId());
      if(markStr!=null) ad.setMark(markStr);

      abilityDetailMap.put(ad.getId(), ad);
    }
    return abilityDetailMap;
  }

  public static void main(String[] args) throws UnsupportedEncodingException, Exception {
    List<Ability> read2 = new IniRead().read("template/Custom/ability.ini",
        URLDecoder.decode(UnitParse.class.getResource("custom/ability.ini").getPath(), "utf8"), Ability.class);
    Map<String, AbilityDetail> map = parse(read2);
    System.out.println(read2);
  }
}
