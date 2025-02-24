package com.yudream.yudreamaddons.common.registry;

import com.yudream.yudreamaddons.Tags;
import com.yudream.yudreamaddons.common.item.LinkCard;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;

import static com.yudream.yudreamaddons.common.BlocksAndItems.*;

@Mod.EventBusSubscriber(modid = Tags.MOD_ID)
public class ItemRegistry {

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
                linkCard = new LinkCard()
        );
    }
}
