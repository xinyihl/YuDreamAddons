package com.yudream.yudreamaddons.common.integration.top;

import com.yudream.yudreamaddons.Tags;
import com.yudream.yudreamaddons.common.api.IHasProbeInfo;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class TileTOPDataProvider implements IProbeInfoProvider {
    public TileTOPDataProvider() {
    }

    @Override
    public String getID() {
        return Tags.MOD_ID + ":" + this.getClass().getSimpleName();
    }

    protected String getYudreamKey(String key) {
        return "{*tooltip.yudreamaddons." + key + "*}";
    }

    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        TileEntity te = world.getTileEntity(data.getPos());
        if (te instanceof IHasProbeInfo) {
            ((IHasProbeInfo) te).addProbeInfo(probeInfo::text, this::getYudreamKey);
        }
    }
}

