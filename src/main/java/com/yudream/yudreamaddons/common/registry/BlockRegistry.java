package com.yudream.yudreamaddons.common.registry;

import com.yudream.yudreamaddons.Tags;
import net.minecraft.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = Tags.MOD_ID)
public class BlockRegistry {

    @SubscribeEvent
    public static void registerBlock(RegistryEvent.Register<Block> event) {
        //event.getRegistry().registerAll();
    }

}
