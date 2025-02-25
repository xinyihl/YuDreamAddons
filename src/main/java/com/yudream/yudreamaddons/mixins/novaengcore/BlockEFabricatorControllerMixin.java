package com.yudream.yudreamaddons.mixins.novaengcore;

import github.kasuminova.novaeng.common.block.ecotech.efabricator.BlockEFabricatorController;
import github.kasuminova.novaeng.common.tile.ecotech.efabricator.EFabricatorController;
import hellfirepvp.modularmachinery.common.block.BlockController;
import hellfirepvp.modularmachinery.common.util.IOInventory;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import javax.annotation.Nonnull;

@Mixin(value = BlockEFabricatorController.class, remap = false)
public abstract class BlockEFabricatorControllerMixin extends BlockController {
    /**
     * @author xin_yi_hl
     * @reason 修复掉两个控制器
     */
    @Overwrite(remap = true)
    public void breakBlock(World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof EFabricatorController) {
            EFabricatorController ctrl = (EFabricatorController)te;
            IOInventory inv = ctrl.getInventory();
            for (int i = 0; i < inv.getSlots(); i++) {
                ItemStack stack = inv.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    spawnAsEntity(worldIn, pos, stack);
                    inv.setStackInSlot(i, ItemStack.EMPTY);
                }
            }
        }
        super.breakBlock(worldIn, pos, state);
    }
}
