package com.common.ini;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import lombok.Data;

/**
 * [heros]
 * sadf = sadf,sdf,sdf,
 * dff = sdf
 * [ultis]
 * sadf = "asdf"
 * ffff = "sdf"
 * [intros]
 * sadf=[=[
 * |cffffcc00神蝉侍女【白藤儿】|r
 * 
 * 单体打击能力 - |cffff0303★★★★☆|r
 * 群体打击能力 - |cffff0303★★★★☆|r
 * 生存防御能力 - |cffff0303★★|r
 * 战术辅助能力 - |cffff0303★★☆|r
 * 
 * |cffffcc00英雄技能|r
 * 三教霞光，神光辉烨，九天日晷，坚韧灵神，
 * |cffff0000古代仪式|r
 * 
 * |cffffcc00英雄介绍|r
 * 被白云山收养的神秘孤女，善良温顺，然而在地底魔人一战中爆发出了恐怖的力量，直接将魔人打成灰烬。关于白藤儿的身世，有人怀疑她的身体被生化改造过。
 * ]=]
 * [stories]
 * sadf=[=[
 * 皮肤/无皮:北境流云随机A装，神庙第二次流云对战变更为马尔科。
 * 皮肤：北境流云附送三个幻惑之粉，击败马尔科后获得135全属性。
 * 非剧情专属：天珑幻镜（占卜，击杀月幽龙蜥，罪神，第二次流云，马尔科，星界魔源母，大树几率获得，雪艾诺必出），幻穹弓（边荒竞技场合成）。
 * ]=]
 */
@Data
public class HeroGroup {
  /** 英雄组 */
  private Map<String, String[]> heros = new LinkedHashMap<>();
  /** 根据英雄ID查找基础英雄ID */
  private Map<String, String> baseHeros = new HashMap<>();
  /** 技能或者大招 */
  private Map<String, String[]> ultis = new HashMap<>();
  /** 简介或者增强内容 */
  private Map<String, String> intros = new HashMap<>();
  /** 专属剧情 自定义的内容 */
  private Map<String, String> stories = new HashMap<>();
  /** 凝空光晶（梦想远景） */
  private Map<String, String> magiccores = new HashMap<>();

  public HeroGroup(Map<String, Map<String, String>> iniMap) {

    Map<String, String> heros = iniMap.get("heros");
    for (Entry<String, String> entry : heros.entrySet()) {
      String baseHeroId = entry.getKey();
      String heroGroup = entry.getValue();

      Set<String> ids = new LinkedHashSet<>();
      ids.add(baseHeroId);
      if (heroGroup != null && heroGroup.length() > 0) {
        ids.addAll(Arrays.asList(heroGroup.split(",")));
      }
      this.heros.put(baseHeroId, ids.toArray(new String[] {}));

      for (String id : ids) {
        this.baseHeros.put(id, baseHeroId);
      }
    }

    Map<String, String> magiccores = iniMap.get("magiccores");
    if (magiccores != null) {
      this.magiccores = magiccores;
    }

    Map<String, String> ultis = iniMap.get("ultis");
    for (Entry<String, String> entry : ultis.entrySet()) {
      String heroId = entry.getKey();
      String ultiGroup = entry.getValue();

      Set<String> ulti = new LinkedHashSet<>();
      if (ultiGroup != null && ultiGroup.length() > 0) {
        ulti.addAll(Arrays.asList(ultiGroup.split(",")));
      }
      this.ultis.put(heroId, ulti.toArray(new String[] {}));
    }

    Map<String, String> intros = iniMap.get("intros");
    if (intros != null) {
      this.intros = intros;
    }
    Map<String, String> stories = iniMap.get("stories");
    if (stories != null) {
      this.stories = stories;
    }

  }

  public String getIntro(String id) {
    return this.intros.get(id);
  }

  public String[] getUlti(String id) {
    return this.ultis.get(id);
  }

  public String getTeamId(String id) {
    return this.baseHeros.get(id);
  }

  public String getMagic(String id) {
    String baseHeroId = getTeamId(id);
    return this.magiccores.get(baseHeroId);
  }

  public String getStory(String id) {
    String baseHeroId = getTeamId(id);
    return this.stories.get(baseHeroId);
  }

}
