package com.yudream.yudreamaddons.common.proxy;

import com.yudream.yudreamaddons.common.integration.TheOneProbe;
import com.yudream.yudreamaddons.common.server.ServerEventHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInterModComms;

public class CommonProxy {
    public void preInit() {
        FMLInterModComms.sendFunctionMessage("theoneprobe", "getTheOneProbe", TheOneProbe.class.getName());
    }

    public void init() {
        MinecraftForge.EVENT_BUS.register(new ServerEventHandler());
    }
}
