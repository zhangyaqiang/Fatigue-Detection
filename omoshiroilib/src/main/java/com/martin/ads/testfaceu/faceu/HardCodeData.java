package com.martin.ads.testfaceu.faceu;

public class HardCodeData {
    public static class EffectItem {
        public String name;
        public int type;
        public String unzipPath;

        public EffectItem(String name, int type, String unzipPath) {
            this.name = name;
            this.type = type;
            this.unzipPath = unzipPath;
        }
    }

//    public final static int TYPE_CHANGE_FACE       = 0;
//    public final static int TYPE_DYNAMIC_STICKER   = 1;
//    public final static int TYPE_SWITCH_FACE       = 2;
//    public final static int TYPE_MULTI_SECTION     = 3;
//    public final static int TYPE_MULTI_TRIANGLE    = 4;  // 注意强制更新的内容
//    public final static int TYPE_TWO_PEOPLE_SWITCH = 5;
//    public final static int TYPE_CLONE_PEOPLE_FACE = 6;
    public static EffectItem[] sItems = new EffectItem[]{
        new EffectItem("faceu_effects/900029_5.zip", 3, "900029_5"), //smallmouth
        new EffectItem("faceu_effects/50291_3.zip", 3, "50291_3"), //fatface
        new EffectItem("faceu_effects/170009_2.zip", 2, "170009_2"), //bigeye
        new EffectItem("faceu_effects/170010_1.zip", 2, "mirrorface"),
        new EffectItem("faceu_effects/50109_2.zip", 1, "weisuo"),
        new EffectItem("faceu_effects/20088_1_b.zip", 3, "animal_catfoot_b"),
    };

}
