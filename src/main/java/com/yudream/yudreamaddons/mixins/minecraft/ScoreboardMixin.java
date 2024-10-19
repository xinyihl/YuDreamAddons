package com.yudream.yudreamaddons.mixins.minecraft;

import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Scoreboard.class)
public abstract class ScoreboardMixin {

    @Inject(
            method = "removeObjective",
            at = @At("HEAD"),
            cancellable = true
    )
    private void injected(ScoreObjective objective, CallbackInfo ci) {
        if(objective == null){
            ci.cancel();
        }
    }
}
