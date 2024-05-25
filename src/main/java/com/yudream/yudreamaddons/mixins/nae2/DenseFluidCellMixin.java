package com.yudream.yudreamaddons.mixins.nae2;

import appeng.api.storage.data.IAEFluidStack;
import co.neeve.nae2.common.items.cells.DenseCell;
import co.neeve.nae2.common.items.cells.DenseFluidCell;
import co.neeve.nae2.common.registration.definitions.Materials;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = DenseFluidCell.class, remap = false)
public abstract class DenseFluidCellMixin extends DenseCellMixin {
    /**
     * @author xinyihl
     * @reason 修改存储元件存储种类
     */
    @Overwrite(remap = false)
    public int getTotalTypes(ItemStack cellItem) {
        switch (component) {
            case CELL_FLUID_PART_256K:
                return 16;
            case CELL_FLUID_PART_1024K:
                return 18;
            case CELL_FLUID_PART_4096K:
                return 20;
            case CELL_FLUID_PART_16384K:
                return 22;
            default:
                return 0;
        }
    }
}
