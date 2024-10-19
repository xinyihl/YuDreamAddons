package com.yudream.yudreamaddons;

import com.yudream.yudreamaddons.common.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = YuDreamAddons.MOD_ID, name = YuDreamAddons.NAME, version = YuDreamAddons.VERSION, dependencies = "required:mixinbooter")
public class YuDreamAddons {
    public static final String MOD_ID = "yudreamaddons";
    public static final String NAME = "YuDreamAddons";
    public static final String VERSION = "1.0.4";
    @SidedProxy(clientSide = "com.yudream.yudreamaddons.client.ClientProxy", serverSide = "com.yudream.yudreamaddons.common.CommonProxy")
    public static CommonProxy PROXY;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        PROXY.init();
    }
}
