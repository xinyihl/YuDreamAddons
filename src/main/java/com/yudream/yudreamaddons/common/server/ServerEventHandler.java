package com.yudream.yudreamaddons.common.server;

import blusunrize.immersiveengineering.common.util.IEDamageSources.ElectricDamageSource;
import com.yudream.yudreamaddons.Configurations;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ServerEventHandler {
    @SubscribeEvent
    public void onHurt(LivingAttackEvent event) {
        if (!Configurations.GENERAL.doElectricUnground && event.getSource() instanceof ElectricDamageSource && !event.getEntity().onGround) {
            ElectricDamageSource dmg = (ElectricDamageSource) event.getSource();
            dmg.dmg = 0;
            event.setCanceled(true);
        }
    }
}
