package com.yudream.yudreamaddons.mixins.appliedenergistics2;

import appeng.items.contents.CellConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import static com.yudream.yudreamaddons.Configuration.AE_TOTAL_TYPES;

@Mixin(value = CellConfig.class, remap = false)
public abstract class CellConfigMixin {
    /**
     * @author xinyihl
     * @reason 修改存储元件存储种类上限
     */
    @ModifyConstant(
            method = "<init>",
            constant = @Constant(intValue = 63)
    )
    private static int injected(int original) {
        return AE_TOTAL_TYPES;
    }
}
