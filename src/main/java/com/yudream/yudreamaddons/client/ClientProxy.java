package com.yudream.yudreamaddons.client;

import com.yudream.yudreamaddons.common.CommonProxy;

@SuppressWarnings("unused")
public class ClientProxy extends CommonProxy {
    public final ClientEvents CLIENT_EVENTS = new ClientEvents();

    @Override
    public void init() {
        CLIENT_EVENTS.registerKeyBindings();
    }
}
