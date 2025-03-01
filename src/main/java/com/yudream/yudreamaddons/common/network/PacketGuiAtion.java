package com.yudream.yudreamaddons.common.network;

import com.yudream.yudreamaddons.common.api.GuiAtion;
import com.yudream.yudreamaddons.common.api.NetworkHubDataStorage;
import com.yudream.yudreamaddons.common.api.NetworkStatus;
import com.yudream.yudreamaddons.common.container.NetworkHubContainer;
import com.yudream.yudreamaddons.common.title.TileNetworkHub;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

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
        EntityPlayerMP player = context.getServerHandler().player;
        Container container = player.openContainer;
        if (container instanceof NetworkHubContainer) {
            TileNetworkHub networkHub = ((NetworkHubContainer) container).networkHub;
            World world = player.world;
            NetworkHubDataStorage storage = NetworkHubDataStorage.get(world);
            player.getServerWorld().addScheduledTask(() -> {
                switch (GuiAtion.valueOf(message.type)) {
                    case CREATE_NETWORK: {
                        storage.addNetwork(new NetworkStatus(player.getGameProfile().getId(), message.tag.getString("name"), false, world.provider.getDimension(), networkHub.getPos()));
                        networkHub.sync();
                        return;
                    }
                    case DELETE_NETWORK: {
                        storage.removeNetwork(player, message.tag.getUniqueId("networkUuid"));
                        networkHub.sync();
                        return;
                    }
                    case SET_NETWORK_UUID: {
                        networkHub.setNetworkUuid(message.tag.getUniqueId("networkUuid"));
                        networkHub.sync();
                        return;
                    }
                    case DISCONNECT_NETWORK: {
                        networkHub.breakConnection();
                        networkHub.sync();
                        return;
                    }
                    case SET_NETWORK_PUBLIC: {
                        NetworkStatus network = storage.getNetwork(player, message.tag.getUniqueId("networkUuid"));
                        if (network != null) {
                            network.setPublic(message.tag.getBoolean("public"));
                        }
                        networkHub.sync();
                    }
                }
            });
        }
        return null;
    }
}
