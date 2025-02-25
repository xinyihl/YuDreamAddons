package com.yudream.yudreamaddons.common.integration.mmce.adapter.tc6;

import com.google.gson.Gson;
import com.warmthdawn.mod.gugu_utils.modularmachenary.MMRequirements;
import com.warmthdawn.mod.gugu_utils.modularmachenary.requirements.RequirementAspectOutput;
import com.warmthdawn.mod.gugu_utils.modularmachenary.requirements.types.RequirementTypeAspect;
import com.yudream.yudreamaddons.YuDreamAddons;
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
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import static com.yudream.yudreamaddons.Configurations.OTHER_CONFIG;

public class AdapterTC6Smelter extends RecipeAdapter {

    private static final Logger log = LogManager.getLogger(AdapterTC6Smelter.class);
    private static final Map<ItemStack, AspectList> itemAspectListMap = new HashMap<>();
    private static final Gson GSON = new Gson();

    public AdapterTC6Smelter() {
        super(new ResourceLocation("thaumcraft", "yudream_smelter"));
    }

    @Nonnull
    public Collection<MachineRecipe> createRecipesFor(ResourceLocation owningMachineName, List<RecipeModifier> modifiers, List<ComponentRequirement<?, ?>> additionalRequirements, Map<Class<?>, List<IEventHandler<RecipeEvent>>> eventHandlers, List<String> recipeTooltips) {
        File file = new File(YuDreamAddons.instance.configDir, "thaumicjei_itemstack_aspects.json");
        List<MachineRecipe> machineRecipeList = new ArrayList<>();

        this.optimizedMethod(file);

        if (!itemAspectListMap.isEmpty()) {
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
        }
        return machineRecipeList;
    }


    private void optimizedMethod(File file) {
        if (!file.exists()) {
            return;
        }
        try (FileReader reader = new FileReader(file)) {
            AspectCache[] aspectCaches = GSON.fromJson(reader, AspectCache[].class);
            for (AspectCache aspectCache : aspectCaches) {
                processAspectCache(aspectCache);
            }
        } catch (IOException e) {
            log.warn("Failed to load aspect caches from file");
        }
    }

    private void processAspectCache(AspectCache aspectCache) {
        Aspect aspect = Aspect.getAspect(aspectCache.aspect);
        if (aspect == null) {
            return;
        }
        aspectCache.items.stream()
                .flatMap(this::safeParseNBT)
                .flatMap(this::processCompound)
                .filter(stack -> !stack.isEmpty())
                .forEach(stack -> updateAspectMap(aspect, stack));
    }

    private Stream<NBTTagCompound> safeParseNBT(String json) {
        try {
            return Stream.of(JsonToNBT.getTagFromJson(json));
        } catch (NBTException e) {
            return Stream.empty();
        }
    }

    private Stream<ItemStack> processCompound(NBTTagCompound compound) {
        try {
            short trueCount = compound.getShort("Count");
            if (trueCount <= 0) {
                return Stream.empty();
            }
            ItemStack stack = new ItemStack(compound);
            return Stream.of(stack);
        } catch (ClassCastException e) {
            return Stream.empty();
        }
    }

    private void updateAspectMap(Aspect aspect, ItemStack stack) {
        ItemStack keyStack = stack.copy();
        keyStack.setCount(1);
        AtomicBoolean tag = new AtomicBoolean(true);
        itemAspectListMap.forEach((machineRecipe, aspectList) -> {
            if(ItemStack.areItemStacksEqual(machineRecipe, keyStack)){
                aspectList.add(aspect, stack.getCount());
                tag.set(false);
            }
        });
        if (tag.get()) {
            AspectList aspectList = itemAspectListMap.computeIfAbsent(keyStack, k -> new AspectList());
            aspectList.add(aspect, stack.getCount());
        }
    }

    private static class AspectCache {
        private String aspect;
        private List<String> items;
        public AspectCache() {
            this.items = new ArrayList<>();
        }
        public AspectCache(String aspect) {
            this();
            this.aspect = aspect;
        }
    }
}
