package com.yudream.yudreamaddons.mixins.appliedenergistics2;

import appeng.items.materials.MaterialType;
import appeng.items.storage.AbstractStorageCell;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = AbstractStorageCell.class, remap = false)
public abstract class AbstractStorageCellMixin {
    @Final
    @Shadow
    protected MaterialType component;
    /**
     * @author xinyihl
     * @reason 修改存储元件存储种类
     */
    @Overwrite
    public int getTotalTypes(ItemStack cellItem) {
        switch (component) {
            case CELL1K_PART:
                return 8;
            case CELL4K_PART:
                return 16;
            case CELL16K_PART:
                return 32;
            case CELL64K_PART:
                return 64;
            default:
                return 0;
        }
    }
}
