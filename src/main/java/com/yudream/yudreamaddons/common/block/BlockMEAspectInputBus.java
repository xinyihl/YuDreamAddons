package com.yudream.yudreamaddons.common.block;

import com.yudream.yudreamaddons.common.title.MEAspectInputBus;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockMEAspectInputBus extends BlockMEAspectBus {
    @Override
    public boolean onBlockActivated(@Nonnull World world, @Nonnull BlockPos blockPos, @Nonnull IBlockState iBlockState, @Nonnull EntityPlayer entityPlayer, @Nonnull EnumHand enumHand, @Nonnull EnumFacing enumFacing, float v, float v1, float v2) {
        return false;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState iBlockState) {
        return new MEAspectInputBus();
    }
}
