package com.yudream.yudreamaddons.mixins.appliedenergistics2;

import appeng.container.AEBaseContainer;
import appeng.container.implementations.ContainerPatternEncoder;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.yudream.yudreamaddons.Configuration.patternEncoder;

@Mixin(value = ContainerPatternEncoder.class, remap = false)
public abstract class ContainerPatternEncoderMixin extends AEBaseContainer {

    public ContainerPatternEncoderMixin(InventoryPlayer ip, Object anchor) {
        super(ip, anchor);
    }

    @Inject(
            method = "encode()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;setTagCompound(Lnet/minecraft/nbt/NBTTagCompound;)V"
            )
    )
    private void injected(CallbackInfo ci, @Local(name = "encodedValue") NBTTagCompound encodedValue) {
        if (patternEncoder) {
            encodedValue.setString("encoderId", getInventoryPlayer().player.getGameProfile().getId().toString());
            encodedValue.setString("encoderName", getInventoryPlayer().player.getGameProfile().getName());
        }
    }
}
