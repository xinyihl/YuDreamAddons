package com.yudream.yudreamaddons.common.mmce.adapter.tc6;

import com.warmthdawn.mod.gugu_utils.modularmachenary.requirements.RequirementAspectOutput;
import crafttweaker.util.IEventHandler;
import github.kasuminova.mmce.common.event.recipe.RecipeEvent;
import hellfirepvp.modularmachinery.common.crafting.MachineRecipe;
import hellfirepvp.modularmachinery.common.crafting.adapter.RecipeAdapter;
import hellfirepvp.modularmachinery.common.crafting.helper.ComponentRequirement;
import hellfirepvp.modularmachinery.common.crafting.requirement.RequirementItem;
import hellfirepvp.modularmachinery.common.machine.IOType;
import hellfirepvp.modularmachinery.common.modifier.RecipeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.yudream.yudreamaddons.Configurations.OTHER_CONFIG;

public class AdapterTC6Smelter extends RecipeAdapter {

    public AdapterTC6Smelter() {
        super(new ResourceLocation("thaumcraft", "yudream_smelter"));
    }
    @Nonnull
    public Collection<MachineRecipe> createRecipesFor(ResourceLocation owningMachineName, List<RecipeModifier> modifiers, List<ComponentRequirement<?, ?>> additionalRequirements, Map<Class<?>, List<IEventHandler<RecipeEvent>>> eventHandlers, List<String> recipeTooltips) {
        List<MachineRecipe> machineRecipeList = new ArrayList<>();

        ForgeRegistries.ITEMS.forEach(item -> {
            ItemStack itemStack = new ItemStack(item);
            if (itemStack.isEmpty()) {
                return;
            }

            AspectList al = ThaumcraftCraftingManager.getObjectTags(itemStack);
            if (al != null && al.size() != 0){
                MachineRecipe machineRecipe = createRecipeShell(
                        new ResourceLocation("thaumcraft", "yudream_auto_smelter" + incId),
                        owningMachineName,
                        OTHER_CONFIG.smelterTime,
                        incId, false);

                machineRecipe.addRequirement(new RequirementItem(IOType.INPUT, itemStack));
                al.aspects.forEach((aspect, integer) -> machineRecipe.addRequirement(new RequirementAspectOutput(integer, aspect)));

                machineRecipeList.add(machineRecipe);
                incId++;
            }
        });

        return machineRecipeList;
    }
}
