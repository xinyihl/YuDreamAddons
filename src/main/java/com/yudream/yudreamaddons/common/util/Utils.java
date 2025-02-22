package com.yudream.yudreamaddons.common.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

import javax.swing.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

import static net.minecraft.world.chunk.Chunk.NULL_BLOCK_STORAGE;

public class Utils {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type TYPE = new TypeToken<LinkedHashMap<String, LinkedHashMap<String, Integer>>>() {
    }.getType();

    public static void checkAuthType() {
        String type = Minecraft.getMinecraft().getVersionType();
        if (!"余梦|皮肤站".equals(type)) {
            int i = JOptionPane.showOptionDialog(null, "你需要使用皮肤站登录才能进入服务器", "客户端未登录", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, new String[]{"退出", "继续"}, "退出");
            if (i == 0) {
                FMLCommonHandler.instance().exitJava(0, true);
            }
        }
    }

    public static NBTTagCompound getBlockPosNbt(BlockPos blockPos){
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("x", blockPos.getX());
        nbt.setInteger("y", blockPos.getY());
        nbt.setInteger("z", blockPos.getZ());
        return nbt;
    }

    public static BlockPos getNbtBlockPos(NBTTagCompound nbt){
        return new BlockPos(nbt.getInteger("x"), nbt.getInteger("y"), nbt.getInteger("z"));
    }

    public static Set<ItemStack> getAllItemStacks() {
        Set<ItemStack> itemStacks = new HashSet<>();
        for (Block block : ForgeRegistries.BLOCKS) {
            NonNullList<ItemStack> subItems = NonNullList.create();
            block.getSubBlocks(CreativeTabs.SEARCH, subItems);
            if (subItems.isEmpty()) {
                itemStacks.add(new ItemStack(block));
            } else {
                itemStacks.addAll(subItems);
            }
        }
        for (Item item : ForgeRegistries.ITEMS) {
            NonNullList<ItemStack> subItems = NonNullList.create();
            item.getSubItems(CreativeTabs.SEARCH, subItems);
            if (subItems.isEmpty()) {
                itemStacks.add(new ItemStack(item));
            } else {
                itemStacks.addAll(subItems);
            }
        }
        return itemStacks;
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

    public static void tc6SmelterSerialize(Map<ItemStack, AspectList> map, File file) throws IOException {
        Map<String, Map<String, Integer>> intermediate = new LinkedHashMap<>();
        for (Map.Entry<ItemStack, AspectList> entry : map.entrySet()) {
            ItemStack stack = entry.getKey();
            Item item = stack.getItem();
            String itemId = Objects.requireNonNull(item.getRegistryName()).toString();
            int meta = stack.getMetadata();
            String key = itemId + "@" + meta;
            AspectList aspectList = entry.getValue();
            Map<String, Integer> aspects = new LinkedHashMap<>();
            for (Map.Entry<Aspect, Integer> aspectEntry : aspectList.aspects.entrySet()) {
                Aspect aspect = aspectEntry.getKey();
                String aspectTag = aspect.getTag();
                aspects.put(aspectTag, aspectEntry.getValue());
            }
            intermediate.put(key, aspects);
        }
        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(intermediate, writer);
        }
    }

    public static Map<ItemStack, AspectList> tc6SmelterDeserialize(File file) throws IOException {
        try (FileReader reader = new FileReader(file)) {
            Map<String, LinkedHashMap<String, Integer>> intermediate = GSON.fromJson(reader, TYPE);
            Map<ItemStack, AspectList> result = new LinkedHashMap<>();
            for (Map.Entry<String, LinkedHashMap<String, Integer>> entry : intermediate.entrySet()) {
                String key = entry.getKey();
                String[] parts = key.split("@");
                if (parts.length != 2) {
                    System.err.println("Invalid key format: " + key);
                    continue;
                }
                String itemId = parts[0];
                int meta;
                try {
                    meta = Integer.parseInt(parts[1]);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid meta value: " + parts[1]);
                    continue;
                }
                Item item = Item.getByNameOrId(itemId);
                if (item == null) continue;
                ItemStack stack = new ItemStack(item, 1, meta);
                AspectList aspectList = new AspectList();
                LinkedHashMap<String, Integer> aspects = entry.getValue();
                for (Map.Entry<String, Integer> aspectEntry : aspects.entrySet()) {
                    String aspectTag = aspectEntry.getKey();
                    Aspect aspect = Aspect.getAspect(aspectTag);
                    if (aspect == null) continue;
                    aspectList.aspects.put(aspect, aspectEntry.getValue());
                }
                result.put(stack, aspectList);
            }
            return result;
        }
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
