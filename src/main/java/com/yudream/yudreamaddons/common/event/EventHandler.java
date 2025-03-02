package com.yudream.yudreamaddons.common.event;

import blusunrize.immersiveengineering.common.util.IEDamageSources.ElectricDamageSource;
import com.yudream.yudreamaddons.Configurations;
import com.yudream.yudreamaddons.YuDreamAddons;
import com.yudream.yudreamaddons.common.api.NetworkHubDataStorage;
import com.yudream.yudreamaddons.common.api.NetworkStatus;
import com.yudream.yudreamaddons.common.network.PacketNHStorage;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EventHandler {
    @SubscribeEvent
    public void onHurt(LivingAttackEvent event) {
        if (!Configurations.GENERAL_CONFIG.doElectricUnground && event.getSource() instanceof ElectricDamageSource && !event.getEntity().onGround) {
            ElectricDamageSource dmg = (ElectricDamageSource) event.getSource();
            dmg.dmg = 0;
            event.setCanceled(true);
        }
    }

    private final Map<UUID, Integer> oldNetworks = new HashMap<>();

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent event) {
        if (event.side.isClient()) return;
        if (event.phase != Phase.START) return;
        if (!(event.player instanceof EntityPlayerMP)) return;
        if (event.player.world.getTotalWorldTime() % 20 != 0) return;
        World world = event.player.world;
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        NetworkHubDataStorage storage = NetworkHubDataStorage.get(world);
        List<NetworkStatus> networks = storage.getAllNetworks(player);
        int newHash = networks.hashCode();
        int oldHash = oldNetworks.getOrDefault(player.getGameProfile().getId(), 0);
        if (oldHash != newHash) {
            oldNetworks.put(player.getGameProfile().getId(), newHash);
            NBTTagCompound tag = new NBTTagCompound();
            NBTTagList list = new NBTTagList();
            for (NetworkStatus network : networks) {
                list.appendTag(network.writeToNBT(new NBTTagCompound()));
            }
            tag.setTag("networks", list);
            YuDreamAddons.instance.networkWrapper.sendTo(new PacketNHStorage(tag), player);
        }
    }
}
