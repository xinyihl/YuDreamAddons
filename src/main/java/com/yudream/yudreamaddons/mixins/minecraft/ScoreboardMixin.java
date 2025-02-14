package com.yudream.yudreamaddons.mixins.minecraft;

import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Scoreboard.class)
public abstract class ScoreboardMixin {

    @Shadow
    public abstract ScorePlayerTeam getTeam(String teamName);

    @Inject(
            method = "removeObjective",
            at = @At("HEAD"),
            cancellable = true
    )
    private void injected(ScoreObjective objective, CallbackInfo ci) {
        if (objective == null) {
            ci.cancel();
        }
    }

    @Inject(
            method = "createTeam",
            at = @At("HEAD"),
            cancellable = true
    )
    private void injected2(String name, CallbackInfoReturnable<ScorePlayerTeam> cir){
        if (name.length() > 16)
        {
            throw new IllegalArgumentException("The team name '" + name + "' is too long!");
        }
        ScorePlayerTeam scoreplayerteam = getTeam(name);
        if (scoreplayerteam != null)
        {
           cir.setReturnValue(scoreplayerteam);
        }
    }
}
