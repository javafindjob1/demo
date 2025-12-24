package com.dj.parse;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.common.ini.Ability;
import com.common.ini.IniRead;
import com.common.parse.AbstractParse;
import com.dj.Client;

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
      if (x == null || y == null) {
        System.out.println("技能坐标未找到！！！");
      }
      if (y.equals("1")) {
        if (x.equals("0")) {
          ad.setHotkey("Z");
        } else if (x.equals("1")) {
          ad.setHotkey("X");
        } else if (x.equals("2")) {
          ad.setHotkey("C");
        } else {
          ad.setHotkey("V");
        }
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
      } else {
        ad.setHotkey("B");
      }
      String ubertip = ability.getUbertip();
      if (ubertip != null && ubertip.contains("@,@")) {
        ubertip = ubertip.substring(ubertip.lastIndexOf("@,@") + 3);
      }
      ubertip = parseUbertip(ability.getId(), ubertip);
      ad.setUbertip(ubertip);

      // 备注
      String markStr = markMap.get(ability.getId());
      if (markStr != null)
        ad.setMark(markStr);

      abilityDetailMap.put(ad.getId(), ad);
    }
    return abilityDetailMap;
  }

  private static Pattern colorPattern = Pattern.compile("\\|[cffCFF]{3}\\w{3,6}");
  private static Pattern singlePattern = Pattern.compile("(?<![\\|\\w])([nr])(?!\\w)");
  private static Pattern doublePattern = Pattern.compile("\\|\\|");

  public static String parseUbertip(String id, String description) {
    if (description == null) {
      return description;
    }
    {
      // 将|Cff之类的替换成小写
      StringBuffer sb = new StringBuffer();
      Matcher matcher = colorPattern.matcher(description);
      while (matcher.find()) {
        String color = matcher.group();
        matcher.appendReplacement(sb, color.toLowerCase());
      }
      matcher.appendTail(sb);
      description = sb.toString();
    }
    return description;
  }

  public static void main(String[] args) throws UnsupportedEncodingException, Exception {
    List<Ability> read2 = new IniRead().read("template/Custom/ability.ini",
        URLDecoder.decode(UnitParse.class.getResource("custom/ability.ini").getPath(), "utf8"), Ability.class);
    Map<String, AbilityDetail> map = parse(read2);
    System.out.println(read2);
  }
}
