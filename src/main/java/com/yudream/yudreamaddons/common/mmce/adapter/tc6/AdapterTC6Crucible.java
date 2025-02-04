package com.yudream.yudreamaddons.common.mmce.adapter.tc6;

import com.warmthdawn.mod.gugu_utils.modularmachenary.requirements.RequirementAspect;
import crafttweaker.util.IEventHandler;
import github.kasuminova.mmce.common.event.recipe.RecipeEvent;
import github.kasuminova.mmce.common.itemtype.ChancedIngredientStack;
import hellfirepvp.modularmachinery.common.crafting.MachineRecipe;
import hellfirepvp.modularmachinery.common.crafting.adapter.RecipeAdapter;
import hellfirepvp.modularmachinery.common.crafting.helper.ComponentRequirement;
import hellfirepvp.modularmachinery.common.crafting.requirement.RequirementIngredientArray;
import hellfirepvp.modularmachinery.common.crafting.requirement.RequirementItem;
import hellfirepvp.modularmachinery.common.lib.RequirementTypesMM;
import hellfirepvp.modularmachinery.common.machine.IOType;
import hellfirepvp.modularmachinery.common.modifier.RecipeModifier;
import hellfirepvp.modularmachinery.common.util.ItemUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.crafting.CrucibleRecipe;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

import static com.yudream.yudreamaddons.Configurations.OTHER_CONFIG;

public class AdapterTC6Crucible extends RecipeAdapter {
    public AdapterTC6Crucible() {
        super(new ResourceLocation("thaumcraft", "yudream_crucible"));
    }

    @Nonnull
    @Override
    public Collection<MachineRecipe> createRecipesFor(ResourceLocation owningMachineName, List<RecipeModifier> modifiers, List<ComponentRequirement<?, ?>> additionalRequirements, Map<Class<?>, List<IEventHandler<RecipeEvent>>> eventHandlers, List<String> recipeTooltips) {
        List<MachineRecipe> machineRecipeList = new ArrayList<>();

        ThaumcraftApi.getCraftingRecipes().forEach((recipeName, tcRecipe) -> {
            if (!(tcRecipe instanceof CrucibleRecipe)) {
                return;
            }
            CrucibleRecipe recipe = (CrucibleRecipe) tcRecipe;
            if (recipe.getCatalyst() == null) {
                return;
            }
            if (recipe.getRecipeOutput() == null) {
                return;
            }
            int inAmount = Math.round(RecipeModifier.applyModifiers(modifiers, RequirementTypesMM.REQUIREMENT_ITEM, IOType.INPUT, 1, false));
            if (inAmount <= 0) {
                return;
            }

            MachineRecipe machineRecipe = createRecipeShell(
                    new ResourceLocation("thaumcraft", "yudream_auto_crucible" + incId),
                    owningMachineName,
                    OTHER_CONFIG.crucibleTime,
                    incId, false);

            // Item Input
            ItemStack[] inputMain = recipe.getCatalyst().getMatchingStacks();
            List<ChancedIngredientStack> inputMainList = Arrays.stream(inputMain)
                    .map(itemStack -> new ChancedIngredientStack(ItemUtils.copyStackWithSize(itemStack, inAmount)))
                    .collect(Collectors.toList());
            if (!inputMainList.isEmpty()) {
                machineRecipe.addRequirement(new RequirementIngredientArray(inputMainList));
            }

            // Aspect Inputs
            recipe.getAspects().aspects.forEach((aspect, amount) -> machineRecipe.addRequirement(RequirementAspect.createInput(amount, aspect)));

            // Output
            ItemStack output = recipe.getRecipeOutput();
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
