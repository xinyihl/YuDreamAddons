package com.yudream.yudreamaddons.common.container;

import com.yudream.yudreamaddons.YuDreamAddons;
import com.yudream.yudreamaddons.common.api.IContaierTickable;
import com.yudream.yudreamaddons.common.api.IInputHandler;
import com.yudream.yudreamaddons.common.api.NetworkHubDataStorage;
import com.yudream.yudreamaddons.common.api.NetworkStatus;
import com.yudream.yudreamaddons.common.network.PacketServerToClient;
import com.yudream.yudreamaddons.common.title.TileNetworkHub;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Map;
import java.util.UUID;

import static com.yudream.yudreamaddons.common.network.PacketServerToClient.ServerToClient.DELETE_NETWORK;
import static com.yudream.yudreamaddons.common.network.PacketServerToClient.ServerToClient.UPDATE_GUI_SELECTED_NETWORK;

public class NetworkHubContainer extends Container implements IInputHandler, IContaierTickable {

    public EntityPlayer player;
    public TileNetworkHub networkHub;
    public final Map<UUID, NetworkStatus> networks;
    public UUID selectedNetwork;
    public NetworkHubDataStorage storage;

    public NetworkHubContainer(EntityPlayer player, TileNetworkHub networkHub) {
        this.player = player;
        this.networkHub = networkHub;
        this.storage = NetworkHubDataStorage.get(networkHub.getWorld());
        this.networks = storage.getNetworks();
        this.selectedNetwork = networkHub.getNetworkUuid();
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return playerIn.getDistanceSq(this.networkHub.getPos()) <= 64;
    }

    @Override
    public void update() {
        if (player.world.isRemote) return;
        if (!(player.world.getTileEntity(networkHub.getPos()) instanceof TileNetworkHub)) {
            ((EntityPlayerMP) player).closeContainer();
        }
    }

    @Override
    public void onGuiAtion(NBTTagCompound compound) {
        if (this.player.world.isRemote) return;
        int button = compound.getInteger("button");
        switch (button) {
            case 0: { // 切换选择的网络
                UUID uuid = compound.getUniqueId("networkUuid");
                NetworkStatus net = this.storage.getNetwork(uuid);
                if (net != null && net.hasPermission(this.player.getGameProfile().getId())) {
                    this.selectedNetwork = uuid;
                }
                NBTTagCompound tag = new NBTTagCompound();
                tag.setUniqueId("networkUuid", this.selectedNetwork);
                YuDreamAddons.instance.networkWrapper.sendTo(new PacketServerToClient(UPDATE_GUI_SELECTED_NETWORK, tag), (EntityPlayerMP) player);
                break;
            }
            case 1: { // 创建网络
                String name = compound.getString("name");
                NetworkStatus net = this.storage.addNetwork(new NetworkStatus(player.getGameProfile().getId(), name, false, networkHub.getWorld().provider.getDimension(), networkHub.getPos()));
                this.networkHub.setHead(true);
                this.networkHub.setNetworkUuid(net.getUuid());
                this.selectedNetwork = net.getUuid();
                NBTTagCompound tag = new NBTTagCompound();
                tag.setUniqueId("networkUuid", this.selectedNetwork);
                YuDreamAddons.instance.networkWrapper.sendTo(new PacketServerToClient(UPDATE_GUI_SELECTED_NETWORK, tag), (EntityPlayerMP) player);
                this.networkHub.sync();
                break;
            }
            case 996: { // 删除网络
                storage.removeNetwork(this.selectedNetwork);
                NBTTagCompound tag = new NBTTagCompound();
                tag.setUniqueId("networkUuid", this.selectedNetwork);
                YuDreamAddons.instance.networkWrapper.sendToAll(new PacketServerToClient(DELETE_NETWORK, tag));
                this.selectedNetwork = new UUID(0, 0);
                this.networkHub.sync();
                break;
            }
            case 997: { // 连接网络
                if (!this.networkHub.getNetworkUuid().equals(this.selectedNetwork)) {
                    this.networkHub.breakConnection();
                }
                this.networkHub.setNetworkUuid(this.selectedNetwork);
                this.networkHub.sync();
                break;
            }
            case 998: { // 断开连接
                this.networkHub.breakConnection();
                this.selectedNetwork = new UUID(0, 0);
                NBTTagCompound tag = new NBTTagCompound();
                tag.setUniqueId("networkUuid", this.selectedNetwork);
                YuDreamAddons.instance.networkWrapper.sendTo(new PacketServerToClient(UPDATE_GUI_SELECTED_NETWORK, tag), (EntityPlayerMP) player);
                this.networkHub.sync();
                break;
            }
            case 999: { // 切换网络是否公开
                NetworkStatus network = this.storage.getNetwork(selectedNetwork);
                if (network != null) {
                    network.setPublic(!network.isPublic());
                    network.setNeedTellClient(true);
                }
                this.networkHub.sync();
                break;
            }
        }
    }
}
