package com.yudream.yudreamaddons.mixins.valkyrielib;

import com.valkyrieofnight.vliblegacy.lib.sys.tile.plugin.ITlePlugin;
import com.valkyrieofnight.vliblegacy.lib.tile.VLTileOwned;
import com.valkyrieofnight.vliblegacy.lib.tile.tickable.VLTileTickable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@SuppressWarnings("rawtypes")
@Mixin(value = VLTileTickable.class, remap = false)
public abstract class VLTileTickableMixin extends VLTileOwned {

    @Shadow
    private List<ITlePlugin> modules;

    @Shadow
    public abstract void updateTile();

    @Inject(
            method = "update",
            at = @At("HEAD"),
            cancellable = true,
            remap = true
    )
    private void onUpdate(CallbackInfo ci) {
        for (ITlePlugin iTileModule : this.modules) {
            iTileModule.update();
        }
        updateTile();
        ci.cancel();
    }
}
