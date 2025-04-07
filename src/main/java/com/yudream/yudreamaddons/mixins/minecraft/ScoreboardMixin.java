package com.yudream.yudreamaddons.mixins.minecraft;

import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(Scoreboard.class)
public abstract class ScoreboardMixin {

    @Final
    @Shadow
    private Map<String, ScorePlayerTeam> teams;

    @Shadow
    public abstract void broadcastTeamCreated(ScorePlayerTeam playerTeam);

    @Shadow
    public abstract void removeTeam(ScorePlayerTeam playerTeam);

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
            method = "getTeam",
            at = @At("HEAD"),
            cancellable = true
    )
    private void injected1(String name, CallbackInfoReturnable<ScorePlayerTeam> cir) {
        ScorePlayerTeam scoreplayerteam = teams.get(name);
        if (scoreplayerteam == null) {
            scoreplayerteam = new ScorePlayerTeam((Scoreboard)(Object)this, name);
            teams.put(name, scoreplayerteam);
            broadcastTeamCreated(scoreplayerteam);
        }
        cir.setReturnValue(scoreplayerteam);
    }

    @Inject(
            method = "createTeam",
            at = @At("HEAD"),
            cancellable = true
    )
    private void injected2(String name, CallbackInfoReturnable<ScorePlayerTeam> cir) {
        if (name.length() > 16) {
            throw new IllegalArgumentException("The team name '" + name + "' is too long!");
        }
        ScorePlayerTeam scoreplayerteam = teams.get(name);
        if (scoreplayerteam != null) {
            removeTeam(scoreplayerteam);
        }
        scoreplayerteam = new ScorePlayerTeam((Scoreboard)(Object)this, name);
        teams.put(name, scoreplayerteam);
        broadcastTeamCreated(scoreplayerteam);
        cir.setReturnValue(scoreplayerteam);
    }
}
