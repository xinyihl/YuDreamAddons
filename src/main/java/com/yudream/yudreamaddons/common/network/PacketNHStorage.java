package com.yudream.yudreamaddons.common.network;

import com.yudream.yudreamaddons.common.api.NetworkHubDataStorage;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketNHStorage implements IMessage, IMessageHandler<PacketNHStorage, IMessage> {

    private NBTTagCompound tag;

    public PacketNHStorage() {

    }

    public PacketNHStorage(NBTTagCompound tag) {
        this.tag = tag;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        tag = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeTag(buf, tag);
    }

    @Override
    public IMessage onMessage(PacketNHStorage message, MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            WorldClient world = Minecraft.getMinecraft().world;
            NetworkHubDataStorage storage = NetworkHubDataStorage.get(world);
            storage.readFromNBT(message.tag);
        });
        return null;
    }
}
