package com.yudream.yudreamaddons;

import com.cleanroommc.configanytime.ConfigAnytime;
import net.minecraftforge.common.config.Config;

@Config(modid = Tags.MOD_ID, name = Tags.MOD_NAME)
public class Configurations {
    @Config.Comment("符文祭坛是否消耗符文")
    public static boolean doRuneConsume = true;
    @Config.Comment("存储元件存储种类上限")
    public static int aeTotalTypes = 1024;
    @Config.Comment("编码样板显示由谁编码")
    public static boolean patternEncoder = true;
    @Config.Comment("星辉等级上限")
    public static int asLevelCap = 300;
    @Config.Comment("不接触地面时是否受电线的伤害")
    public static boolean doElectricUnground = false;
    @Config.Comment("彩虹发电机发电量")
    public static int rainbowGeneratorEnergy = 2500000;

    static {
        ConfigAnytime.register(Configurations.class);
    }
}
