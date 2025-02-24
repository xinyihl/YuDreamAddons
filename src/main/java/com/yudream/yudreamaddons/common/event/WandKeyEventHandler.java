package com.yudream.yudreamaddons.common.event;

import com.yudream.yudreamaddons.YuDreamAddons;
import com.yudream.yudreamaddons.common.network.PacketWandOops;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;
import portablejim.bbw.core.items.IWandItem;
import portablejim.bbw.shims.BasicPlayerShim;

public class WandKeyEventHandler {

    public static final KeyBinding KEY_OOPS = new KeyBinding("bbw.key.oops", KeyConflictContext.IN_GAME, KeyModifier.CONTROL, Keyboard.KEY_Z, "bbw.key.category");

    public WandKeyEventHandler() {
        ClientRegistry.registerKeyBinding(KEY_OOPS);
    }

    @SubscribeEvent
    public void KeyEvent(InputEvent event) {
        if (KEY_OOPS.isPressed()) {
            ItemStack currentItemstack = BasicPlayerShim.getHeldWandIfAny(Minecraft.getMinecraft().player);
            if (currentItemstack != null && currentItemstack.getItem() instanceof IWandItem) {
                PacketWandOops packet = new PacketWandOops();
                YuDreamAddons.instance.networkWrapper.sendToServer(packet);
            }
        }
    }
}
