package com.yudream.yudreamaddons;

import com.yudream.yudreamaddons.common.proxy.CommonProxy;
import com.yudream.yudreamaddons.yudreamaddons.Tags;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = Tags.MOD_ID, name = Tags.MOD_NAME, version = Tags.VERSION, dependencies = "required:mixinbooter")
public class YuDreamAddons {
    public static final String MOD_ID = "yudreamaddons";
    public static final String NAME = "YuDreamAddons";
    public static final String VERSION = "1.0.6";
    @SidedProxy(clientSide = "com.yudream.yudreamaddons.common.proxy.ClientProxy", serverSide = "com.yudream.yudreamaddons.common.proxy.CommonProxy")
    public static CommonProxy PROXY;
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        PROXY.init();
    }
}
