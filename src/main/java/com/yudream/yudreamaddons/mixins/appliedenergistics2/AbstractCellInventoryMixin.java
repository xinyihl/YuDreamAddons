package com.yudream.yudreamaddons.mixins.appliedenergistics2;

import appeng.me.storage.AbstractCellInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import static com.yudream.yudreamaddons.Configuration.AE_TOTAL_TYPES;

@Mixin(value = AbstractCellInventory.class, remap = false)
public abstract class AbstractCellInventoryMixin {
    @Unique
    private static final String[] ITEM_SLOT_KEYS = new String[AE_TOTAL_TYPES];
    @Unique
    private static final String[] ITEM_SLOT_COUNT_KEYS = new String[AE_TOTAL_TYPES];
    @Unique
    private static final int MAX_ITEM_TYPES = AE_TOTAL_TYPES;
    /**
     * @author xinyihl
     * @reason 修改存储元件存储种类上限
     */
    @ModifyConstant(
            method = {
                    "<init>",
                    "<clinit>"
            },
            constant = @Constant(intValue = 63)
    )
    private static int injected(int original) {
        return AE_TOTAL_TYPES;
    }
}
