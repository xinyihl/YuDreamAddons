package com.yudream.yudreamaddons.common;

import net.minecraftforge.fml.common.Loader;

public enum Mods {
    BOTANIA("botania", true),
    AE2("appliedenergistics2", true),
    MEKENG("mekeng", true),
    NAE2("nae2", true),
    JEI("jei", true),
    AE2FC("ae2fc", true),
    AST("astralsorcery", true),
    FTBLIB("ftblib", true),
    VALKLIB("valkyrielib", true),
    EXU2("extrautils2", true),
    BBW("betterbuilderswands", true),
    TMF("tinymobfarm", true),
    TCOM("tcomplement", true),
    TCO("tconstruct", false),
    MMCE("modularmachinery", true),
    TC6("thaumcraft", false),
    IE("immersiveengineering", false);
    private final boolean mixin;
    public final String modid;
    private final boolean loaded;

    Mods(String modid, boolean mixin) {
        this.modid = modid;
        this.loaded = Loader.isModLoaded(this.modid);
        this.mixin = mixin;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public boolean isMixin(){
        return mixin;
    }
}
