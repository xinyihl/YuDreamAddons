package com.yudream.yudreamaddons.common.event;

import com.yudream.yudreamaddons.client.gui.NetworkHubGuiContainer;
import com.yudream.yudreamaddons.common.container.NetworkHubContainer;
import com.yudream.yudreamaddons.common.title.TileNetworkHub;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nullable;

public class GUIHandler implements IGuiHandler {

    public static final int GUI_NETWORK_HUB = 1;

    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == GUI_NETWORK_HUB) {
            return new NetworkHubContainer(player, (TileNetworkHub) player.world.getTileEntity(new BlockPos(x, y, z)));
        }
        return null;
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == GUI_NETWORK_HUB) {
            return new NetworkHubGuiContainer(new NetworkHubContainer(player, (TileNetworkHub) player.world.getTileEntity(new BlockPos(x, y, z))));
        }
        return null;
    }
}
