package com.yudream.yudreamaddons.common.proxy;

import com.yudream.yudreamaddons.common.client.ClientEventHandler;
import net.minecraftforge.common.MinecraftForge;

@SuppressWarnings("unused")
public class ClientProxy implements CommonProxy {
    @Override
    public void init() {
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
    }
}
