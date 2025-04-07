package com.yudream.yudreamaddons.common.block;

import com.yudream.yudreamaddons.common.title.TitleMEAspectInputBusMMCE;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockMEAspectInputBusMMCE extends BlockMEAspectBus {
    public BlockMEAspectInputBusMMCE() {
        super("blockmeaspectinputbusmmce");
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState iBlockState) {
        return new TitleMEAspectInputBusMMCE();
    }
}
