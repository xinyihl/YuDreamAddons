package com.yudream.yudreamaddons.common.util;

import com.yudream.yudreamaddons.Configurations;
import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.Random;

@ZenRegister
@ZenClass("mods.yudreamaddons.YdUtils")
public class YdUtils {

    private static final int MIN_VALUE = Configurations.ELECTROMAGNETIC_GENERATOR.minValue;
    private static final int MAX_VALUE = Configurations.ELECTROMAGNETIC_GENERATOR.maxValue;

    @ZenMethod
    public static int getRandGeneratorEnergy(int x, int y, int z) {
        int seed = (x * 114514 + y * 114514 + z * 114514) & 0x7FFFFFFF;
        Random random = new Random(seed);
        double baseRandom = random.nextDouble();
        double zFactor = 1.0 / (1 + z);
        double distanceFactor = 1.0 / (1 + Math.abs((x % 1000) - (y % 1000)));
        double resultFactor = baseRandom * distanceFactor * zFactor;
        int result = (int) (resultFactor * (MAX_VALUE - MIN_VALUE) + MIN_VALUE);
        return Math.max(MIN_VALUE, Math.min(MAX_VALUE, result));
    }
}
