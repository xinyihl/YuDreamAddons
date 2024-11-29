package com.yudream.yudreamaddons.common.server;

import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import blusunrize.immersiveengineering.common.util.IEDamageSources.ElectricDamageSource;

import static com.yudream.yudreamaddons.Configuration.DO_ELECTRIC_UNGROUND;

public class ServerEventHandler {
    @SubscribeEvent
    public void onHurt(LivingAttackEvent event) {
        if(!DO_ELECTRIC_UNGROUND && event.getSource() instanceof ElectricDamageSource && !event.getEntity().onGround) {
            ElectricDamageSource dmg = (ElectricDamageSource) event.getSource();
            dmg.dmg = 0;
            event.setCanceled(true);
        }
    }
}
