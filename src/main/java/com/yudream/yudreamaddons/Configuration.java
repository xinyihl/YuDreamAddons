package com.yudream.yudreamaddons;

import net.minecraftforge.common.config.Config;

@Config(modid = YuDreamAddons.MOD_ID)
public class Configuration {
    @Config.Comment("符文祭坛是否消耗符文")
    public static boolean duRuneConsume = true;
    @Config.Comment("存储元件存储种类上限")
    public static int aeTotalTypes = 1024;
    @Config.Comment("编码样板显示由谁编码")
    public static boolean patternEncoder = true;
}
