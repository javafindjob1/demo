package com.abc;

import static org.junit.Assert.assertEquals;

import java.util.regex.Matcher;

import org.junit.Test;

public class FunctionParseTest {

  @Test
  public void testrandomPattern() throws Exception {
    {

      String row = "call SaveInteger(Ud,GetHandleId(GetTriggeringTrigger())*Yj,$50BBB8,GetRandomInt(1,7))";
      Matcher matcher = FunctionParse.randomIntPattern.matcher(row);
      if(matcher.find()){
        int i = 0;
        assertEquals("50BBB8", matcher.group(++i));
        assertEquals("7", matcher.group(++i));
        assertEquals(null, matcher.group(++i));
        assertEquals(null, matcher.group(++i));
        assertEquals(null, matcher.group(++i));
      }
    }

    {

      String row = "call SaveInteger(Ud,GetHandleId(GetTriggeringTrigger())*Yj,$50BBB8,GetRandomInt(1,7-vp))";
      Matcher matcher = FunctionParse.randomIntPattern.matcher(row);
      if(matcher.find()){
        int i = 0;
        assertEquals("50BBB8", matcher.group(++i));
        assertEquals("7-vp", matcher.group(++i));
        assertEquals("-vp", matcher.group(++i));
        assertEquals(null, matcher.group(++i));
        assertEquals("vp", matcher.group(++i));
      }
    }

    {

      String row = "call SaveInteger(Ud,GetHandleId(GetTriggeringTrigger())*Yj,$50BBB8,GetRandomInt(1,7-2*vp))";
      Matcher matcher = FunctionParse.randomIntPattern.matcher(row);
      if(matcher.find()){
        int i = 0;
        assertEquals("50BBB8", matcher.group(++i));
        assertEquals("7-2*vp", matcher.group(++i));
        assertEquals("-2*vp", matcher.group(++i));
        assertEquals("2*", matcher.group(++i));
        assertEquals("vp", matcher.group(++i));
      }
    }

    {

      String row = "call SaveInteger(Ud,GetHandleId(GetTriggeringTrigger())*Yj,$50BBB8,GetRandomInt(1,7+2*vp))";
      Matcher matcher = FunctionParse.randomIntPattern.matcher(row);
      if(matcher.find()){
        int i = 0;
        assertEquals("50BBB8", matcher.group(++i));
        assertEquals("7+2*vp", matcher.group(++i));
        assertEquals("+2*vp", matcher.group(++i));
        assertEquals("2*", matcher.group(++i));
        assertEquals("vp", matcher.group(++i));
      }
    }

    {

      String row = "call SaveInteger(Ud,GetHandleId(GetTriggeringTrigger())*Yj,$2970D116,GetRandomInt(1,18))";
      Matcher matcher = FunctionParse.randomIntPattern.matcher(row);
      if(matcher.find()){
        int i = 0;
        assertEquals("2970D116", matcher.group(++i));
        assertEquals("18", matcher.group(++i));
        assertEquals(null, matcher.group(++i));
        assertEquals(null, matcher.group(++i));
        assertEquals(null, matcher.group(++i));
      }
    }
  }

  @Test
  public void testEngine() throws Exception {
    Object o = FunctionParse.engine.eval("5*3");
    assertEquals(15, o);
  }


  @Test
  public void testRatePattern() throws Exception {
    {

      String row = "if LoadInteger(Ud,GetHandleId(GetTriggeringTrigger())*Yj,$50BBB8)>=2 and LoadInteger(Ud,GetHandleId(GetTriggeringTrigger())*Yj,$50BBB8)<=4 then";
      System.out.println(1234);
      Matcher matcher = FunctionParse.ratePattern.matcher(row);
      if(matcher.find()){
        int i = 0;
        assertEquals("50BBB8", matcher.group(++i));
        assertEquals(">=", matcher.group(++i));
        assertEquals("2", matcher.group(++i));
        assertEquals(null, matcher.group(++i));
        assertEquals(null, matcher.group(++i));
        assertEquals(null, matcher.group(++i));
        assertEquals(null, matcher.group(++i));
        assertEquals(null, matcher.group(++i));
      }
      if(matcher.find()){
        int i = 0;
        assertEquals("50BBB8", matcher.group(++i));
        assertEquals("<=", matcher.group(++i));
        assertEquals("4", matcher.group(++i));
        assertEquals(null, matcher.group(++i));
        assertEquals(null, matcher.group(++i));
        assertEquals(null, matcher.group(++i));
        assertEquals(null, matcher.group(++i));
        assertEquals(null, matcher.group(++i));
      }
    }
    {

      String row = "if LoadInteger(Ud,GetHandleId(GetTriggeringTrigger())*Yj,$50BBB8)>=40-2*Vp-3*LoadInteger(Ud,GetHandleId(GetTriggeringTrigger())*Yj,$838B24B5) then";
      Matcher matcher = FunctionParse.ratePattern.matcher(row);
      if(matcher.find()){
        int i = 0;
        assertEquals("50BBB8", matcher.group(++i));
        assertEquals(">=", matcher.group(++i));
        assertEquals("40", matcher.group(++i));
        assertEquals("-2*Vp", matcher.group(++i));
        assertEquals("2*", matcher.group(++i));
        assertEquals("Vp", matcher.group(++i));
        assertEquals("-3*LoadInteger(Ud,GetHandleId(GetTriggeringTrigger())*Yj,$838B24B5)", matcher.group(++i));
        assertEquals("3*", matcher.group(++i));
      }
    }

    {

      String row = "if RectContainsUnit(Kb,GetTriggerUnit())==true and LoadInteger(Ud,GetHandleId(GetTriggeringTrigger())*Yj,$50BBB8)>=40-2*Vp-3*LoadInteger(Ud,GetHandleId(GetTriggeringTrigger())*Yj,$838B24B5) then";
      System.out.println(1234);
      Matcher matcher = FunctionParse.ratePattern.matcher(row);
      if(matcher.find()){
        int i = 0;
        assertEquals("50BBB8", matcher.group(++i));
        assertEquals(">=", matcher.group(++i));
        assertEquals("40", matcher.group(++i));
        assertEquals("-2*Vp", matcher.group(++i));
        assertEquals("2*", matcher.group(++i));
        assertEquals("Vp", matcher.group(++i));
        assertEquals("-3*LoadInteger(Ud,GetHandleId(GetTriggeringTrigger())*Yj,$838B24B5)", matcher.group(++i));
        assertEquals("3*", matcher.group(++i));
      }
    }

    
    {

      String row = "if LoadInteger(Ud,GetHandleId(GetTriggeringTrigger())*Yj,$50BBB8)>=40-2*Vp-3*LoadInteger(Ud,GetHandleId(GetTriggeringTrigger())*Yj,$838B24B5) and RectContainsUnit(Kb,GetTriggerUnit())==true then";
      System.out.println(1234);
      Matcher matcher = FunctionParse.ratePattern.matcher(row);
      if(matcher.find()){
        int i = 0;
        assertEquals("50BBB8", matcher.group(++i));
        assertEquals(">=", matcher.group(++i));
        assertEquals("40", matcher.group(++i));
        assertEquals("-2*Vp", matcher.group(++i));
        assertEquals("2*", matcher.group(++i));
        assertEquals("Vp", matcher.group(++i));
        assertEquals("-3*LoadInteger(Ud,GetHandleId(GetTriggeringTrigger())*Yj,$838B24B5)", matcher.group(++i));
        assertEquals("3*", matcher.group(++i));
      }
    }

    {

      String row = "if LoadInteger(Ud,GetHandleId(GetTriggeringTrigger())*Yj,$50BBB8)>=40-2*Vp then";
      System.out.println(1234);
      Matcher matcher = FunctionParse.ratePattern.matcher(row);
      if(matcher.find()){
        int i = 0;
        assertEquals("50BBB8", matcher.group(++i));
        assertEquals(">=", matcher.group(++i));
        assertEquals("40", matcher.group(++i));
        assertEquals("-2*Vp", matcher.group(++i));
        assertEquals("2*", matcher.group(++i));
        assertEquals("Vp", matcher.group(++i));
        assertEquals(null, matcher.group(++i));
        assertEquals(null, matcher.group(++i));
      }
    }

    {

      String row = "if LoadInteger(Ud,GetHandleId(GetTriggeringTrigger())*Yj,$50BBB8)>=40-Vp then";
      System.out.println(1234);
      Matcher matcher = FunctionParse.ratePattern.matcher(row);
      if(matcher.find()){
        int i = 0;
        assertEquals("50BBB8", matcher.group(++i));
        assertEquals(">=", matcher.group(++i));
        assertEquals("40", matcher.group(++i));
        assertEquals("-Vp", matcher.group(++i));
        assertEquals(null, matcher.group(++i));
        assertEquals("Vp", matcher.group(++i));
        assertEquals(null, matcher.group(++i));
        assertEquals(null, matcher.group(++i));
      }
    }

    {

      String row = "if LoadInteger(Ud,GetHandleId(GetTriggeringTrigger())*Yj,$50BBB8)>=40 then";
      System.out.println(1234);
      Matcher matcher = FunctionParse.ratePattern.matcher(row);
      if(matcher.find()){
        int i = 0;
        assertEquals("50BBB8", matcher.group(++i));
        assertEquals(">=", matcher.group(++i));
        assertEquals("40", matcher.group(++i));
        assertEquals(null, matcher.group(++i));
        assertEquals(null, matcher.group(++i));
        assertEquals(null, matcher.group(++i));
        assertEquals(null, matcher.group(++i));
        assertEquals(null, matcher.group(++i));
      }
    }

  }
}
