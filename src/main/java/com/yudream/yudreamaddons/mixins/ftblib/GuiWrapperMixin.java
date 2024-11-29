package com.yudream.yudreamaddons.mixins.ftblib;

import com.feed_the_beast.ftblib.lib.gui.GuiBase;
import com.feed_the_beast.ftblib.lib.gui.GuiWrapper;
import com.yudream.yudreamaddons.common.util.Utils;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GuiWrapper.class, remap = false)
public abstract class GuiWrapperMixin {
    @Shadow
    private GuiBase wrappedGui;

    @Inject(
            method = "handleMouseInput",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiScreen;handleMouseInput()V",
                    shift = At.Shift.AFTER
            ),
            cancellable = true,
            remap = true
    )
    public void injected(CallbackInfo ci) {
        if (Utils.isCleanroomLoader()) {
            int scroll = Mouse.getEventDWheel();
            this.wrappedGui.mouseScrolled(scroll);
            ci.cancel();
        }
    }
}
