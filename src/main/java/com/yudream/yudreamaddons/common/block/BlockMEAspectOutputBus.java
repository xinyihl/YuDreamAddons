package com.yudream.yudreamaddons.common.block;

import com.yudream.yudreamaddons.common.title.MEAspectOutputBus;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockMEAspectOutputBus extends BlockMEAspectBus {
    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState iBlockState) {
        return new MEAspectOutputBus();
    }
}
