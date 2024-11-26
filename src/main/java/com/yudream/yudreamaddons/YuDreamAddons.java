package com.yudream.yudreamaddons;

import com.yudream.yudreamaddons.common.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = YuDreamAddons.MOD_ID, name = YuDreamAddons.NAME, version = YuDreamAddons.VERSION, dependencies = "required:mixinbooter")
public class YuDreamAddons {
    public static final String MOD_ID = "yudreamaddons";
    public static final String NAME = "YuDreamAddons";
    public static final String VERSION = "1.0.5";
    @SidedProxy(clientSide = "com.yudream.yudreamaddons.common.proxy.ClientProxy", serverSide = "com.yudream.yudreamaddons.common.proxy.ServerProxy")
    public static CommonProxy PROXY;
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        PROXY.init();
    }
}
