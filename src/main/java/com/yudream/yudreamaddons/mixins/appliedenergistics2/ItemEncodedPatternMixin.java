package com.yudream.yudreamaddons.mixins.appliedenergistics2;

import appeng.items.misc.ItemEncodedPattern;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static com.yudream.yudreamaddons.Configuration.patternEncoder;

@Mixin(value = ItemEncodedPattern.class, remap = false)
public abstract class ItemEncodedPatternMixin {

    @Inject(
            method = "addCheckedInformation",
            at = @At(
                    value = "TAIL"
            )
    )
    private void injected(CallbackInfo ci, @Local(name = "stack") ItemStack stack, @Local(name = "lines") List<String> lines) {
        if (patternEncoder) {
            NBTTagCompound tag = stack.getTagCompound();
            String encoder;
            if (tag != null && !(encoder = tag.getString("encoderName")).isEmpty()) {
                lines.add(String.format("由 %s 编码", encoder));
            }
        }
    }
}
