package com.yudream.yudreamaddons.mixins.botania;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import vazkii.botania.common.block.tile.TileRuneAltar;
import vazkii.botania.common.item.ModItems;

import static com.yudream.yudreamaddons.Configuration.duRuneConsume;

@Mixin(value = TileRuneAltar.class, remap = false)
public abstract class TileRuneAltarMixin {
    @Redirect(
            method = "onWanded",
            at = @At(
                    value = "FIELD",
                    target = "Lvazkii/botania/common/item/ModItems;rune:Lnet/minecraft/item/Item;"
            )
    )
    public Item injected() {
        return duRuneConsume ? Items.AIR : ModItems.rune;
    }
}
