package com.yudream.yudreamaddons.common.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.fml.common.FMLCommonHandler;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

import javax.swing.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class Utils {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type TYPE = new TypeToken<LinkedHashMap<String, LinkedHashMap<String, Integer>>>(){}.getType();

    public static void checkAuthType() {
        String type = Minecraft.getMinecraft().getVersionType();
        if (!"余梦|皮肤站".equals(type)) {
            JFrame frame = new JFrame();
            JOptionPane.showMessageDialog(frame, "你需要使用皮肤站登录才能进入服务器！", "错误", JOptionPane.ERROR_MESSAGE);
            FMLCommonHandler.instance().exitJava(0, true);
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

    public static void tc6SmelterSerialize(Map<Item, AspectList> map, File file) throws IOException {
        Map<String, Map<String, Integer>> intermediate = new LinkedHashMap<>();
        for (Map.Entry<Item, AspectList> entry : map.entrySet()) {
            Item item = entry.getKey();
            String itemId = Objects.requireNonNull(item.getRegistryName()).toString();
            AspectList aspectList = entry.getValue();
            Map<String, Integer> aspects = new LinkedHashMap<>();
            for (Map.Entry<Aspect, Integer> aspectEntry : aspectList.aspects.entrySet()) {
                Aspect aspect = aspectEntry.getKey();
                String aspectTag = aspect.getTag();
                aspects.put(aspectTag, aspectEntry.getValue());
            }
            intermediate.put(itemId, aspects);
        }
        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(intermediate, writer);
        }
    }

    public static Map<Item, AspectList> tc6SmelterDeserialize(File file) throws IOException {
        try (FileReader reader = new FileReader(file)) {
            Map<String, LinkedHashMap<String, Integer>> intermediate = GSON.fromJson(reader, TYPE);
            Map<Item, AspectList> result = new LinkedHashMap<>();
            for (Map.Entry<String, LinkedHashMap<String, Integer>> entry : intermediate.entrySet()) {
                String itemId = entry.getKey();
                Item item = Item.getByNameOrId(itemId);
                if (item == null) continue;
                AspectList aspectList = new AspectList();
                LinkedHashMap<String, Integer> aspects = entry.getValue();
                for (Map.Entry<String, Integer> aspectEntry : aspects.entrySet()) {
                    String aspectTag = aspectEntry.getKey();
                    Aspect aspect = Aspect.getAspect(aspectTag);
                    if (aspect == null) continue;
                    aspectList.aspects.put(aspect, aspectEntry.getValue());
                }
                result.put(item, aspectList);
            }
            return result;
        }
    }

    /**
     * Sets the block at the given position without triggering lighting updates.
     * WARNING: This bypasses many of the normal safety checks and may result in
     * world inconsistencies. Use at your own risk.
     */
    public static boolean setBlockWithoutLighting(World world, BlockPos pos, IBlockState newState) {
        Chunk chunk = world.getChunk(pos);
        int sectionIndex = pos.getY() >> 4;
        ExtendedBlockStorage[] storageArray = chunk.getBlockStorageArray();

        if (sectionIndex < 0 || sectionIndex >= storageArray.length) {
            return false;
        }

        ExtendedBlockStorage storage = storageArray[sectionIndex];
        if (storage == null) {
            return false;
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
