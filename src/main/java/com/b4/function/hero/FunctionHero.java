package com.b4.function.hero;

import java.util.List;
import java.util.Map;

import com.b4.Function;
import com.b4.FunctionDetail;

/**
 * 英雄解析
 * 
 *
 */
public class FunctionHero {
  public FunctionDetail parse(Map<String, Function> funMap) {
    

    FunctionHeroMainProp FunctionHeroMainProp = new FunctionHeroMainProp();
    // <udg_ROCK, "力量">
    Map<String, String> udgHeroMainPropMap = FunctionHeroMainProp.parse(funMap);

    FunctionHeroName FunctionHeroName = new FunctionHeroName();
    // <O004 , udg_ROCK>
    Map<String, String> udgHeroNameMap = FunctionHeroName.parse(funMap);

    FunctionHeroJianjieOrDaZhao FunctionHeroJianjieOrDaZhao = new FunctionHeroJianjieOrDaZhao();
    // <O004 , List<String>>
    Map<String, List<String>> udgHeroJianjieOrDaZhaoMap = FunctionHeroJianjieOrDaZhao.parse(funMap);

    FunctionHeroDaZhaoAndCore FunctionHeroDaZhaoAndCore = new FunctionHeroDaZhaoAndCore();
    //<111|222, List<Map<udg_ROCK, ability>> <111|222, List<Map<O004, ability>> 
    Map<String, List<Map<String, String>>> udgHeroDazhaoAndCoreMap = FunctionHeroDaZhaoAndCore.parse(funMap);

    FunctionDetail functionDetail = new FunctionDetail();
    return functionDetail;
  }
}