package com.yudream.yudreamaddons.common.proxy;

import com.yudream.yudreamaddons.common.server.ServerEventHandler;
import net.minecraftforge.common.MinecraftForge;

@SuppressWarnings("unused")
public class ServerProxy implements CommonProxy {
    @Override
    public void init() {
        MinecraftForge.EVENT_BUS.register(new ServerEventHandler());
    }
}
