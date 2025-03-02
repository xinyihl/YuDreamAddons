package com.yudream.yudreamaddons;

import com.cleanroommc.configanytime.ConfigAnytime;
import net.minecraftforge.common.config.Config;

@Config(modid = Tags.MOD_ID, name = Tags.MOD_NAME)
public class Configurations {

    @Config.Comment("基础设置")
    public static final GeneralConfig GENERAL_CONFIG = new GeneralConfig();

    @Config.Comment("其他设置")
    public static final OtherConfig OTHER_CONFIG = new OtherConfig();

    @Config.Comment("电磁发电机")
    public static final ElectromagneticGeneratorConfig ELECTROMAGNETIC_GENERATOR_CONFIG = new ElectromagneticGeneratorConfig();

    static {
        ConfigAnytime.register(Configurations.class);
    }

    public static class GeneralConfig {
        @Config.Comment("符文祭坛是否消耗符文")
        public boolean doRuneConsume = true;
        @Config.Comment("存储元件存储种类上限(生成世界后不要修改)")
        public int aeTotalTypes = 1024;
        @Config.Comment("编码样板显示由谁编码")
        public boolean patternEncoder = true;
        @Config.Comment("星辉等级上限")
        public int asLevelCap = 300;
        @Config.Comment("不接触地面时是否受电线的伤害")
        public boolean doElectricUnground = false;
        @Config.Comment("彩虹发电机发电量")
        public int rainbowGeneratorEnergy = 2500000;
        @Config.Comment("无线连接器是否可以跨维度")
        public boolean canRDimension = false;
    }

    public static class OtherConfig {
        @Config.Comment("神秘坩埚配方耗时")
        public int crucibleTime = 100;
        @Config.Comment("神秘源质配方耗时")
        public int smelterTime = 100;
        @Config.Comment("子节点能量消耗 = powerBase + powerDistanceMultiplier * Distance * ln(Distance^2 + 3) (AE/t)")
        public double powerBase = 1000D;
        public double powerDistanceMultiplier = 0.5D;
        @Config.Comment("主节点能量消耗 = powerHeadBase * size (子节点个数)")
        public double powerHeadBase = 10000D;
    }

    public static class ElectromagneticGeneratorConfig {
        @Config.Comment("最小发电量")
        public int minValue = 1000000;
        @Config.Comment("最大发电量")
        public int maxValue = 10000000;
    }
}
