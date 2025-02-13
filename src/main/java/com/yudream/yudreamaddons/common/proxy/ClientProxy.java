package com.yudream.yudreamaddons.common.proxy;

import com.yudream.yudreamaddons.common.client.ClientEventHandler;
import com.yudream.yudreamaddons.common.client.WandKeyEventHandler;
import com.yudream.yudreamaddons.common.integration.TheOneProbe;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInterModComms;

@SuppressWarnings("unused")
public class ClientProxy extends CommonProxy {

    @Override
    public void preInit() {
        super.preInit();
        FMLInterModComms.sendFunctionMessage("theoneprobe", "getTheOneProbe", TheOneProbe.class.getName());
    }

    @Override
    public void init() {
        super.init();
        MinecraftForge.EVENT_BUS.register(new WandKeyEventHandler());
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
    }
}
