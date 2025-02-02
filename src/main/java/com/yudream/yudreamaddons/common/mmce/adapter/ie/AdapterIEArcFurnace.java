package com.yudream.yudreamaddons.common.mmce.adapter.ie;

import crafttweaker.util.IEventHandler;
import github.kasuminova.mmce.common.event.recipe.RecipeEvent;
import github.kasuminova.mmce.common.itemtype.ChancedIngredientStack;
import hellfirepvp.modularmachinery.common.crafting.MachineRecipe;
import hellfirepvp.modularmachinery.common.crafting.adapter.RecipeAdapter;
import hellfirepvp.modularmachinery.common.crafting.helper.ComponentRequirement;
import hellfirepvp.modularmachinery.common.crafting.requirement.RequirementEnergy;
import hellfirepvp.modularmachinery.common.crafting.requirement.RequirementIngredientArray;
import hellfirepvp.modularmachinery.common.crafting.requirement.RequirementItem;
import hellfirepvp.modularmachinery.common.lib.RequirementTypesMM;
import hellfirepvp.modularmachinery.common.machine.IOType;
import hellfirepvp.modularmachinery.common.modifier.RecipeModifier;
import hellfirepvp.modularmachinery.common.util.ItemUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

import static blusunrize.immersiveengineering.api.crafting.ArcFurnaceRecipe.recipeList;

public class AdapterIEArcFurnace extends RecipeAdapter {
    public AdapterIEArcFurnace() {
        super(new ResourceLocation("immersiveengineering", "yudream_arcfurnace"));
    }

    @Nonnull
    @Override
    public Collection<MachineRecipe> createRecipesFor(ResourceLocation owningMachineName, List<RecipeModifier> modifiers, List<ComponentRequirement<?, ?>> additionalRequirements, Map<Class<?>, List<IEventHandler<RecipeEvent>>> eventHandlers, List<String> recipeTooltips) {
        List<MachineRecipe> machineRecipeList = new ArrayList<>();

        recipeList.forEach((recipe)->{
            if (recipe.input == null) {
                return;
            }
            if (recipe.additives == null) {
                return;
            }
            if (recipe.output == null) {
                return;
            }
            int inAmount = Math.round(RecipeModifier.applyModifiers(modifiers, RequirementTypesMM.REQUIREMENT_ITEM, IOType.INPUT, 1, false));
            if (inAmount <= 0) {
                return;
            }

            MachineRecipe machineRecipe = createRecipeShell(
                    new ResourceLocation("immersiveengineering", "yudream_auto_arcfurnace" + incId),
                    owningMachineName,
                    recipe.getTotalProcessTime(),
                    incId, false);

            // Input
            ItemStack[] inputMain = recipe.input.toRecipeIngredient().getMatchingStacks();
            List<ChancedIngredientStack> inputMainList = Arrays.stream(inputMain)
                    .map(itemStack -> new ChancedIngredientStack(ItemUtils.copyStackWithSize(itemStack, inAmount)))
                    .collect(Collectors.toList());
            if (!inputMainList.isEmpty()) {
                machineRecipe.addRequirement(new RequirementIngredientArray(inputMainList));
            }

            int energyPerTick = Math.round(RecipeModifier.applyModifiers(
                    modifiers, RequirementTypesMM.REQUIREMENT_ENERGY, IOType.INPUT, (float) recipe.getTotalProcessEnergy() / (float) recipe.getTotalProcessTime() , false)
            );
            // Energy
            if (energyPerTick > 0) {
                machineRecipe.addRequirement(new RequirementEnergy(IOType.INPUT, energyPerTick));
            }

            // Output
            ItemStack output = recipe.output;
            int outAmount = Math.round(RecipeModifier.applyModifiers(modifiers, RequirementTypesMM.REQUIREMENT_ITEM, IOType.OUTPUT, output.getCount(), false));
            if (outAmount > 0) {
                machineRecipe.addRequirement(new RequirementItem(IOType.OUTPUT, ItemUtils.copyStackWithSize(output, outAmount)));
            }

            machineRecipeList.add(machineRecipe);
            incId++;
        });
        return machineRecipeList;
    }
}
