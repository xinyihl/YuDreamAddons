package com.yudream.yudreamaddons.common.registry;

import com.yudream.yudreamaddons.Tags;
import com.yudream.yudreamaddons.common.block.BlockMEAspectInputBus;
import com.yudream.yudreamaddons.common.block.BlockMEAspectOutputBus;
import com.yudream.yudreamaddons.common.block.BlockShareInfHandler;
import com.yudream.yudreamaddons.common.item.LinkCard;
import com.yudream.yudreamaddons.common.item.MyItemBlock;
import com.yudream.yudreamaddons.common.title.TitleMEAspectInputBus;
import com.yudream.yudreamaddons.common.title.TitleMEAspectOutputBus;
import com.yudream.yudreamaddons.common.title.TitleShareInfHandler;
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
                itemMEAspectInputBus = new MyItemBlock(blockMEAspectInputBus),
                itemMEAspectOutputBus = new MyItemBlock(blockMEAspectOutputBus),
                itemShareInfHandler = new MyItemBlock(blockShareInfHandler)
        );
    }

    @SubscribeEvent
    public static void registerBlock(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(
                blockMEAspectInputBus = new BlockMEAspectInputBus(),
                blockMEAspectOutputBus = new BlockMEAspectOutputBus(),
                blockShareInfHandler = new BlockShareInfHandler()
        );
        GameRegistry.registerTileEntity(TitleMEAspectInputBus.class, new ResourceLocation(Tags.MOD_ID, "tile_measpectinputbus"));
        GameRegistry.registerTileEntity(TitleMEAspectOutputBus.class, new ResourceLocation(Tags.MOD_ID, "tile_measpectoutputbus"));
        GameRegistry.registerTileEntity(TitleShareInfHandler.class, new ResourceLocation(Tags.MOD_ID, "tile_shareinfhandler"));
    }
}
