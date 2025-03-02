package com.yudream.yudreamaddons.common.network;

import com.yudream.yudreamaddons.YuDreamAddons;
import com.yudream.yudreamaddons.common.api.GuiAtion;
import com.yudream.yudreamaddons.common.api.NetworkHubDataStorage;
import com.yudream.yudreamaddons.common.api.NetworkStatus;
import com.yudream.yudreamaddons.common.container.NetworkHubContainer;
import com.yudream.yudreamaddons.common.title.TileNetworkHub;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.List;

public class PacketGuiAtion implements IMessage, IMessageHandler<PacketGuiAtion, IMessage> {
    private String type;
    private NBTTagCompound tag;

    public PacketGuiAtion() {
    }

    public PacketGuiAtion(GuiAtion type, NBTTagCompound tag) {
        this.type = type.name();
        this.tag = tag;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        type = ByteBufUtils.readUTF8String(buf);
        tag = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, type);
        ByteBufUtils.writeTag(buf, tag);
    }

    @Override
    public IMessage onMessage(PacketGuiAtion message, MessageContext context) {
        context.getServerHandler().server.addScheduledTask(() -> {
            EntityPlayerMP player = context.getServerHandler().player;
            Container container = player.openContainer;
            if (container instanceof NetworkHubContainer) {
                TileNetworkHub networkHub = ((NetworkHubContainer) container).networkHub;
                World world = player.world;
                NetworkHubDataStorage storage = NetworkHubDataStorage.get(world);
                switch (GuiAtion.valueOf(message.type)) {
                    case CREATE_NETWORK: {
                        NetworkStatus net = storage.addNetwork(new NetworkStatus(player.getGameProfile().getId(), message.tag.getString("name"), false, world.provider.getDimension(), networkHub.getPos()));
                        networkHub.setHead(true);
                        networkHub.setNetworkUuid(net.getUuid());
                        networkHub.sync();
                        syncNetworks(world, player);
                        break;
                    }
                    case DELETE_NETWORK: {
                        networkHub.breakConnection();
                        storage.removeNetwork(player, message.tag.getUniqueId("networkUuid"));
                        networkHub.sync();
                        syncNetworks(world, player);
                        break;
                    }
                    case SET_NETWORK_UUID: {
                        networkHub.setNetworkUuid(message.tag.getUniqueId("networkUuid"));
                        networkHub.sync();
                        syncNetworks(world, player);
                        break;
                    }
                    case DISCONNECT_NETWORK: {
                        networkHub.breakConnection();
                        networkHub.sync();
                        syncNetworks(world, player);
                        break;
                    }
                    case SET_NETWORK_PUBLIC: {
                        NetworkStatus network = storage.getNetwork(player, message.tag.getUniqueId("networkUuid"));
                        if (network != null) {
                            network.setPublic(message.tag.getBoolean("public"));
                        }
                        storage.markDirty();
                        networkHub.sync();
                        syncNetworks(world, player);
                        break;
                    }
                }
            }
        });
        return null;
    }

    private void syncNetworks(World world, EntityPlayerMP player) {
        NetworkHubDataStorage storage = NetworkHubDataStorage.get(world);
        List<NetworkStatus> networks = storage.getAllNetworks(player);
        NBTTagCompound nbt = new NBTTagCompound();
        NBTTagList list = new NBTTagList();
        for (NetworkStatus network : networks) {
            NBTTagCompound tag = new NBTTagCompound();
            list.appendTag(network.writeToNBT(tag));
        }
        nbt.setTag("networks", list);
        YuDreamAddons.instance.networkWrapper.sendTo(new PacketNHStorage(nbt), player);
    }
}
