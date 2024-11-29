package com.yudream.yudreamaddons.mixins.extrautils2;

import com.rwtema.extrautils2.tile.TileRainbowGenerator;
import com.yudream.yudreamaddons.Configurations;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = TileRainbowGenerator.class, remap = false)
public abstract class TileRainbowGeneratorMixin {

    @ModifyConstant(
            method = "update",
            constant = @Constant(intValue = 25000000),
            remap = true
    )
    private int injected(int constant) {
        return Configurations.RAINBOW_GENERATOR_ENERGY;
    }

    @ModifyConstant(
            method = "onBlockActivated",
            constant = @Constant(intValue = 25000000)
    )
    private int injected1(int constant) {
        return Configurations.RAINBOW_GENERATOR_ENERGY;
    }
}
