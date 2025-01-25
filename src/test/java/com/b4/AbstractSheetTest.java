package com.b4;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AbstractSheetTest {

    @InjectMocks
    private ItemParse itemParse;

    @Before
    public void setUp() {
    }
    /**
     * 黑剑
     * 
     */
    @Test
    public void testParseText_NormalItem()  {
        String desc = "|cffdaa520武器|n|n|r|cffff7f50攻击:99|n体力:55|n防御值:22|n生命值:500|n能力:黑天使Lv1(B级)|n被赐予的力量:0/3|n|n天使为了帮助那些受难人类种族,因为这样的原因,强大的恶魔,很气愤,于是用能力诅咒了她,当她每经过的地方都会开始腐烂,都会发生灾难!天使渐渐发出悲伤的声";
        List<String[]> text = AbstractSheet.parseText(desc);
        assertEquals("|cffdaa520:武器\n\n", text.get(0)[0]+":"+text.get(0)[1]);
        assertEquals("|cffff7f50:攻击:99\n", text.get(1)[0]+":"+text.get(1)[1]);
        assertEquals("|cffff7f50:体力:55\n", text.get(2)[0]+":"+text.get(2)[1]);
        assertEquals("|cffff7f50:防御值:22\n", text.get(3)[0]+":"+text.get(3)[1]);
        assertEquals("|cffff7f50:生命值:500\n", text.get(4)[0]+":"+text.get(4)[1]);
        assertEquals("|cffff7f50:能力:黑天使Lv1(B级)\n", text.get(5)[0]+":"+text.get(5)[1]);
        assertEquals("|cffff7f50:被赐予的力量:0/3\n\n", text.get(6)[0]+":"+text.get(6)[1]);
        assertEquals("|cffff7f50:天使为了帮助那些受难人类种族,因为这样的原因,强大的恶魔,很气愤,于是用能力诅咒了她,当她每经过的地方都会开始腐烂,都会发生灾难!天使渐渐发出悲伤的声", text.get(7)[0]+":"+text.get(7)[1]);
    }

}
