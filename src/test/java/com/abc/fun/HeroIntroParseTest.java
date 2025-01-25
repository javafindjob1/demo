package com.abc.fun;

import static org.junit.Assert.assertEquals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class HeroIntroParseTest {

  @Test
  public void testHeadParse() {
    assertEquals("O018", "O018");
    assertEquals("O018", HeroIntroParse.headParse(
        "if GetUnitTypeId(GetTriggerUnit())=='O018' and WU[GetConvertedPlayerId(GetTriggerPlayer())]==false then"));
    assertEquals("bb[23]", HeroIntroParse
        .headParse("if GetTriggerUnit()==bb[23] and WU[GetConvertedPlayerId(GetTriggerPlayer())]==false then"));
  }

  @Test
  public void testSubname() {
    String unitId = "bb[1234]";
    String arrName = unitId.substring(0, unitId.indexOf("["));
    assertEquals("bb", arrName);

  }

  // "|cffccffff类型：|r|cffffcc99物理伤害、分身|n|r|cffccffff属性：|r|cffffcc99攻击力|n|r|cffccffff|n红翼天使在附近敌人身上创造|r|cffffce00分身|r|cffccffff进行攻击，分身拥有本体|r|cffffce0078%攻击力，|r|cffccffff额外|r|cffffce0060%攻击速度，|r|cffccffff且无法控制，但也不会受到伤害。最多制造|r|cffffcc002个|r|cffccffff分身|n|r|cffffce00持续5秒|r",

  @Test
  public void testControl() {
    // String desc =
    // "|cffccffff类型：|r|cffffcc99物理伤害、分身|n|r|cffccffff属性：|r|cffffcc99攻击力|n|r|cffccffff|n红翼天使在附近敌人身上创造|r|cffffce00分身|r|cffccffff进行攻击，分身拥有本体|r|cffffce0078%攻击力，|r|cffccffff额外|r|cffffce0060%攻击速度，|r|cffccffff且无法控制，但也不会受到伤害。最多制造|r|cffffcc002个|r|cffccffff分身|n|r|cffffce00持续5秒|r";
    String desc = "|cffccffff类型：|r|cffffcc99物理伤害（被动）|n|r|cffccffff属性：|r|cffffcc99攻击力|r|n|n|cffccffff纸牌秘书永久增加|r|cffffcc007500点|r|cffccffff攻击力，并且普通攻击有|r|cffffcc0020%|r|cffccffff几率造成无视防御的伤害。|r";

    // <p><span style="color:#ccffff">类型：</span><span
    // style="color:#ffcc99">物理伤害、分身<span></p>
    // <p><span style="color:#ccffff">属性：</span><span
    // style="color:#ffcc99">攻击力</span></p>
    // <br>
    // <p><span style="color:#ccffff">红翼天使在附近敌人身上创造</span><span
    // style="color:#ffce00">分身</span><span
    // style="color:#ccffff">进行攻击，分身拥有本体</span><span
    // style="color:#ffce00">78%攻击力，</span><span
    // style="color:#ccffff">额外</span><span
    // style="color:#ffce00">60%攻击速度，</span><span
    // style="color:#ccffff">且无法控制，但也不会受到伤害。最多制造</span><span
    // style="color:#ffcc00">2个</span><span style="color:#ccffff">分身</span></p>
    // <p><span style="color:#ffce00">持续5秒<span></p>

    String[] sdf = desc.split("\\|n");
    StringBuffer buf = new StringBuffer();
    String color = "|cffffffff";
    for (String string : sdf) {
      color = handle(string, buf, color);
    }
    System.out.println(buf);
  }

  @Test
  public void testhandle() {
    String desc = "神鹫天降";
    // String desc = "|cffccffff类型：|r|cffccffff物理伤害、分身";
    StringBuffer buf = new StringBuffer();
    String color = "|cffffffff";
    color = handle(desc, buf, color);
    System.out.println(buf);
  }

  public String handle(String str, StringBuffer buf, String color) {
    // |cffccffff类型：|r|cffffcc99物理伤害、分身|r
    str = str.replaceAll("\\|r\\|c", "|c");
    str = str.replaceAll("\\|r", "|cfffff");

    // 继承上一行的颜色
    if (!str.startsWith("|cff")) {
      str = color + str;
    }
    buf.append("<p>");
    Pattern p = Pattern.compile("(\\|cff([a-zA-Z0-9]{3,6}))(.*?)(?=((\\|cff)|$))");
    Matcher m = p.matcher(str);
    String lastColor = "";
    int start = buf.length();
    while (m.find()) {
      lastColor = m.group(1);
      String text = m.group(3);
      buf.append("</span>");
      buf.append("<span style=\"color:#" + m.group(2) + "\">");
      buf.append(text);
    }
    if (start < buf.length()) {
      buf.delete(start, start + "</span>".length());
      buf.append("</span>");
    }
    buf.append("</p>");

    return lastColor;
  }

}