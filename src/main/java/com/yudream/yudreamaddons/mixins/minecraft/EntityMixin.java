package com.yudream.yudreamaddons.mixins.minecraft;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Inject(
            method = "setInvisible",
            at = @At("HEAD"),
            cancellable = true
    )
    @SuppressWarnings({"ConstantValue", "UnresolvedMixinReference"})
    private void injected(boolean invisible, CallbackInfo ci) {
        if (invisible && (Object)this instanceof EntityPlayer && ((EntityPlayer)(Object)this).isPotionActive(MobEffects.INVISIBILITY)) {
            ci.cancel();
        }
    }
}
