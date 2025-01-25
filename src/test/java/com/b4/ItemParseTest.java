package com.b4;

import java.util.*;
import java.math.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class ItemParseTest {

    @InjectMocks
    private ItemParse itemParse;

    private ItemDetail itemDetail;

    @Before
    public void setUp() {
        itemDetail = new ItemDetail("测试物品");
    }

    /**
     * 黑剑
     * 
     */
    @Test
    public void testParseDescription_NormalItem()  {
        String desc = "|cffdaa520武器|r|n|cffffd700|n攻击力:12000|n筋力:300|n敏捷:300|n生命:2w|n闪避:20|n魔抗:20%|n能力:二刀流大师（S）|n能力:损伤倍率调整（S）|n品质:传说|n|n由40层BOSS掉落的武器,极度吸引大多数玩家的眼光,是非常具备争议性的武器!(当黑白双剑拥有时桐人技能部分技能得到特殊提升并获得|r";
        ItemParse.parseDescription(itemDetail, desc);
        assertEquals("传说", itemDetail.getLevel());
        assertEquals(300, itemDetail.getStr());
        assertEquals(300, itemDetail.getAgi());
        assertEquals(0, itemDetail.getItn());
        assertEquals(20000, itemDetail.getHp());
        assertEquals(20, itemDetail.getMiss());
        assertEquals(20, itemDetail.getDef());
        assertEquals("S,S", itemDetail.getNengli());
    }

    /**
     * 清透的月牙之剑
     */
    @Test
    public void testParseDescription_NormalItem_I04R()  {
        String desc = "|cffdaa520武器|r|n|n|cffffd700攻击力:500|n攻击速度:50|n筋力:60|n敏捷:60|n体力:60|n大地精灵Lv1(E级)|n品质:传说|n|r|n神话中描述这把刀,在晚上就像一个月亮在照耀着前方";

        ItemParse.parseDescription(itemDetail, desc);

        assertEquals("传说", itemDetail.getLevel());
        assertEquals(60, itemDetail.getStr());
        assertEquals(60, itemDetail.getAgi());
        assertEquals(60, itemDetail.getItn());
        assertEquals(500, itemDetail.getAttack());
        assertEquals(0, itemDetail.getHp());
        assertEquals(0, itemDetail.getMiss());
        assertEquals(0, itemDetail.getDef());
        assertEquals("", itemDetail.getNengli());
    }
    @Test
    public void testParseDescription_NormalItem_I00P()  {
        String desc = "|cFF9999CCEX:嫉妒|r|n|cFF96A8CCEX:独占欲|r|n|cFF9999A6EX:俄罗斯蓝猫|r|n|cFFFFFF00季节为秋。|r|n|cFFFFCC33在那树叶开始发黄的时节，少年来到了夕渚镇。|n|r|n|cFF99999C少年最初遇到的，是一位独自发传单的可爱猫耳女仆。|r|n|cFFCCCCCC“Setaria”咖、咖啡馆。请多指教|r|n";
        ItemParse.parseDescription(itemDetail, desc);

        assertEquals("|cff9999ccEX:嫉妒|r|n|cff96a8ccEX:独占欲|r|n|cff9999a6EX:俄罗斯蓝猫|r|n|cffffff00季节为秋。|r|n|cffffcc33在那树叶开始发黄的时节，少年来到了夕渚镇。|n|r|n|cff99999c少年最初遇到的，是一位独自发传单的可爱猫耳女仆。|r|n|cffcccccc“Setaria”咖、咖啡馆。请多指教|r|n", itemDetail.getDescription());
        assertEquals("", itemDetail.getLevel());
        assertEquals(0, itemDetail.getStr());
        assertEquals(0, itemDetail.getAgi());
        assertEquals(0, itemDetail.getItn());
        assertEquals(0, itemDetail.getAttack());
        assertEquals(0, itemDetail.getHp());
        assertEquals(0, itemDetail.getMiss());
        assertEquals(0, itemDetail.getDef());
        assertEquals("", itemDetail.getNengli());
    }

    @Test
    public void testParseDropList_NormalItem_I00E()  {
        String desc = "|cffff0000迷宫37层-囚禁牢域（B）|r|cff800080|n|r|cffff00ff|n掉落物品|r|cffffff00:|r|cffff99cc无|r";
        ItemParse.parseDescription(itemDetail, desc);

        assertEquals("无", itemDetail.getDropList().get(0));
    }
    @Test
    public void testParseDropList_NormalItem_I019()  {
        String desc = "|cffff0000迷宫6层-禁地の森|r|cff800080|n|r|cffff00ff|n掉落物品:|r|cffffff00纯净宝石 |r|cff00ff00准弯刀|r|cffffff00 龙肩爪 |r|cff0000ff神秘：蓝雪晶 |r|n推荐:阿斯蒂芬空间";
        ItemParse.parseDescription(itemDetail, desc);

        assertEquals("纯净宝石", itemDetail.getDropList().get(0));
        assertEquals("准弯刀", itemDetail.getDropList().get(1));
        assertEquals("龙肩爪", itemDetail.getDropList().get(2));
        assertEquals("神秘：蓝雪晶", itemDetail.getDropList().get(3));
        assertEquals(4, itemDetail.getDropList().size());

    }
}