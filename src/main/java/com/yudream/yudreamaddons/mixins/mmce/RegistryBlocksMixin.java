package com.yudream.yudreamaddons.mixins.mmce;

import com.yudream.yudreamaddons.common.ModBlocksAndItem;
import com.yudream.yudreamaddons.common.block.BlockMEAspectInputBus;
import com.yudream.yudreamaddons.common.block.BlockMEAspectOutputBus;
import com.yudream.yudreamaddons.common.block.BlockShareInfHandler;
import com.yudream.yudreamaddons.common.title.TitleMEAspectInputBus;
import com.yudream.yudreamaddons.common.title.TitleMEAspectOutputBus;
import com.yudream.yudreamaddons.common.title.TitleShareInfHandler;
import hellfirepvp.modularmachinery.common.registry.RegistryBlocks;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.yudream.yudreamaddons.common.ModBlocksAndItem.*;

@Mixin(value = RegistryBlocks.class, remap = false)
public abstract class RegistryBlocksMixin {

    @Shadow
    private static <T extends Block> T prepareRegister(T block) {
        return null;
    }

    @Shadow
    private static ItemBlock prepareItemBlockRegister(Block block) {
        return null;
    }

    @Shadow
    private static void registerTileWithModID(final Class<? extends TileEntity> aClass) {
    }

    @Inject(
            method = "initialize",
            at = @At(
                    value = "HEAD"
            )
    )
    private static void injected(CallbackInfo ci) {
        blockMEAspectInputBus = prepareRegister(new BlockMEAspectInputBus());
        ModBlocksAndItem.itemMEAspectInputBus = prepareItemBlockRegister(blockMEAspectInputBus);
        blockMEAspectOutputBus = prepareRegister(new BlockMEAspectOutputBus());
        ModBlocksAndItem.itemMEAspectOutputBus = prepareItemBlockRegister(blockMEAspectOutputBus);
        blockShareInfHandler = prepareRegister(new BlockShareInfHandler());
        ModBlocksAndItem.itemShareInfHandler = prepareItemBlockRegister(blockShareInfHandler);
        registerTileWithModID(TitleMEAspectInputBus.class);
        registerTileWithModID(TitleMEAspectOutputBus.class);
        registerTileWithModID(TitleShareInfHandler.class);
    }
}
