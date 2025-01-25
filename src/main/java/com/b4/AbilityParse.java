package com.b4;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AbilityParse {

  public static Map<String, AbilityDetail> parse(List<Ability> abilities) {
    Map<String, AbilityDetail> abilityDetailMap = new LinkedHashMap<>();
    for (Ability ability : abilities) {
      AbilityDetail ad = new AbilityDetail();
      ad.setId(ability.getId());
      ad.setHotkey(ability.getHotkey());
      ad.setName(ability.getTip());
      ad.setArt(ability.getArt());
      ad.setCool(ability.getCool().split("\\.")[0]);
      String ubertip = ability.getUbertip();
      if (ubertip != null && ubertip.contains("@,@")) {
        ubertip = ubertip.substring(ubertip.lastIndexOf("@,@") + 3);
      }
      ad.setUbertip(ubertip);
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
