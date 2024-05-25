package com.yudream.yudreamaddons;

import com.google.common.collect.Lists;
import net.minecraftforge.fml.common.Loader;
import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.List;

public class MixinLoader implements ILateMixinLoader {
    @Override
    public List<String> getMixinConfigs() {
        return Lists.newArrayList(
                "mixins.yudreamaddons_botania.json",
                "mixins.yudreamaddons_appliedenergistics2.json",
                "mixins.yudreamaddons_nae2.json"
        );
    }
    @Override
    public boolean shouldMixinConfigQueue(String mixinConfig) {
        return Loader.isModLoaded(mixinConfig.substring(mixinConfig.indexOf('_') + 1, mixinConfig.lastIndexOf('.')));
    }
}
