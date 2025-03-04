package com.yudream.yudreamaddons.common.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.swing.*;
import java.util.Objects;

import static net.minecraft.world.chunk.Chunk.NULL_BLOCK_STORAGE;

public class Utils {

    public static void checkAuthType() {
        String type = Minecraft.getMinecraft().getVersionType();
        if (!"余梦|皮肤站".equals(type)) {
            int i = JOptionPane.showOptionDialog(null, "你需要使用皮肤站登录才能进入服务器", "客户端未登录", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, new String[]{"退出", "继续"}, "退出");
            if (i == 0) {
                FMLCommonHandler.instance().exitJava(0, true);
            }
        }
    }

    public static String getItemId(ItemStack itemStack) {
        int meta = itemStack.getMetadata();
        String text = "<" + Objects.requireNonNull(itemStack.getItem().getRegistryName()) + (meta == 0 ? "" : ":" + meta) + ">";
        if (itemStack.serializeNBT().hasKey("tag")) {
            String nbt = itemStack.serializeNBT().getTag("tag").toString();
            if (!nbt.isEmpty())
                text += ".withTag(" + nbt + ")";
        }
        return text;
    }

    public static boolean isCleanroomLoader() {
        try {
            Class.forName("com.cleanroommc.boot.Main");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean isPlayerOp(EntityPlayer player) {
        MinecraftServer server = player.getServer();
        boolean isOp = false;
        if (server != null) {
            isOp = server.getPlayerList().canSendCommands(player.getGameProfile());
        }
        return isOp;
    }

    /**
     * Sets the block at the given position without triggering lighting updates.
     * WARNING: This bypasses many of the normal safety checks and may result in
     * world inconsistencies. Use at your own risk.
     */
    public static boolean setBlockStateFast(World world, BlockPos pos, IBlockState newState) {
        Chunk chunk = world.getChunk(pos);
        int sectionIndex = pos.getY() >> 4;
        ExtendedBlockStorage[] storageArray = chunk.getBlockStorageArray();
        ExtendedBlockStorage storage = storageArray[sectionIndex];

        if (storage == NULL_BLOCK_STORAGE) {
            storage = new ExtendedBlockStorage(pos.getY() >> 4 << 4, chunk.getWorld().provider.hasSkyLight());
            chunk.getBlockStorageArray()[sectionIndex] = storage;
        }

        int x = pos.getX() & 15;
        int y = pos.getY() & 15;
        int z = pos.getZ() & 15;

        IBlockState oldState = storage.get(x, y, z);
        storage.set(x, y, z, newState);
        chunk.markDirty();
        world.notifyBlockUpdate(pos, oldState, newState, 2);
        return true;
    }
}
