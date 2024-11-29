package com.yudream.yudreamaddons.mixins.astralsorcery;

import com.yudream.yudreamaddons.Configurations;
import hellfirepvp.astralsorcery.common.constellation.perk.PerkLevelManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = PerkLevelManager.class, remap = false)
public abstract class PerkLevelManagerMixin {
    @Redirect(
            method = "loadFromConfig",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraftforge/common/config/Configuration;getInt(Ljava/lang/String;Ljava/lang/String;IIILjava/lang/String;)I"
            )
    )
    public int injected(Configurations instance, String name, String category, int defaultValue, int minValue, int maxValue, String comment) {
        return Configurations.AS_LEVEL_CAP;
    }
}
