package com.yudream.yudreamaddons.mixins.appliedenergistics2;

import appeng.me.storage.AbstractCellInventory;
import com.yudream.yudreamaddons.Configuration;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = AbstractCellInventory.class, remap = false)
public abstract class AbstractCellInventoryMixin {
    @Unique
    private static final String[] ITEM_SLOT_KEYS = new String[Configuration.aeTotalTypes];
    @Unique
    private static final String[] ITEM_SLOT_COUNT_KEYS = new String[Configuration.aeTotalTypes];
    @Unique
    private static final int MAX_ITEM_TYPES = Configuration.aeTotalTypes;
    /**
     * @author xinyihl
     * @reason 修改存储元件存储种类上限
     */
    @ModifyConstant(method = {"<init>","<clinit>"} ,constant = @Constant(intValue = 63))
    private static int injected(int original) {
        return Configuration.aeTotalTypes;
    }
}
