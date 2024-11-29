package com.yudream.yudreamaddons.common.proxy;

import com.yudream.yudreamaddons.common.server.ServerEventHandler;
import net.minecraftforge.common.MinecraftForge;

public class CommonProxy {
    public void init() {
        MinecraftForge.EVENT_BUS.register(new ServerEventHandler());
    }
}
