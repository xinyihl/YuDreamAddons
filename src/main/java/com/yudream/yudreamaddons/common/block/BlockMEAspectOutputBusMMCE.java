package com.yudream.yudreamaddons.common.block;

import com.yudream.yudreamaddons.common.title.TitleMEAspectOutputBusMMCE;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockMEAspectOutputBusMMCE extends BlockMEAspectBus {
    public BlockMEAspectOutputBusMMCE() {
        super("blockmeaspectoutputbusmmce");
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState iBlockState) {
        return new TitleMEAspectOutputBusMMCE();
    }
}
