package com.yudream.yudreamaddons.common.proxy;

import com.yudream.yudreamaddons.common.client.ClientEventHandler;
import net.minecraftforge.common.MinecraftForge;

@SuppressWarnings("unused")
public class ClientProxy extends CommonProxy {
    @Override
    public void init() {
        super.init();
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
    }
}
