package com.yudream.yudreamaddons.mixins.jei;

import com.llamalad7.mixinextras.sugar.Local;
import mezz.jei.gui.recipes.RecipesGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RecipesGui.class, remap = false)
public abstract class RecipesGuiMixin {
    @Inject(
            method = "updateLayout",
            at = @At(
                    value = "INVOKE",
                    target = "Lmezz/jei/api/gui/IDrawable;getHeight()I",
                    ordinal = 1
            ),
            cancellable = true
    )
    private void injected(CallbackInfo ci, @Local(name = "recipesPerPage") int recipesPerPage) {
        if (recipesPerPage < 0) {
            ci.cancel();
        }
    }
}
