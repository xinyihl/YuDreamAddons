package com.yudream.yudreamaddons.mixins.mekeng;

import com.mekeng.github.common.item.ItemGasCell;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import static com.mekeng.github.common.ItemAndBlocks.*;

@Mixin(value = ItemGasCell.class, remap = false)
public abstract class ItemGasCellMixin {
    @Final
    @Shadow(remap = false)
    private ItemStack core;
    /**
     * @author xinyihl
     * @reason 修改存储元件存储种类
     */
    @Overwrite(remap = false)
    public int getTotalTypes(ItemStack cellItem) {
        Item item = core.getItem();
        if (GAS_CELL_CORE_1k.equals(item)) {
            return 4;
        } else if (GAS_CELL_CORE_4k.equals(item)) {
            return 8;
        } else if (GAS_CELL_CORE_16k.equals(item)) {
            return 16;
        } else if (GAS_CELL_CORE_64k.equals(item)) {
            return 32;
        }
        return 0;
    }
}
