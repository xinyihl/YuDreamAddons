package com.yudream.yudreamaddons;

import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import zone.rong.mixinbooter.IEarlyMixinLoader;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
@IFMLLoadingPlugin.Name(Tags.MOD_ID)
@IFMLLoadingPlugin.MCVersion(ForgeVersion.mcVersion)
public class EarlyMixinLoader implements IFMLLoadingPlugin, IEarlyMixinLoader {
    @Override
    public List<String> getMixinConfigs() {
        return Collections.singletonList("mixins.yudreamaddons_minecraft.json");
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
