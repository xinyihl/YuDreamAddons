package com.yudream.yudreamaddons.mixins.astralsorcery;

import hellfirepvp.astralsorcery.common.constellation.perk.PerkLevelManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = PerkLevelManager.class, remap = false)
public abstract class PerkLevelManagerMixin {
    @ModifyConstant(
            method = "loadFromConfig",
            constant = @Constant(intValue = 100)
    )
    private static int injected(int original) {
        return 300;
    }
}
