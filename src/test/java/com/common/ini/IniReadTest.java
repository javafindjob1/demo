package com.common.ini;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class IniReadTest {

  IniRead iniRead;

  @Before
  public void before() {
    iniRead = new IniRead();
  }

  // {1.0,2,3}
  // {1=1.0,2=2,3=3}
  @Test
  public void testNumberArr() {
    assertEquals("异常", iniRead.parseArr("{1.0}"), "1.0");
    assertEquals("异常", iniRead.parseArr("{1.0,2,3}"), "1.0@,@2@,@3");
    assertEquals("异常", iniRead.parseArr("{1=1.0,2=2,3=3}"), "1.0@,@2@,@3");
  }

  // {"a,=","b","c"}
  // {1="a,=",2="b",3="c"}
  @Test
  public void testStrArr() {
    assertEquals("异常", iniRead.parseArr("{\"a,=\",\"b\",\"c\"}"), "a,=@,@b@,@c");
    assertEquals("异常", iniRead.parseArr("{1=\"a,=\",2=\"b\",3=\"c\"}"), "a,=@,@b@,@c");
    assertEquals("异常", iniRead.parseArr("{1 = \"|cff99cc00类型： 输出|n属性： 敏捷|n伤害： 25|n范围： 弹射x4|n冷却： 7秒|n|n技能描述：|r|cffff4040|n|r|cff00ccff以极快的速度向目标单位跳跃，并依次攻击附近4个敌人，每个敌人造成相关敏捷的伤害。当完成最后一击时，还会对周围敌人造成一次伤害。|r\",2 = \"|cff99cc00类型： 输出|n属性： 敏捷|n伤害： 32|n范围： 弹射x4|n冷却： 7秒|n|n技能描述：|r|cffff4040|n|r|cff00ccff以极快的速度向目标单位跳跃，并依次攻击附近4个敌人，每个敌人造成相关敏捷的伤害。当完成最后一击时，还会对周围敌人造成一次伤害。|r\",}"), 
    "|cff99cc00类型： 输出|n属性： 敏捷|n伤害： 25|n范围： 弹射x4|n冷却： 7秒|n|n技能描述：|r|cffff4040|n|r|cff00ccff以极快的速度向目标单位跳跃，并依次攻击附近4个敌人，每个敌人造成相关敏捷的伤害。当完成最后一击时，还会对周围敌人造成一次伤害。|r@,@|cff99cc00类型： 输出|n属性： 敏捷|n伤害： 32|n范围： 弹射x4|n冷却： 7秒|n|n技能描述：|r|cffff4040|n|r|cff00ccff以极快的速度向目标单位跳跃，并依次攻击附近4个敌人，每个敌人造成相关敏捷的伤害。当完成最后一击时，还会对周围敌人造成一次伤害。|r");
  }
  
  @Test
  public void testLongStr(){
    assertEquals("异常", iniRead.parseArr("{1=[=[中华]=],2=[=[中国]=]}"), "中华@,@中国");

  }

  @Test
  public void testHero() throws Exception{
    Map<String, Map<String, String>> res = IniRead.read2(IniRead.class.getResourceAsStream("hero.ini"));
    System.out.println(res.get("heros"));
    System.out.println(res.get("ulti"));
    System.out.println(res.get("story"));

  }
  @Test
  public void testParseArr() {
    String str = "{1 = [=[每次技能上升LV时,体力就会永久增加一点]=],2 = [=[每次技能上升LV时,体力就会永久增加一点]=],3 = [=[每次技能上升LV时,体力就会永久增加一点]=],4 = [=[每次技能上升LV时,体力就会永久增加一点]=],5 = [=[每次技能上升LV时,体力就会永久增加一点]=],6 = [=[每次技能上升LV时,体力就会永久增加一点]=],7 = [=[每次技能上升LV时,体力就会永久增加一点]=],8 = [=[每次技能上升LV时,体力就会永久增加一点]=],9 = [=[每次技能上升LV时,体力就会永久增加一点]=],10 = [=[每次技能上升LV时,体力就会永久增加一点]=],11 = [=[每次技能上升LV时,体力就会永久增加一点]=],12 = [=[每次技能上升LV时,体力就会永久增加一点]=],13 = [=[每次技能上升LV时,体力就会永久增加一点]=],14 = [=[每次技能上升LV时,体力就会永久增加一点]=],15 = [=[每次技能上升LV时,体力就会永久增加一点]=],16 = [=[每次技能上升LV时,体力就会永久增加一点]=],17 = [=[每次技能上升LV时,体力就会永久增加一点]=],18 = [=[每次技能上升LV时,体力就会永久增加一点]=],19 = [=[每次技能上升LV时,体力就会永久增加一点]=],20 = [=[每次技能上升LV时,体力 就会永久增加一点]=],21 = [=[每次技能上升LV时,体力就会永久增加一点]=],22 = [=[每次技能上升LV时,体力就会永久增加一点]=],23 = [=[每次技能上升LV时,体力就会永久增加一点]=],24 = [=[每次技能上升LV时,体力就会永久增加一点]=],25 = [=[每次技能上升LV时,体力就会永久增加一点]=],26 = [=[每次技能上升LV时,体力就会永久增加一点]=],27 = [=[每次技能上升LV时,体力就会永久增加一点]=],28 = [=[每次技能上升LV时,体力就会永久增加一点]=],29 = [=[每次技能上升LV时,体力就会永久增加一点]=],30 = [=[每次技能上升LV时,体力就会永久增加一点]=],}";
    String res = IniRead.parseArr(str);
    System.out.println(res);
  }



}
