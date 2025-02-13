package com.yudream.yudreamaddons.common.proxy;

import com.yudream.yudreamaddons.common.server.ServerEventHandler;
import net.minecraftforge.common.MinecraftForge;

public abstract class CommonProxy {
    public void preInit() {
    }

    public void init() {
        MinecraftForge.EVENT_BUS.register(new ServerEventHandler());
    }
}
