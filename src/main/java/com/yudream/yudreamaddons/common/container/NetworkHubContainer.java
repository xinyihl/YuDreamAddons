package com.yudream.yudreamaddons.common.container;

import com.yudream.yudreamaddons.common.api.NetworkHubDataStorage;
import com.yudream.yudreamaddons.common.api.NetworkStatus;
import com.yudream.yudreamaddons.common.title.TileNetworkHub;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

import java.util.List;

public class NetworkHubContainer extends Container {

    public EntityPlayer player;
    public TileNetworkHub networkHub;
    public final List<NetworkStatus> networks;

    public NetworkHubContainer(EntityPlayer player, TileNetworkHub networkHub) {
        this.player = player;
        this.networkHub = networkHub;
        NetworkHubDataStorage storage = NetworkHubDataStorage.get(networkHub.getWorld());
        this.networks = storage.getAllNetworks();
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return playerIn.getDistanceSq(this.networkHub.getPos()) <= 64;
    }
}
