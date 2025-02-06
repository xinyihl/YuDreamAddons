package com.yudream.yudreamaddons.common.mmce.adapter.tc6;

import com.warmthdawn.mod.gugu_utils.modularmachenary.requirements.RequirementAspectOutput;
import com.yudream.yudreamaddons.common.util.Utils;
import crafttweaker.util.IEventHandler;
import github.kasuminova.mmce.common.event.recipe.RecipeEvent;
import hellfirepvp.modularmachinery.common.crafting.MachineRecipe;
import hellfirepvp.modularmachinery.common.crafting.adapter.RecipeAdapter;
import hellfirepvp.modularmachinery.common.crafting.helper.ComponentRequirement;
import hellfirepvp.modularmachinery.common.crafting.requirement.RequirementItem;
import hellfirepvp.modularmachinery.common.machine.IOType;
import hellfirepvp.modularmachinery.common.modifier.RecipeModifier;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
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

    private static final File file = new File(Minecraft.getMinecraft().gameDir, "config/thaumcraft_smelter.dat");
    private static final Logger log = LogManager.getLogger(AdapterTC6Smelter.class);
    private static Map<Item, AspectList> itemAspectListMap = new HashMap<>();

    public AdapterTC6Smelter() {
        super(new ResourceLocation("thaumcraft", "yudream_smelter"));
    }

    @Nonnull
    public Collection<MachineRecipe> createRecipesFor(ResourceLocation owningMachineName, List<RecipeModifier> modifiers, List<ComponentRequirement<?, ?>> additionalRequirements, Map<Class<?>, List<IEventHandler<RecipeEvent>>> eventHandlers, List<String> recipeTooltips) {
        List<MachineRecipe> machineRecipeList = new ArrayList<>();
        if (file.exists()) {
            try {
                itemAspectListMap = Utils.tc6SmelterDeserialize(file);
            } catch (IOException e) {
                itemAspectListMap = new HashMap<>();
                throw new RuntimeException(e);
            }
        }
        boolean isFirstLoad = itemAspectListMap.isEmpty();
        if (isFirstLoad) {
            ForgeRegistries.ITEMS.forEach(item -> {
                ItemStack itemStack = new ItemStack(item);
                if (itemStack.isEmpty()) {
                    return;
                }
                AspectList al = ThaumcraftCraftingManager.getObjectTags(itemStack);
                if (al != null && al.size() != 0) {
                    MachineRecipe machineRecipe = createRecipeShell(
                            new ResourceLocation("thaumcraft", "yudream_auto_smelter" + incId),
                            owningMachineName,
                            OTHER_CONFIG.smelterTime,
                            incId, false);

                    machineRecipe.addRequirement(new RequirementItem(IOType.INPUT, itemStack));
                    al.aspects.forEach((aspect, integer) -> machineRecipe.addRequirement(new RequirementAspectOutput(integer, aspect)));
                    itemAspectListMap.put(item, al);
                    machineRecipeList.add(machineRecipe);
                    incId++;
                }
            });
            try {
                Utils.tc6SmelterSerialize(itemAspectListMap, file);
            } catch (IOException e) {
                log.error("[YuDreamAddons] 源质演练厂配方缓存保存失败");
                //noinspection ResultOfMethodCallIgnored
                file.delete();
                throw new RuntimeException(e);
            }
        } else {
            itemAspectListMap.forEach((item, aspectList) -> {
                ItemStack itemStack = new ItemStack(item);
                if (itemStack.isEmpty()) {
                    return;
                }
                MachineRecipe machineRecipe = createRecipeShell(
                        new ResourceLocation("thaumcraft", "yudream_auto_smelter" + incId),
                        owningMachineName,
                        OTHER_CONFIG.smelterTime,
                        incId, false);

                machineRecipe.addRequirement(new RequirementItem(IOType.INPUT, itemStack));
                aspectList.aspects.forEach((aspect, integer) -> machineRecipe.addRequirement(new RequirementAspectOutput(integer, aspect)));
                machineRecipeList.add(machineRecipe);
                incId++;
            });
        }
        return machineRecipeList;
    }
}
