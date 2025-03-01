package com.yudream.yudreamaddons.common.proxy;

import com.yudream.yudreamaddons.client.event.GetItemKeyHandler;
import com.yudream.yudreamaddons.client.event.WandKeyHandler;
import net.minecraftforge.common.MinecraftForge;

@SuppressWarnings("unused")
public class ClientProxy extends CommonProxy {

    @Override
    public void preInit() {
        super.preInit();
    }

    @Override
    public void init() {
        super.init();
        MinecraftForge.EVENT_BUS.register(new WandKeyHandler());
        MinecraftForge.EVENT_BUS.register(new GetItemKeyHandler());
    }
}
