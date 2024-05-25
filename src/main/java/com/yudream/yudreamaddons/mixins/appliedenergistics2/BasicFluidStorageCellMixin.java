package com.yudream.yudreamaddons.mixins.appliedenergistics2;

import appeng.api.storage.data.IAEFluidStack;
import appeng.fluids.items.BasicFluidStorageCell;
import appeng.items.materials.MaterialType;
import appeng.items.storage.AbstractStorageCell;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = BasicFluidStorageCell.class, remap = false)
public abstract class BasicFluidStorageCellMixin extends AbstractStorageCellMixin {
    /**
     * @author xinyihl
     * @reason 修改存储元件存储种类
     */
    @Overwrite(remap = false)
    public int getTotalTypes(ItemStack cellItem) {
        switch (component) {
            case FLUID_CELL1K_PART:
                return 8;
            case FLUID_CELL4K_PART:
                return 10;
            case FLUID_CELL16K_PART:
                return 12;
            case FLUID_CELL64K_PART:
                return 14;
            default:
                return 0;
        }
    }
}
