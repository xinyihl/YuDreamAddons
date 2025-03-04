package com.yudream.yudreamaddons.common.proxy;

import com.yudream.yudreamaddons.YuDreamAddons;
import com.yudream.yudreamaddons.common.container.GUIContainerHandler;
import com.yudream.yudreamaddons.common.event.EventHandler;
import com.yudream.yudreamaddons.common.integration.TheOneProbe;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class CommonProxy {
    public void preInit() {
        FMLInterModComms.sendFunctionMessage("theoneprobe", "getTheOneProbe", TheOneProbe.class.getName());
    }

    public void init() {
        MinecraftForge.EVENT_BUS.register(new EventHandler());
        NetworkRegistry.INSTANCE.registerGuiHandler(YuDreamAddons.instance, new GUIContainerHandler());
    }
}
