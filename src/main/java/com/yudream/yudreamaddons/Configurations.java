package com.yudream.yudreamaddons;

import com.cleanroommc.configanytime.ConfigAnytime;
import net.minecraft.block.Block;
import net.minecraftforge.common.config.Config;

@Config(modid = Tags.MOD_ID, name = Tags.MOD_NAME)
public class Configurations {

    @Config.Comment("基础设置")
    public static final General GENERAL = new General();

    @Config.Comment("电磁发电机")
    public static final ElectromagneticGenerator ELECTROMAGNETIC_GENERATOR = new ElectromagneticGenerator();

    public static class General {
        @Config.Comment("符文祭坛是否消耗符文")
        public boolean doRuneConsume = true;
        @Config.Comment("存储元件存储种类上限")
        public int aeTotalTypes = 1024;
        @Config.Comment("编码样板显示由谁编码")
        public boolean patternEncoder = true;
        @Config.Comment("星辉等级上限")
        public int asLevelCap = 300;
        @Config.Comment("不接触地面时是否受电线的伤害")
        public boolean doElectricUnground = false;
        @Config.Comment("彩虹发电机发电量")
        public int rainbowGeneratorEnergy = 2500000;
    }

    public static class ElectromagneticGenerator {
        @Config.Comment("最小发电量")
        public int minValue = 1000000;
        @Config.Comment("最大发电量")
        public int maxValue = 10000000;
    }

    static {
        ConfigAnytime.register(Configurations.class);
    }
}
