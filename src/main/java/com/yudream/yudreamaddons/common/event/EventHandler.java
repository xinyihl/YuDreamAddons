package com.yudream.yudreamaddons.common.event;

import blusunrize.immersiveengineering.common.util.IEDamageSources.ElectricDamageSource;
import com.yudream.yudreamaddons.Configurations;
import com.yudream.yudreamaddons.YuDreamAddons;
import com.yudream.yudreamaddons.common.api.IContaierTickable;
import com.yudream.yudreamaddons.common.api.NetworkHubDataStorage;
import com.yudream.yudreamaddons.common.api.NetworkStatus;
import com.yudream.yudreamaddons.common.network.PacketServerToClient;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

import java.util.List;

import static com.yudream.yudreamaddons.common.network.PacketServerToClient.ServerToClient.UPDATE_NETWORKS;

public class EventHandler {
    @SubscribeEvent
    public void onHurt(LivingAttackEvent event) {
        if (!Configurations.GENERAL_CONFIG.doElectricUnground && event.getSource() instanceof ElectricDamageSource && !event.getEntity().onGround) {
            ElectricDamageSource dmg = (ElectricDamageSource) event.getSource();
            dmg.dmg = 0;
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent event) {
        if (event.side.isClient()) return;
        if (event.phase != Phase.START) return;
        if (!(event.player instanceof EntityPlayerMP)) return;
        Container container = event.player.openContainer;
        if (container instanceof IContaierTickable) {
            ((IContaierTickable) container).update();
        }
        if (event.player.world.getTotalWorldTime() % 20 != 0) return;
        World world = event.player.world;
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        NetworkHubDataStorage storage = NetworkHubDataStorage.get(world);
        List<NetworkStatus> networks = storage.getNeedUpdateNetworks(player.getGameProfile().getId());
        if (!networks.isEmpty()) {
            NBTTagCompound tag = new NBTTagCompound();
            NBTTagList list = new NBTTagList();
            for (NetworkStatus network : networks) {
                list.appendTag(network.writeToNBT(new NBTTagCompound()));
                network.setNeedTellClient(false);
            }
            tag.setTag("networks", list);
            YuDreamAddons.instance.networkWrapper.sendTo(new PacketServerToClient(UPDATE_NETWORKS, tag), player);
        }
    }
}

