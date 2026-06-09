package com.xi5.parse;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.common.ini.Ability;
import com.common.ini.IniRead;
import com.common.parse.AbstractParse;
import com.xi5.Client;

public class AbilityParse extends AbstractParse {

  public static Map<String, AbilityDetail> parse(List<Ability> abilities) {
    Map<String, AbilityDetail> abilityDetailMap = new LinkedHashMap<>();

    Map<String, Map<String, String>> mark = getMark(Client.class);
    Map<String, String> markMap = mark.get("备注");

    for (Ability ability : abilities) {
      AbilityDetail ad = new AbilityDetail();
      ad.setId(ability.getId());
      ad.setHotkey(ability.getHotkey());
      ad.setName(ability.getName());
      ad.setArt(ability.getArt());
      String x = ability.getButtonpos_1();
      String y = ability.getButtonpos_2();
      if (y.equals("1") && x.equals("2")) {
        ad.setHotkey("T");
      } else if (y.equals("2")) {
        if (x.equals("0")) {
          ad.setHotkey("Q");
        }
        if (x.equals("1")) {
          ad.setHotkey("W");
        }
        if (x.equals("2")) {
          ad.setHotkey("E");
        }
        if (x.equals("3")) {
          ad.setHotkey("R");
        }
      }
      String ubertip = ability.getUbertip();
      if (ubertip != null && ubertip.contains("@,@")) {
        String[] arr = ubertip.split("@,@");
        ad.setUbertips(arr);
        ubertip = arr[arr.length - 1];
      }
      ad.setUbertip(ubertip);

      // 备注
      String markStr = markMap.get(ability.getId());
      if (markStr != null)
        ad.setMark(markStr);

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
