package com.yudream.yudreamaddons.common.container;

import com.yudream.yudreamaddons.common.title.TileNetworkHub;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class NetworkHubContainer extends Container {

    public EntityPlayer player;
    public TileNetworkHub networkHub;

    public NetworkHubContainer(EntityPlayer player, TileNetworkHub networkHub) {
        this.player = player;
        this.networkHub = networkHub;
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return playerIn.getDistanceSq(this.networkHub.getPos()) <= 64;
    }
}
