package com.yudream.yudreamaddons.common.mmce.adapter;

import com.yudream.yudreamaddons.common.mmce.adapter.ie.AdapterIEArcFurnace;
import com.yudream.yudreamaddons.common.mmce.adapter.tc6.AdapterTC6Crucible;
import com.yudream.yudreamaddons.common.mmce.adapter.tc6.AdapterTC6InfusionMatrix;
import net.minecraftforge.fml.common.Loader;

import static hellfirepvp.modularmachinery.common.registry.RegistryRecipeAdapters.registerAdapter;

public class RegRecipeAdapters {
    public static void initialize() {
        if (Loader.isModLoaded("thaumcraft")) {
            registerAdapter(new AdapterTC6Crucible());
            registerAdapter(new AdapterTC6InfusionMatrix());
        }
        if (Loader.isModLoaded("immersiveengineering")) {
            registerAdapter(new AdapterIEArcFurnace());
        }
    }
}
