package com.yudream.yudreamaddons.common.item;

import com.yudream.yudreamaddons.Tags;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

import java.util.Objects;

import static com.yudream.yudreamaddons.common.ModBlocksAndItem.*;

@Mod.EventBusSubscriber(modid = Tags.MOD_ID)
public class ItemRegistry {

    public static final CreativeTabs EXAMPLE_CREATIVE_TAB = new CreativeTabs("yudream_tab") {
        @Override
        @Nonnull
        public ItemStack createIcon() {
            return new ItemStack(linkCard);
        }
    };

    @SubscribeEvent
    @SideOnly(value = Side.CLIENT)
    public static void onModelReg(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(linkCard, 0, new ModelResourceLocation(Objects.requireNonNull(linkCard.getRegistryName()), "inventory"));
    }

    @SubscribeEvent
    public static void registerItem(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                linkCard = new LinkCard()
        );
    }
}
