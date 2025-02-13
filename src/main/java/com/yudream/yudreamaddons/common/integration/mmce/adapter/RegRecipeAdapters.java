package com.yudream.yudreamaddons.common.integration.mmce.adapter;

import com.yudream.yudreamaddons.common.Mods;
import com.yudream.yudreamaddons.common.integration.mmce.adapter.ie.AdapterIEArcFurnace;
import com.yudream.yudreamaddons.common.integration.mmce.adapter.tc6.AdapterTC6Crucible;
import com.yudream.yudreamaddons.common.integration.mmce.adapter.tc6.AdapterTC6InfusionMatrix;
import com.yudream.yudreamaddons.common.integration.mmce.adapter.tc6.AdapterTC6Smelter;
import com.yudream.yudreamaddons.common.integration.mmce.adapter.tconstruct.AdapterSmelteryBasinCasting;
import com.yudream.yudreamaddons.common.integration.mmce.adapter.tconstruct.AdapterSmelteryTableCasting;

import static hellfirepvp.modularmachinery.common.registry.RegistryRecipeAdapters.registerAdapter;

public class RegRecipeAdapters {
    public static void initialize() {
        if (Mods.TC6.isLoaded()) {
            registerAdapter(new AdapterTC6Crucible());
            registerAdapter(new AdapterTC6InfusionMatrix());
            registerAdapter(new AdapterTC6Smelter());
        }
        if (Mods.IE.isLoaded()) {
            registerAdapter(new AdapterIEArcFurnace());
        }
        if (Mods.TCO.isLoaded()) {
            registerAdapter(new AdapterSmelteryBasinCasting());
            registerAdapter(new AdapterSmelteryTableCasting());
        }
    }
}
