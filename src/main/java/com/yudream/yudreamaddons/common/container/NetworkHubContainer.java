package com.yudream.yudreamaddons.common.container;

import com.yudream.yudreamaddons.YuDreamAddons;
import com.yudream.yudreamaddons.common.api.IInputHandler;
import com.yudream.yudreamaddons.common.api.NetworkHubDataStorage;
import com.yudream.yudreamaddons.common.api.NetworkStatus;
import com.yudream.yudreamaddons.common.network.PacketServerToClient;
import com.yudream.yudreamaddons.common.title.TileNetworkHub;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.LinkedHashMap;
import java.util.UUID;

import static com.yudream.yudreamaddons.common.network.PacketServerToClient.ServerToClient.DELETE_NETWORK;
import static com.yudream.yudreamaddons.common.network.PacketServerToClient.ServerToClient.UPDATE_GUI_SELECTED_NETWORK;

public class NetworkHubContainer extends Container implements IInputHandler {

    public EntityPlayer player;
    public TileNetworkHub networkHub;
    public final LinkedHashMap<UUID, NetworkStatus> networks;
    public UUID selectedNetwork;
    public NetworkHubDataStorage storage;

    public NetworkHubContainer(EntityPlayer player, TileNetworkHub networkHub) {
        this.player = player;
        this.networkHub = networkHub;
        this.storage = NetworkHubDataStorage.get(networkHub.getWorld());
        this.networks = storage.getAllNetworks();
        this.selectedNetwork = networkHub.getNetworkUuid();
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return playerIn.getDistanceSq(this.networkHub.getPos()) <= 64;
    }

    @Override
    @SideOnly(Side.SERVER)
    public void onGuiAtion(NBTTagCompound compound) {
        if (player.world.isRemote) return;
        int button = compound.getInteger("button");
        switch (button) {
            case 0: { // 切换选择的网络
                UUID uuid = compound.getUniqueId("networkUuid");
                NetworkStatus net = storage.getNetwork(uuid);
                if (net != null && net.hasPermission(player.getGameProfile().getId())) {
                    selectedNetwork = uuid;
                }
                NBTTagCompound tag = new NBTTagCompound();
                tag.setUniqueId("networkUuid", selectedNetwork);
                YuDreamAddons.instance.networkWrapper.sendTo(new PacketServerToClient(UPDATE_GUI_SELECTED_NETWORK, tag), (EntityPlayerMP) player);
                break;
            }
            case 1: { // 创建网络
                String name = compound.getString("name");
                NetworkStatus net = storage.addNetwork(new NetworkStatus(player.getGameProfile().getId(), name, false, networkHub.getWorld().provider.getDimension(), networkHub.getPos()));
                networkHub.setHead(true);
                networkHub.setNetworkUuid(net.getUuid());
                networkHub.sync();
                break;
            }
            case 996: { // 删除网络
                networkHub.breakConnection();
                storage.removeNetwork(selectedNetwork);
                NBTTagCompound tag = new NBTTagCompound();
                tag.setUniqueId("networkUuid", selectedNetwork);
                YuDreamAddons.instance.networkWrapper.sendTo(new PacketServerToClient(DELETE_NETWORK, tag), (EntityPlayerMP) player);
                networkHub.sync();
                break;
            }
            case 997: { // 连接网络
                if (!networkHub.getNetworkUuid().equals(selectedNetwork)) {
                    networkHub.breakConnection();
                }
                networkHub.setNetworkUuid(selectedNetwork);
                networkHub.sync();
                break;
            }
            case 998: { // 断开连接
                networkHub.breakConnection();
                selectedNetwork = new UUID(0, 0);
                NBTTagCompound tag = new NBTTagCompound();
                tag.setUniqueId("networkUuid", selectedNetwork);
                YuDreamAddons.instance.networkWrapper.sendTo(new PacketServerToClient(UPDATE_GUI_SELECTED_NETWORK, tag), (EntityPlayerMP) player);
                networkHub.sync();
                break;
            }
            case 999: { // 切换网络是否公开
                NetworkStatus network = storage.getNetwork(selectedNetwork);
                if (network != null) {
                    network.setPublic(!network.isPublic());
                    network.setNeedTellClient(true);
                }
                networkHub.sync();
                break;
            }
        }
    }
}
