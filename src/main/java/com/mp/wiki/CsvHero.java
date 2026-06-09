package com.mp.wiki;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSONObject;
import com.mp.Helper;
import com.mp.function.hero.Hero;
import com.mp.parse.AbilityDetail;
import com.mp.parse.ExcelImageInsert;
import com.mp.parse.UnitDetail;
import com.mp.parse.UnitParse;

public class CsvHero {
  static Map<String, Integer> indexMap = new HashMap<>();
  static {
    int index = 1;
    for (String key : Helper.pMap.keySet()) {
      indexMap.put(key, index++);
    }
  }
  static Map<String, Integer> sortMap = new HashMap<>();
  static {
    int i = 1;
    sortMap.put("狂战神", i++);
    sortMap.put("白银骑士", i++);
    sortMap.put("幻影魔猎", i++);
    sortMap.put("剑舞双月", i++);
    sortMap.put("自然共鸣使", i++);
    sortMap.put("天罚之烁", i++);
    sortMap.put("猫灵", i++);
    sortMap.put("烈斗", i++);
    sortMap.put("极杀寂刃", i++);
    sortMap.put("耀光射手", i++);
    sortMap.put("教宗", i++);
    sortMap.put("人鱼公主", i++);
    sortMap.put("岩之巨人", i++);
    sortMap.put("暗礁之龙", i++);
    sortMap.put("剑影", i++);
    sortMap.put("隐暗射手", i++);
    sortMap.put("雷魂", i++);
    sortMap.put("元素贤者", i++);
    sortMap.put("工程大师", i++);
    sortMap.put("天眷素人", i++);
    sortMap.put("无", i++);
    sortMap.put("重炮手", i++);
    sortMap.put("慷慨之人", i++);
    sortMap.put("天籁织韵", i++);
    sortMap.put("血猩棘刺", i++);
    sortMap.put("晨光之壁", i++);
    sortMap.put("苍岚", i++);
    sortMap.put("燎原赤剑", i++);
    sortMap.put("史莱姆国王", i++);
    sortMap.put("巡星", i++);
  }

  public static void main(String[] args) {

  }

  // 禁止出现| 防止wiki语法冲突
  public static String format(String str) {
    return str == null ? "" : str.replaceAll("\\|", "\\\\");
  }

  public static String read(String file) throws UnsupportedEncodingException, FileNotFoundException, IOException {
    StringBuilder buf = new StringBuilder();
    try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf8"))) {
      String line = null;
      while ((line = br.readLine()) != null) {
        buf.append(line).append("\\n");
      }
    }
    return buf.toString();
  }

  public static String parse(String str, int index) {
    Pattern p = Pattern.compile("^" + index + " = \"(.*)\",$", Pattern.MULTILINE);
    Matcher matcher = p.matcher(str);
    String result = "";
    while (matcher.find()) {
      result += "\n" + matcher.group(1);
    }
    if (result.length() > 0) {
      return result.substring(1);
    }
    return result;
  }

  public static String clear(String str) {
    return str.replaceAll("\\|cff\\w{6}", "").replaceAll("\\|r", "");
  }

  public static void insert(String mapName, String subname, Map<String, Hero[]> map)
      throws UnsupportedEncodingException, FileNotFoundException, IOException {

    // String 简介 =
    // read("D:\\Code\\demo\\demo\\src\\main\\java\\com\\mp\\custom\\简介.txt");
    // String 光晶 =
    // read("D:\\Code\\demo\\demo\\src\\main\\java\\com\\mp\\custom\\凝空光晶.txt");
    // String 专属灵格 =
    // read("D:\\Code\\demo\\demo\\src\\main\\java\\com\\mp\\custom\\专属灵格.ini");

    String 简介 = "Tip = {\n1 = \"|cffff00ff独行|r\",\n2 = \"|cff00ff00自然之心|r\",\n3 = \"|cffffcc00斯戴博安联邦|r\",\n4 = \"|cffff00ff独行|r\",\n5 = \"|cffff0000极东之国烨煌|r\",\n6 = \"|cffcc99ff蚀魔者|r\",\n7 = \"|cff00ffff海潮卫戍|r\",\n8 = \"|cffff00ff独行|r\",\n9 = \"|cffffff00光谕圣堂|r·[剑]\",\n10 = \"|cffff00ff独行|r\",\n11 = \"|cffffcc00斯戴博安联邦|r\",\n12 = \"|cffff00ff独行|r\",\n13 = \"|cffff00ff独行|r\",\n14 = \"|cffff00ff独行|r\",\n15 = \"|cffffff00光谕圣堂|r·[愈]\",\n16 = \"|cff00ffff海潮卫戍|r\",\n17 = \"|cffcc99ff蚀魔者|r\",\n18 = \"|cffffcc00斯戴博安联邦|r\",\n19 = \"|cff00ff00自然之心|r\",\n20 = \"|cffffff00光谕圣堂|r·[杖]\",\n21 = \"|cffff0000极东之国烨煌|r\",\n22 = \"|cffff0000极东之国烨煌|r\",\n23 = \"|cffcc99ff蚀魔者|r\",\n24 = \"|cffff00ff独行|r\",\n25 = \"|cffffff00光谕圣堂|r·[盾]\",\n26 = \"|cffcc99ff蚀魔者|r\",\n27 = \"|cffff0000极东之国烨煌|r\",\n28 = \"|cff00ff00自然之心|r\",\n29 = \"|cffff00ff独行|r\",\n30 = \"|cffd6d5b7旅行者简介|r\",\n}\n-- 提示工具 - 普通 - 扩展\nUbertip = {\n1 = \"|n巴尔萨克出生在一个并未完全开化的部族，族人大多蠢笨而粗鲁。但魔蚀侵袭之严重，让这帮迟钝的蛮族人也意识到世界开始倾覆。为此，部族不得不派出部落里的精英前往大陆的中心探索魔蚀的原因，他就是其中一个。外面的世界比他想象的更糟糕。当他重归部落时，却发现这里已是一片废墟。\",\n2 = \"|n由自然的地之力给养而来的猫灵对魔蚀并没有那么感兴趣。它只是在随意游玩，碰巧遇到了旅行者们，又碰巧心情好，就加入了小队。对他来说，没有比自由和快乐更重要的事了。\",\n3 = \"|n斯里薇尔，斯戴博安联邦的骑士。她以凡人之躯，赢得了战神之契。她的剑下救赎了无数生灵。然而，蔓延的魔蚀与世间的苦难，让她目睹了世界滑向深渊的轨迹。当希望如退潮般消散，这位骑士做出了自己的选择：她将踏上一条无光之路，去直面并了结那个一直被世界畏惧与搁置的终极灾难。\",\n4 = \"|n万是地精十兄弟中的老大。不同于他的兄弟们，万更喜欢研究机械和打造技术。此次他与冒险者们结伴，一方面是为解决魔蚀，让地精兄弟们的生意更加兴隆，另一方面也是为了研究如何将某些力量应用于生产生活中。\",\n5 = \"|n来自极东之国烨煌的女斗士，是烨煌公认的、身负巨熊血统的天选之人。她那碾压性的武力，源于血脉，更源于一颗永无止境的争强之心。平静令她困倦，唯有在生死一线的战斗中，她才能真正感受到自我的存在。她踏入广阔的天地，加入形形色色的冒险队伍：这一切，都只为了追寻能与她匹敌的、足以让她燃烧殆尽的强敌。\",\n6 = \"|n探险家罗克在一次山中奇遇后，成为了自然的一部分：字面意义上的一部分。他的躯体呈现出岩石的冷硬与苔藓的湿润，与山脉、森林再无分别。当他返回人类城镇，他本身就成了一个行走的怪谈，一个被同胞畏惧的“他者”。昔日的同伴对他敬而远之，温暖的家园将他拒之门外。从那时起，罗克的目标改变了：他不再探索外部世界的未知，而是转向探寻自身命运的谜题：如何从一个“活着的异类”，重新变回一个能被世界接纳的“人”。\",\n7 = \"|n在遥远的往昔，暗礁龙族曾是无可争议的七海之主。他们驾驭龙威，统御万流，其辉煌曾是海洋本身的声音。然而，荣光终被时光侵蚀，力量随潮水褪去，一场浩劫更令这古老龙族近乎化为历史中的一道剪影。美露莘，作为族群唯一的遗孤，背负着“最后海龙”的沉重冠冕。她纤柔而坚韧的身影，必须穿梭于沉没的古城与陌生的岸邦，去探索每一个被遗忘的角落，寻找那或许能使潮声再起的、关乎族群复兴的渺茫秘辛。\",\n8 = \"|n达克尼斯来自于以速度著称的幻影族，他的成绩在族群的培养体制中一直无人能比。在结业仪式上，因魔蚀而变异的怪物如潮涌般侵入族群，族人奋起抵抗，但最终只有达克尼斯侥幸逃脱。消沉了几年后，他决定募集伙伴，前往魔蚀爆发的中心解决这场旷世灾难。\",\n9 = \"|n作为光谕圣堂的剑士，蓓雷德肩负着教主交付的重任：在魔蚀的阴影彻底笼罩世界前，探寻将其根除的方法。她并非被动执行命令，而是将这份职责内化为自己的信念。征途之中，她那独特的剑术既是为未来而战的利器，也是庇护当下的坚盾。她的存在，对于怪物即是挥之不去的审判，对于平民则是不期而遇的庇护。\",\n10 = \"|n尽管故乡耀光森林在魔蚀中得以幸免，但赫卡莉心中的正义却无法安眠。象征光明的她，若对世间的苦难视而不见，那便是对自身信念最深的背叛。她与光之精灵一同启程，主动踏入外部世界的黑暗。\",\n11 = \"|n莎奈，斯戴博安联邦的精英重炮手。当这片大陆的灾难持续数年而未得解决时，联邦为防魔蚀蔓延至本土，派出了她：这位以重火力著称的战士，远渡重洋前来进行调查与援助。\",\n12 = \"|n一名神秘的刺客，据说他曾经是某个暗部组织的首领。由于魔蚀肆虐，他的组织所隶属的国家似乎已经灭亡，组织也被解散。无处可去的他决定踏上冒险旅途，希望找到自己的下一个归宿。\",\n13 = \"|n在远离魔蚀大陆的彼端，暗之国屹立于永夜之中。那里能人辈出，而雅弥正是其中翘楚：一位精通隐暗箭术的精英弓箭手。一次任务中，她突感意识恍惚，再回神时，天地已非故土。紫夜化作青空，幽光换作烈日，熟悉的暗影荡然无存。历经探查，她明白自己已坠入异界。为寻归途，她暂掩乡愁，踏入一支异乡冒险者的队伍，在未知的土地上拉开了一场穿越世界的序幕。\",\n14 = \"|n在自己的国度里，他曾是一位居无定所的浪人剑客，凭借精妙的剑术和强大的力量浪迹天涯。时空交叉，位面错乱，在一场酒醉过后他无意中闯入了魔蚀的世界。初到时的震撼并没有持续太久，他很快开始学习如何在陌生的大陆生存。他脱去身上格格不入的异国长跑，随手捡来的长剑成了他御敌的利器。\",\n15 = \"|n在光谕圣堂，海里尔已将一生的漫长岁月，奉献给了那座神赐的静默岗位。然而，魔蚀的阴影顷刻间吞没了凡世的喧嚣，将亘古未有的危机推至他的面前。这位曾经的守望者，无法再安于一方净土。他毅然执起圣杖，决意踏出圣堂。他不仅要守护最后的信仰之光，更要成为引路之人，带领所有在黑暗中挣扎的子民，直至寻回失落的黎明。\",\n16 = \"|n深海之中，美人鱼公主梅洛以她的法术守护着王国最后的微光，成为抵御魔蚀侵扰的坚盾。但当连番的灾难让海洋的歌声逐渐沉寂，她意识到固守已是徒劳。为了夺回族的安宁，梅洛毅然告别故土，向着一切黑暗的起点：遗失王都进发。她不仅是公主，更愿成为照亮这漫漫长夜的明灯，为所有海洋生灵寻回希望的曙光。\",\n17 = \"|n一场空前的雷暴将某个边陲小镇夷为平地，当救援者赶到时，整片焦土之上，只剩下一个活物：萨恩德。他从废墟中醒来，惊恐地发现自己的躯体发出了诡异的低鸣，皮肤上浮现出如同雷纹的烙印。他是唯一的幸存者，也是最诡异的谜题。萨恩德坚信，这场灾难与自己的异变，绝非自然所为，其背后必然与那侵蚀世界的“魔蚀”有关。为追寻真相，他步入了那些追寻魔蚀的冒险者之中。\",\n18 = \"|n来自斯戴博安联邦的她，以其神秘的兜帽装束、冰冷的姿态与惊人的占卜天赋而为人侧目。她一贯疏离，仿佛活在一层看不见的冰墙之后。然而，一次深度的冥想中，一个全新的、剧烈波动的未来景象强行闯入她的意识。那景象如此真切且迫近，令她无法再保持置身事外的冷漠。为此，这位观测命运者，决定亲自步入命运的洪流，加入了与之相关的冒险队伍。\",\n19 = \"|n他有很多的身份和传说，他以各种面目出现，鹰隼、鹿、狼，甚至是亦可枯朽的古树，没有人能说清楚哪一个才是他的本质。他是自然的共鸣使，任何胆敢亵渎时光古树的生物，都是他的敌人。当魔物开始打扰到他幽静的家园时，他意识到自己该走出森林做些什么了。\",\n20 = \"|n她是光谕圣堂的圣女，执掌着惩戒邪恶的罚光之力。她本应是神意的代行者，但魔蚀中怪物肆虐、生灵涂炭的景象，却深深灼伤了她的心。这份痛苦让她意识到，她的信仰不应只存在于高高的祭坛之上。于是，她走下神坛，走入尘埃，将那曾用于制裁的神力，化为庇护孤弱的温暖光芒。她无私的关怀极具感染力，如同黑暗中点燃的烛火，自然地在冒险队中营造出一种彼此信任、亲如家人的融洽氛围。\",\n21 = \"|n菲朗特匹斯德出生于一个平凡的农民家庭，从小家人就教导他要慷慨无私。他从不为自己索取任何回报，总是将荣誉和奖励让给他人。他的慷慨不仅体现在物质上，更体现在他对待每一个人的尊重与关怀上。而他的行为激励着每一个遇到他的人，让他们相信，即使在这个充满挑战的世界中，善良和慷慨依然能够带来希望和光明。\",\n22 = \"|n缪斯是一位来自极东之国烨煌的精灵族少女，她拥有操控音律和编织魔法的能力，音符既是她的朋友，也是她用以保护家园的武器。魔蚀肆虐的世界里，她用调谐的魔法为冒险者们带来胜利的希望。\",\n23 = \"|n通常来说，被魔蚀侵蚀的人类必定会异变成怪物。但艾达却是个例外，在魔蚀能量吞噬她的瞬间，她竟奇迹般地保留了人类形态，只从中获得了力量。更令人难以置信的是，艾达原本只是个普通少女，对如何掌控这份突如其来的力量一无所知。这场始于偶然的旅程将如何展开？无人能够预料。\",\n24 = \"|n萝丝芮梵，其名“血猩棘刺”，乃是为族群未来而战的吸血鬼贵族。当灾难“魔蚀”席卷人间，屠戮生灵，也断绝了血族延续的食粮。她手持长枪“蔷薇”，踏入这片死寂之地，并非为了慈悲，而是为了生存。她必须查明这场灾难的根源，为了吸血鬼不朽的未来，清扫一切障碍。无论是魔物，还是绝望，都将成为她枪下绽放的养料。\",\n25 = \"|n她来自彼岸大陆的光谕圣堂，乃是圣堂最坚固的盾。此次远渡重洋，并非为了征服，而是肩负着一项神圣的使命：追踪并探查那湮灭生灵、腐化大地的“魔蚀”的根源。她坚信，唯有理解这黑暗的本质，才能用光辉将其彻底净化。\",\n26 = \"|n阿利斯泰尔曾是追逐风之自由的法师，直至魔蚀吞噬了他的家园。灾难的诅咒将他的形态扭曲成了鹤形灵体。如今，哀伤的风暴缠绕着他的羽翼，但风中仍回荡着他清晰的人类意识：一个被困在永恒宿命里，永不屈服的守护之魂。\",\n27 = \"|n身负家仇与故土的灰烬，伊芙利特是一位追寻魔蚀根源的烨煌剑士。她以凡人之躯驾驭着危险的火焰魔法，手中红剑所向，誓以烈焰焚尽一切污秽。她行走于黑暗之中，是为那些被吞噬的生命讨回公道，也为在绝望之地燃起一丝永不熄灭的火光。\",\n28 = \"|n没人会在意它的故事，也没人想知道它来自哪个泥泞的沼泽或是哪座被遗忘的遗迹。|n在那些自诩高贵的英雄眼中，它，不过只是一只史莱姆。\",\n29 = \"|n诺拉来自一个早已与世界隔绝、被遗忘在星海之中的遥远大陆。她自幼便目睹了神秘的魔蚀现象如何缓慢地吞噬她故乡的星空与生机。年仅十六岁的她，毅然肩负起沉重的使命，独自踏上了通往未知领域的旅程。她唯一的目标，就是追寻魔蚀的源头，并找到彻底终结它的方法，以此挽救她日渐黯淡的家园。\",\n30 = \"|n诺拉来自一个早已与世界隔绝、被遗忘在星海之中的遥远大陆。她自幼便目睹了神秘的魔蚀现象如何缓慢地吞噬她故乡的星空与生机。年仅十六岁的她，毅然肩负起沉重的使命，独自踏上了通往未知领域的旅程。她唯一的目标，就是追寻魔蚀的源头，并找到彻底终结它的方法，以此挽救她日渐黯淡的家园。\",\n}\n";
    String 光晶 = "Ubertip = {\n1 = \"|cffbeedc7根据无畏跳斩（断）的技能等级提高自身物理伤害|r|n|n|cffd6d5b7你的目光落在凝空光晶上，晶体表面流转的光芒中，浮现出无数破碎的时空残影。|r\",\n2 = \"|cffbeedc7崩山裂地击的余震次数 + 3|r|n|n|cffd6d5b7你的目光落在凝空光晶上，晶体表面流转的光芒中，浮现出无数破碎的时空残影。|r\",\n3 = \"|cffbeedc7提高银之转化的攻击力转化率|r|n|n|cffd6d5b7你的目光落在凝空光晶上，晶体表面流转的光芒中，浮现出无数破碎的时空残影。|r\",\n4 = \"|cffbeedc7提高暴雨火箭弹的伤害系数|r|n|n|cffd6d5b7你的目光落在凝空光晶上，晶体表面流转的光芒中，浮现出无数破碎的时空残影。|r\",\n5 = \"|cffbeedc7提高魁斗的生命值上限加成|r|n|n|cffd6d5b7你的目光落在凝空光晶上，晶体表面流转的光芒中，浮现出无数破碎的时空残影。|r\",\n6 = \"|cffbeedc7岩之雨（T）不会再投出小型石块|r|n|n|cffd6d5b7你的目光落在凝空光晶上，晶体表面流转的光芒中，浮现出无数破碎的时空残影。|r\",\n7 = \"|cffbeedc7施放德拉贡之奔腾时会回复一定百分比最大生命值|r|n|n|cffd6d5b7你的目光落在凝空光晶上，晶体表面流转的光芒中，浮现出无数破碎的时空残影。|r\",\n8 = \"|cffbeedc7提高影葬的伤害系数|r|n|n|cffd6d5b7你的目光落在凝空光晶上，晶体表面流转的光芒中，浮现出无数破碎的时空残影。|r\",\n9 = \"|cffbeedc7提高月震的伤害减免效率|r|n|n|cffd6d5b7你的目光落在凝空光晶上，晶体表面流转的光芒中，浮现出无数破碎的时空残影。|r\",\n10 = \"|cffbeedc7圣光箭的发动次数 + 1|r|n|n|cffd6d5b7你的目光落在凝空光晶上，晶体表面流转的光芒中，浮现出无数破碎的时空残影。|r\",\n11 = \"|cffbeedc7提高瞄准的总体攻击力加成系数|r|n|n|cffd6d5b7你的目光落在凝空光晶上，晶体表面流转的光芒中，浮现出无数破碎的时空残影。|r\",\n12 = \"|cffbeedc7追袭将造成额外伤害|r|n|n|cffd6d5b7你的目光落在凝空光晶上，晶体表面流转的光芒中，浮现出无数破碎的时空残影。|r\",\n13 = \"|cffbeedc7提高自身3000点基础攻击力|r|n|n|cffd6d5b7你的目光落在凝空光晶上，晶体表面流转的光芒中，浮现出无数破碎的时空残影。|r\",\n14 = \"|cffbeedc7剑影分身额外产生一个残像|r|n|n|cffd6d5b7你的目光落在凝空光晶上，晶体表面流转的光芒中，浮现出无数破碎的时空残影。|r\",\n15 = \"|cffbeedc7裁决之雷将降低命中单位更多的护甲值|r|n|n|cffd6d5b7你的目光落在凝空光晶上，晶体表面流转的光芒中，浮现出无数破碎的时空残影。|r\",\n16 = \"|cffbeedc7提高水流冲击的伤害系数|r|n|n|cffd6d5b7你的目光落在凝空光晶上，晶体表面流转的光芒中，浮现出无数破碎的时空残影。|r\",\n17 = \"|cffbeedc7根据雷暴领域的等级提高自身法术伤害|r|n|n|cffd6d5b7你的目光落在凝空光晶上，晶体表面流转的光芒中，浮现出无数破碎的时空残影。|r\",\n18 = \"|cffbeedc7元素掌控提供更多的法术伤害加成|r|n|n|cffd6d5b7你的目光落在凝空光晶上，晶体表面流转的光芒中，浮现出无数破碎的时空残影。|r\",\n19 = \"|cffbeedc7提高自然瓦解的法术抗性削减效率|r|n|n|cffd6d5b7你的目光落在凝空光晶上，晶体表面流转的光芒中，浮现出无数破碎的时空残影。|r\",\n20 = \"|cffbeedc7降低曦光阵的冷却时间|r|n|n|cffd6d5b7你的目光落在凝空光晶上，晶体表面流转的光芒中，浮现出无数破碎的时空残影。|r\",\n21 = \"|cffbeedc7所有冒险者额外获得2次灵格刷新次数|r|n|n|cffd6d5b7你的目光落在凝空光晶上，晶体表面流转的光芒中，浮现出无数破碎的时空残影。|r\",\n22 = \"|cffbeedc7可移动型音符的伤害值提高10%，前进速度 + 40%|r|n|n|cffd6d5b7你的目光落在凝空光晶上，晶体表面流转的光芒中，浮现出无数破碎的时空残影。|r\",\n23 = \"|cffbeedc7极速掌掴的伤害系数提高10%|r|n|n|cffd6d5b7你的目光落在凝空光晶上，晶体表面流转的光芒中，浮现出无数破碎的时空残影。|r\",\n24 = \"|cffbeedc7提高永绽庭院提供的暗属性强化|r|n|n|cffd6d5b7你的目光落在凝空光晶上，晶体表面流转的光芒中，浮现出无数破碎的时空残影。|r\",\n25 = \"|cffbeedc7辐辉的作用次数 + 1|r|n|n|cffd6d5b7你的目光落在凝空光晶上，晶体表面流转的光芒中，浮现出无数破碎的时空残影。|r\",\n26 = \"|cffbeedc7飓风：赛克隆对命中单位造成额外伤害|r|n|n|cffd6d5b7你的目光落在凝空光晶上，晶体表面流转的光芒中，浮现出无数破碎的时空残影。|r\",\n27 = \"|cffbeedc7提高赤炎形态二的附加伤害|r|n|n|cffd6d5b7你的目光落在凝空光晶上，晶体表面流转的光芒中，浮现出无数破碎的时空残影。|r\",\n28 = \"|cffbeedc7提高凝胶元素化一的伤害系数|r|n|n|cffd6d5b7你的目光落在凝空光晶上，晶体表面流转的光芒中，浮现出无数破碎的时空残影。|r\",\n29 = \"|cffbeedc7穹之泪命中时附加额外的法术伤害|r|n|n|cffd6d5b7你的目光落在凝空光晶上，晶体表面流转的光芒中，浮现出无数破碎的时空残影。|r\",\n30 = \"---\",\n}\n";
    String 专属灵格 = "1 = \"|n|cffbeedc7攻击力 + 10000|r|n|cfff4606c物理抗性 - 3%|r|n|n|cffd6d5b7“我是个粗人。”|r\",\n2 = \"|n|cffbeedc7力量 + 75|n攻击速度 + 25%|n所有冲击波类技能伤害 + 5%|r|n|n|cffd6d5b7“其实...被扔出去的时候我还挺兴奋的。”|r\",\n3 = \"|n|cffbeedc7护甲 + 20|n银白守御的发动机率 + 3%|n每次被攻击都会以小型守御反击，造成银白守御反击系数×0.03的伤害。|r|n|n|cffd6d5b7“贯彻信念是一件极其困难的事情。”|r\",\n4 = \"|n|cffbeedc7全能力 + 40|n全体冒险者物理抗性 + 3%|n为全体冒险者提供3%额外伤害乘区。|r|n|n|cffd6d5b7“如果不是迫不得已，我也不想上前线。”|r\",\n5 = \"|n|cffbeedc7生命值 + 2000|n燃怒持续时间 + 0.5秒|n佩戴拳甲造成的爆炸伤害 + 25%|r|n|n|cffd6d5b7“让你尝尝老娘的拳头！”|r\",\n6 = \"|n|cffbeedc7攻击速度 + 50%|n投掷出的岩石或树木伤害 + 10%|n投掷猫灵的伤害 + 15%|r|n|n|cffd6d5b7“把人扔出去的感觉真的不错。”|r\",\n7 = \"|n|cffbeedc7经验获取率 + 10%|n暗礁爆炸力量系数 × 1.1|n魔龙撕咬自身等级系数 + 5|r|n|n|cffd6d5b7“幽渊蛰伏千年影，一啸潮崩万里云。”|r\",\n8 = \"|n|cffbeedc7敏捷 + 70|n基础敏捷值 × 1.10|n影葬命中时会附加一次暗影加护的伤害|r|n|n|cffd6d5b7“迅如疾风！”|r\",\n9 = \"|n|cffbeedc7攻击力 + 4500|n月升·疾发动0.3秒内受到攻击将会反击，同时解除所有眩晕状态|n如果拥有[蓓雷德的剑鞘]，原反击判定时间翻倍，反击时解除所有眩晕状态|r|n|n|cffd6d5b7“截剑式。”|r\",\n10 = \"|n|cffbeedc7敏捷 + 80|n基础敏捷值 × 1.10|n施放瞬光时会额外获得20%法术抗性|r|n|n|cffd6d5b7“我与赫尔姆拉的配合天衣无缝。”|r\",\n11 = \"|n|cffbeedc7攻击力 + 5000|r|n|cfff4606c攻击速度 - 20%|r|n|cffbeedc7重炮形态下，炸弹投掷会装填两发爆炸弹|r|n|n|cffd6d5b7“哈哈哈，烟花，好漂亮啊~”|r\",\n12 = \"|n|cffbeedc7攻击力 + 2000|n敏捷 + 80|n普通攻击伤害提高10%|r|n|n|cffd6d5b7“我很感谢现在的世界，我的武器终于可以不再用于夺取别人的性命。”|r\",\n13 = \"|n|cffbeedc7攻击力 + 2000|n攻击速度 + 20%|n暗能引爆的被动触发机率 + 12%|r|n|n|cffd6d5b7“沉溺于黑暗之中吧。”|r\",\n14 = \"|n|cffbeedc7攻击力 + 4000|n绝斩暴击倍率 + 0.2|r|n|n|cffd6d5b7“我已经发现你的弱点了。”|r\",\n15 = \"|n|cffbeedc7智力 + 200|n灵域的治疗效果扩大为全地图。|r|n|n|cffd6d5b7“真主与我同在！”|r\",\n16 = \"|n|cffbeedc7法术穿透 + 3%|n冷却缩短 + 3%|n施放潮涌后使用“A”向指定地点击出潮流冲击。|n海曦则全面强化派生技能。|r|n|n|cffd6d5b7“一定要，保护我的故乡！”|r\",\n17 = \"|n|cffbeedc7法术穿透 + 5%|n加深魔蚀侵染度。|r|n|n|cffd6d5b7“唔...唔哇啊啊啊啊啊啊啊...”|r\",\n18 = \"|n|cffbeedc7智力 + 100|n每施放5次技能，下次技能的技能威力额外提高40%|r|n|n|cffd6d5b7“愿银叶林的智慧指引你...现在，说出你灼烧着心脏的那个疑问吧。”|r\",\n19 = \"|n|cffbeedc7智力 + 100|n法术穿透 + 3%|n拟态射击有50%机率再次施放。|r|n|n|cffd6d5b7“自然是我真正的主母。”|r\",\n20 = \"|n|cffbeedc7基础智力值 × 1.10|n冷却缩短 + 3%|n自身罚光系技能对黑暗系怪物产生1.10额外克制。|r|n|n|cffd6d5b7“圣光将照亮我前行之路。”|r\",\n21 = \"|n|cffbeedc7全员灵格刷新次数 + 3|r|n|n|cffd6d5b7他，便是慷慨的化身。|r\",\n22 = \"|n|cffbeedc7智力 + 150|n跃动音符弹跳次数+2|n音律冷却时间-0.2秒|r|n|n|cffd6d5b7“喜欢我演奏的曲子吗？”|r\",\n23 = \"\",\n24 = \"|n|cffbeedc7最终伤害 + 5%|n血棘贯刺的最终伤害额外提高10%|n棘之枪可以在赤宴的影响下催生另一形态|r|n|n|cffd6d5b7「不必哀叹生命的短暂，毕竟连永恒…也曾在我枪下碎裂。」|r\",\n25 = \"|n|cffbeedc7力量 + 120|n天光裁决的下劈伤害额外提高20%。|n超越之光的效果提高6%|r|n|n|cffd6d5b7我的盾承载过往，我的剑开拓未来——而光，永不妥协。|r\",\n26 = \"|n|cffbeedc7敏捷 + 70|n攻击速度 + 80%|n风幻双身命中时额外附带敏捷×0.8的法术伤害。|r|n|n|cffd6d5b7“我聆听风的低语，却以鹤的形态回应。”|r\",\n27 = \"|n|cffbeedc7攻击力 + 3000|n渐炽提供的物理穿透加成额外提高原百分比的25%|r|n|n|cffd6d5b7“我斩出的不是剑光，是熔炼黄昏的流火。”|r\",\n28 = \"|n|cffbeedc7选择后，视作进行了一次形态变化。|r|n|n|cffd6d5b7“噗噜噗噜，噗噜噜噜噜噜噜”|r\",\n29 = \"|n|cffbeedc7智力 + 120|n光属性强化 + 3%|n星之心的效果提高10%。|r|n|n|cffd6d5b7“星辉的指引从来不是巧合。”|r\",\n";

    for (Hero[] list : map.values()) {
      String role = list[0].getUnit().getPropernames();
      role = clear(role);
      String uid = list[0].getUnit().getId();
      if (!indexMap.containsKey(uid)) {
        throw new RuntimeException("未找到角色索引 " + role);
      }
      int index = indexMap.get(uid);
      Hero hero = list[0];
      HeroJson json = new HeroJson();
      json.setSortVal(sortMap.get(role));
      json.setRole(role);
      json.setPrimary(UnitParse.convertPrimaryType(hero.getUnit().getPrimary()));
      json.setIntro(parse(简介, index));
      json.setGuangjing(parse(光晶, index));
      json.setLingge(parse(专属灵格, index));

      for (int i = 0; i < list.length; i++) {
        Hero detail = list[i];
        if (detail == null) {
          continue;
        }

        HeroJson.Hero h = new HeroJson.Hero();
        h.setName(detail.getUnit().getName());
        h.setPropernames(clear(detail.getUnit().getPropernames()));
        if(i>0){
          h.setBuff(detail.getIntro().getUbertip());
        }
        h.setAtkType1(detail.getUnit().getAtkType1());
        // h.setAtkType1(UnitParse.convertAtkType(detail.getUnit().getAtkType1()));
        h.setCool1(Double.parseDouble(detail.getUnit().getCool1()));
        h.setSpd(Integer.parseInt(detail.getUnit().getSpd()));
        h.getAbils().add(new HeroJson.Ability(detail.getQ()));
        h.getAbils().add(new HeroJson.Ability(detail.getW()));
        h.getAbils().add(new HeroJson.Ability(detail.getE()));
        h.getAbils().add(new HeroJson.Ability(detail.getR()));
        h.getAbils().add(new HeroJson.Ability(detail.getT()));
        json.getHeros().add(h);
        hero.getItemList().forEach(item -> {
          h.getItems().add(item.getName());
        });
      }

      StringBuilder buf = new StringBuilder();
      buf.append(JSONObject.toJSONString(json, true)).append("\n");
      boolean append = false;
      try (BufferedWriter wr = new BufferedWriter(
          new OutputStreamWriter(new FileOutputStream("wiki/"+role + ".json", append), "UTF-8"))) {
        wr.write(buf.toString());
      }
    }

    System.out.println("写入完成");
  }

  public static void wrapHero(Hero detail, String mapName, String role, int index, StringBuilder buf)
      throws IOException {
    buf.append("sortVal,role,name,propernames,primary,atkType1,cool1,spd,intro,guangjing,lingge,q,w,e,r,t\n");
    /*
     * {{英雄
     * |角色=狂战神
     * |名字=狂战神
     * |称谓=狂战神
     * |主属性=力量
     * |攻击类型=火
     * |攻击间隔=
     * |移动速度=
     * |索引=1
     * |复杂度=2
     * |定位=输出
     * }}
     * 
     */
    UnitDetail unit = detail.getUnit();
    String name = unit.getName();
    String propsname = unit.getPropernames().replaceAll("\\|cff\\w{6}", "").replaceAll("\\|r", "");

    buf.append(format(role)).append("=");
    buf.append("==简介==").append("\\n");
    buf.append("{{英雄").append("\\n");
    buf.append("|角色=").append(format(role)).append("\\n");
    buf.append("|名字=").append(format(name)).append("\\n");
    buf.append("|称谓=").append(format(propsname)).append("\\n");
    buf.append("|图片格式=").append("png").append("\\n");
    buf.append("|ID=").append(detail.getId()).append("\\n");
    String primary = "";
    switch (detail.getUnit().getPrimary()) {
      case "STR":
        primary = "力量";
        break;
      case "AGI":
        primary = "敏捷";
        break;
      case "INT":
        primary = "智力";
        break;
      default:
        break;
    }
    buf.append("|主属性=").append(primary).append("\\n");
    buf.append("|攻击类型=").append(format(detail.getUnit().getAtkType1())).append("\\n");
    buf.append("|攻击间隔=").append(format(detail.getUnit().getCool1())).append("\\n");
    buf.append("|移动速度=").append(format(detail.getUnit().getSpd())).append("\\n");
    buf.append("|索引=").append(index).append("\\n");
    buf.append("|复杂度=").append("").append("\\n");
    buf.append("|定位=").append("").append("\\n");
    buf.append("}}\\n");
    buf.append("\n");

    String fileName = "英雄-头像-" + role + ".png";
    File f = new File("wikiimageheros\\" + fileName);
    if (!f.exists()) {
      ExcelImageInsert.convertImageToPng(detail.getUnit().getArt().replace(".tga", ".blp"), f);
    }
  }
}
