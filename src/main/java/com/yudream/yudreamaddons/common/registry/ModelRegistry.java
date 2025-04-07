package com.yudream.yudreamaddons.common.registry;

import com.yudream.yudreamaddons.Tags;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Objects;

import static com.yudream.yudreamaddons.common.BlocksAndItems.*;

@Mod.EventBusSubscriber(modid = Tags.MOD_ID, value = Side.CLIENT)
public class ModelRegistry {

    @SubscribeEvent
    public static void registerModel(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(linkCard, 0, new ModelResourceLocation(Objects.requireNonNull(linkCard.getRegistryName()), "inventory"));
        ModelLoader.setCustomModelResourceLocation(itemMEAspectInputBus, 0, new ModelResourceLocation(Objects.requireNonNull(itemMEAspectInputBus.getRegistryName()), "inventory"));
        ModelLoader.setCustomModelResourceLocation(itemMEAspectOutputBus, 0, new ModelResourceLocation(Objects.requireNonNull(itemMEAspectOutputBus.getRegistryName()), "inventory"));
        ModelLoader.setCustomModelResourceLocation(itemMEAspectInputBusMMCE, 0, new ModelResourceLocation(Objects.requireNonNull(itemMEAspectInputBusMMCE.getRegistryName()), "inventory"));
        ModelLoader.setCustomModelResourceLocation(itemMEAspectOutputBusMMCE, 0, new ModelResourceLocation(Objects.requireNonNull(itemMEAspectOutputBusMMCE.getRegistryName()), "inventory"));
        ModelLoader.setCustomModelResourceLocation(itemShareInfHandler, 0, new ModelResourceLocation(Objects.requireNonNull(itemShareInfHandler.getRegistryName()), "inventory"));

    }
}
