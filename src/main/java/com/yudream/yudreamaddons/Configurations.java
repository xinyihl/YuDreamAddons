package com.yudream.yudreamaddons;

import net.minecraftforge.common.config.Config;

@Config(modid = Tags.MOD_ID, name = Tags.MOD_NAME)
public class Configurations {
    @Config.Comment("符文祭坛是否消耗符文")
    public static boolean DO_RUNE_CONSUME = true;
    @Config.Comment("存储元件存储种类上限")
    public static int AE_TOTAL_TYPES = 192;
    @Config.Comment("编码样板显示由谁编码")
    public static boolean PATTERN_ENCODER = true;
    @Config.Comment("星辉等级上限")
    public static int AS_LEVEL_CAP = 300;
    @Config.Comment("不接触地面时是否受电线的伤害")
    public static boolean DO_ELECTRIC_UNGROUND = false;
    @Config.Comment("彩虹发电机发电量")
    public static int RAINBOW_GENERATOR_ENERGY = 2500000;
}