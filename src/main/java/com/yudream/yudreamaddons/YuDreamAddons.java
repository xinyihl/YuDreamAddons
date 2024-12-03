package com.yudream.yudreamaddons;

import com.yudream.yudreamaddons.common.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = Tags.MOD_ID, name = Tags.MOD_NAME, version = Tags.VERSION, dependencies = "required-after:configanytime@[2.0,);required-after:mixinbooter@[8.0,)")
public class YuDreamAddons {
    @SidedProxy(clientSide = "com.yudream.yudreamaddons.common.proxy.ClientProxy", serverSide = "com.yudream.yudreamaddons.common.proxy.CommonProxy")
    public static CommonProxy PROXY;
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        PROXY.init();
    }
}
