package com.yudream.yudreamaddons.mixins.botania;

import com.yudream.yudreamaddons.Configuration;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.mana.IManaReceiver;
import vazkii.botania.api.recipe.RecipeRuneAltar;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.block.tile.TileRuneAltar;
import vazkii.botania.common.block.tile.TileSimpleInventory;
import vazkii.botania.common.item.ModItems;

import java.util.List;

@Mixin(value = TileRuneAltar.class, remap = false)
public abstract class TileRuneAltarMixin extends TileSimpleInventory implements IManaReceiver, ITickable {
    @Final
    @Shadow(remap = false)
    private static int SET_COOLDOWN_EVENT;
    @Final
    @Shadow(remap = false)
    private static int CRAFT_EFFECT_EVENT;
    @Shadow(remap = false)
    RecipeRuneAltar currentRecipe;
    @Shadow(remap = false)
    public int manaToGet;
    @Shadow(remap = false)
    int mana;
    /**
     * @author xinyihl
     * @reason 控制符文祭坛是否消耗符文
     */
    @Overwrite(remap = false)
    public void onWanded(EntityPlayer player, ItemStack wand) {
        if (world.isRemote) return;

        RecipeRuneAltar recipe = null;

        if(currentRecipe != null)
            recipe = currentRecipe;

        else for(RecipeRuneAltar recipe_ : BotaniaAPI.runeAltarRecipes) {
            if(recipe_.matches(itemHandler)) {
                recipe = recipe_;
                break;
            }
        }

        if(manaToGet > 0 && mana >= manaToGet) {
            List<EntityItem> items = world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos, pos.add(1, 1, 1)));
            EntityItem livingrock = null;

            for(EntityItem item : items)
                if(!item.isDead && !item.getItem().isEmpty() && item.getItem().getItem() == Item.getItemFromBlock(ModBlocks.livingrock)) {
                    livingrock = item;
                    break;
                }

            if(livingrock != null) {
                assert recipe != null;
                int mana = recipe.getManaUsage();
                recieveMana(-mana);
                ItemStack output = recipe.getOutput().copy();
                EntityItem outputItem = new EntityItem(world, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, output);
                world.spawnEntity(outputItem);
                currentRecipe = null;
                world.addBlockEvent(getPos(), ModBlocks.runeAltar, SET_COOLDOWN_EVENT, 60);
                world.addBlockEvent(getPos(), ModBlocks.runeAltar, CRAFT_EFFECT_EVENT, 0);
                saveLastRecipe();

                for(int i = 0; i < getSizeInventory(); i++) {
                    ItemStack stack = itemHandler.getStackInSlot(i);
                    if(!stack.isEmpty()) {
                        if(stack.getItem() == ModItems.rune && !Configuration.duRuneConsume && (player == null || !player.capabilities.isCreativeMode)) {
                            EntityItem outputRune = new EntityItem(world, getPos().getX() + 0.5, getPos().getY() + 1.5, getPos().getZ() + 0.5, stack.copy());
                            world.spawnEntity(outputRune);
                        }
                        itemHandler.setStackInSlot(i, ItemStack.EMPTY);
                    }
                }
                livingrock.getItem().shrink(1);
            }
        }
    }
    @Shadow(remap = false)
    public void saveLastRecipe() {}
}
