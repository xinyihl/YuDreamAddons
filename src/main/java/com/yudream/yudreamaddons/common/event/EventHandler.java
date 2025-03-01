package com.yudream.yudreamaddons.common.event;

import blusunrize.immersiveengineering.common.util.IEDamageSources.ElectricDamageSource;
import com.yudream.yudreamaddons.Configurations;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventHandler {
    @SubscribeEvent
    public void onHurt(LivingAttackEvent event) {
        if (!Configurations.GENERAL_CONFIG.doElectricUnground && event.getSource() instanceof ElectricDamageSource && !event.getEntity().onGround) {
            ElectricDamageSource dmg = (ElectricDamageSource) event.getSource();
            dmg.dmg = 0;
            event.setCanceled(true);
        }
    }
}
