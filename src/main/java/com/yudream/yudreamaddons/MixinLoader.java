package com.yudream.yudreamaddons;

import com.google.common.collect.Lists;
import net.minecraftforge.fml.common.Loader;
import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.List;

public class MixinLoader implements ILateMixinLoader {
    private final String[] mixins = {"botania", "appliedenergistics2", "mekeng", "nae2", "jei", "ae2fc", "astralsorcery", "ftblib", "valkyrielib"};
    @Override
    public List<String> getMixinConfigs() {
        List<String> mixinconfigs = Lists.newArrayList();
        for(String mixin : mixins) mixinconfigs.add("mixins.yudreamaddons_" + mixin + ".json");
        return mixinconfigs;
    }
    @Override
    public boolean shouldMixinConfigQueue(String mixinConfig) {
        return Loader.isModLoaded(mixinConfig.substring(mixinConfig.indexOf('_') + 1, mixinConfig.lastIndexOf('.')));
    }
}
