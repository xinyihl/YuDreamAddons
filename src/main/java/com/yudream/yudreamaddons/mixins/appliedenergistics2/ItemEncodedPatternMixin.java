package com.yudream.yudreamaddons.mixins.appliedenergistics2;

import appeng.items.misc.ItemEncodedPattern;
import com.glodblock.github.loader.FCItems;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.resources.I18n;
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
        boolean isFCencode = stack.getItem() == FCItems.DENSE_ENCODED_PATTERN || stack.getItem() == FCItems.LARGE_ITEM_ENCODED_PATTERN;
        if (patternEncoder && !isFCencode) {
            NBTTagCompound tag = stack.getTagCompound();
            if (tag != null && tag.hasKey("encoderName")) {
                lines.add(I18n.format("ae2fc.tooltip.pattern_encoder.name", tag.getString("encoderName")));
            }
        }
    }
}
