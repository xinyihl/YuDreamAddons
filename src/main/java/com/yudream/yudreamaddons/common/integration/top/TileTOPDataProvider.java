package com.yudream.yudreamaddons.common.integration.top;

import com.yudream.yudreamaddons.Tags;
import com.yudream.yudreamaddons.common.title.TileNetworkHub;
import com.yudream.yudreamaddons.common.title.TitleShareInfHandler;
import com.yudream.yudreamaddons.common.title.base.TitleMEAspectBus;
import com.yudream.yudreamaddons.common.title.base.TitleMeBase;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import thaumicenergistics.api.IThELangKey;

public class TileTOPDataProvider implements IProbeInfoProvider {
    public TileTOPDataProvider() {
    }

    @Override
    public String getID() {
        return Tags.MOD_ID + ":" + this.getClass().getSimpleName();
    }

    protected String getLocalizedKey(IThELangKey key) {
        return "{*" + key.getUnlocalizedKey() + "*}";
    }

    protected String getYudreamKey(String key) {
        return "{*tooltip.yudreamaddons." + key + "*}";
    }

    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        TileEntity te = world.getTileEntity(data.getPos());
        if (te instanceof TitleMeBase) {
            ((TitleMeBase) te).withPowerStateText(probeInfo::text, this::getLocalizedKey);
        }
        if (te instanceof TitleMEAspectBus) {
            ((TitleMEAspectBus) te).withPowerStateText(probeInfo::text, this::getLocalizedKey);
        }
        if (te instanceof TitleShareInfHandler){
            ((TitleShareInfHandler) te).withLinkStateText(probeInfo::text, this::getYudreamKey);
        }
        if (te instanceof TileNetworkHub) {
            probeInfo.text("Net: " + ((TileNetworkHub) te).getNetworkUuid().toString());
        }
    }
}

