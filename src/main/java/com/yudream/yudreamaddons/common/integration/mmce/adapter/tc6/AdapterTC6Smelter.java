package com.yudream.yudreamaddons.common.integration.mmce.adapter.tc6;

import com.warmthdawn.mod.gugu_utils.modularmachenary.MMRequirements;
import com.warmthdawn.mod.gugu_utils.modularmachenary.requirements.RequirementAspectOutput;
import com.warmthdawn.mod.gugu_utils.modularmachenary.requirements.types.RequirementTypeAspect;
import com.yudream.yudreamaddons.YuDreamAddons;
import com.yudream.yudreamaddons.common.util.Utils;
import crafttweaker.util.IEventHandler;
import github.kasuminova.mmce.common.event.recipe.RecipeEvent;
import hellfirepvp.modularmachinery.common.crafting.MachineRecipe;
import hellfirepvp.modularmachinery.common.crafting.adapter.RecipeAdapter;
import hellfirepvp.modularmachinery.common.crafting.helper.ComponentRequirement;
import hellfirepvp.modularmachinery.common.crafting.requirement.RequirementItem;
import hellfirepvp.modularmachinery.common.lib.RequirementTypesMM;
import hellfirepvp.modularmachinery.common.machine.IOType;
import hellfirepvp.modularmachinery.common.modifier.RecipeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.yudream.yudreamaddons.Configurations.OTHER_CONFIG;

public class AdapterTC6Smelter extends RecipeAdapter {

    private static final Logger log = LogManager.getLogger(AdapterTC6Smelter.class);
    private static Map<ItemStack, AspectList> itemAspectListMap = new HashMap<>();

    public AdapterTC6Smelter() {
        super(new ResourceLocation("thaumcraft", "yudream_smelter"));
    }

    @Nonnull
    public Collection<MachineRecipe> createRecipesFor(ResourceLocation owningMachineName, List<RecipeModifier> modifiers, List<ComponentRequirement<?, ?>> additionalRequirements, Map<Class<?>, List<IEventHandler<RecipeEvent>>> eventHandlers, List<String> recipeTooltips) {
        File file = new File(YuDreamAddons.instance.configDir, "thaumcraft_smelter.dat");
        List<MachineRecipe> machineRecipeList = new ArrayList<>();
        if (file.exists()) {
            try {
                itemAspectListMap = Utils.tc6SmelterDeserialize(file);
            } catch (IOException e) {
                log.info("[YuDreamAddons] 源质演练厂配方缓存读取失败，重新建立缓存");
                throw new RuntimeException(e);
            }
        }
        boolean isFirstLoad = itemAspectListMap.isEmpty();
        if (isFirstLoad) {
            Utils.getAllItemStacks().forEach(itemStack -> {
                if (itemStack.isEmpty()) {
                    return;
                }
                AspectList al = ThaumcraftCraftingManager.getObjectTags(itemStack);
                if (al == null || al.size() == 0) {
                    return;
                }
                itemAspectListMap.put(itemStack, al);
            });
            try {
                Utils.tc6SmelterSerialize(itemAspectListMap, file);
                log.info("[YuDreamAddons] 源质演练厂配方缓存保存成功, {}", file);
            } catch (IOException e) {
                log.error("[YuDreamAddons] 源质演练厂配方缓存保存失败, {}", file);
                //noinspection ResultOfMethodCallIgnored
                file.delete();
                throw new RuntimeException(e);
            }
        }
        itemAspectListMap.forEach((itemStack, aspectList) -> {
            if (itemStack.isEmpty()) {
                return;
            }
            MachineRecipe machineRecipe = createRecipeShell(
                    new ResourceLocation("thaumcraft", "yudream_auto_smelter" + incId),
                    owningMachineName,
                    OTHER_CONFIG.smelterTime,
                    incId, false);

            int inAmount = Math.round(RecipeModifier.applyModifiers(modifiers, RequirementTypesMM.REQUIREMENT_ITEM, IOType.INPUT, itemStack.getCount(), false));
            if (inAmount <= 0) {
                return;
            }
            itemStack.setCount(inAmount);
            machineRecipe.addRequirement(new RequirementItem(IOType.INPUT, itemStack));

            aspectList.aspects.forEach((aspect, integer) -> {
                int outAmount = Math.round(RecipeModifier.applyModifiers(modifiers, (RequirementTypeAspect) MMRequirements.REQUIREMENT_TYPE_ASPECT, IOType.OUTPUT, integer, false));
                if (outAmount <= 0) {
                    return;
                }
                machineRecipe.addRequirement(new RequirementAspectOutput(outAmount, aspect));
            });
            machineRecipeList.add(machineRecipe);
            incId++;
        });
        return machineRecipeList;
    }
}
