package com.yudream.yudreamaddons.common.registry;

import com.yudream.yudreamaddons.Tags;
import com.yudream.yudreamaddons.common.block.BlockNetworkHub;
import com.yudream.yudreamaddons.common.item.LinkCard;
import com.yudream.yudreamaddons.common.item.MyItemBlock;
import com.yudream.yudreamaddons.common.title.TileNetworkHub;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nonnull;

import static com.yudream.yudreamaddons.common.BlocksAndItems.*;

@Mod.EventBusSubscriber(modid = Tags.MOD_ID)
public class Registry {

    public static final CreativeTabs CREATIVE_TAB = new CreativeTabs("yudream_tab") {
        @Override
        @Nonnull
        public ItemStack createIcon() {
            return new ItemStack(linkCard);
        }
    };

    @SubscribeEvent
    public static void registerItem(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                linkCard = new LinkCard(),
                itemNetworkHub = new MyItemBlock(blockNetworkHub)
        );
    }

    @SubscribeEvent
    public static void registerBlock(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(
                blockNetworkHub = new BlockNetworkHub()
        );
        GameRegistry.registerTileEntity(TileNetworkHub.class, new ResourceLocation(Tags.MOD_ID, "tile_network_hub"));
    }
}
