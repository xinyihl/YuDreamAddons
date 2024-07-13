package com.yudream.yudreamaddons.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.Objects;

public class ClientEvents {
    public static final KeyBinding KEY_PLACE = new KeyBinding("key.yudreamaddons.getitemId", Keyboard.KEY_K, "key.yudreamaddons.desc");

    public ClientEvents() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static void setSysClipboardText(String writeMe) {
        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable tText = new StringSelection(writeMe);
        clip.setContents(tText, null);
    }

    public void registerKeyBindings() {
        ClientRegistry.registerKeyBinding(ClientEvents.KEY_PLACE);
    }

    @SubscribeEvent
    public void onGuiKeyboardEvent(GuiScreenEvent.KeyboardInputEvent.Pre event) {
        if (handleKeyEvent(event)) {
            event.setCanceled(true);
        }
    }

    private boolean handleKeyEvent(GuiScreenEvent.KeyboardInputEvent.Pre event) {
        char typedChar = Keyboard.getEventCharacter();
        int eventKey = Keyboard.getEventKey();

        return ((eventKey == 0 && typedChar >= 32) || Keyboard.getEventKeyState()) &&
                handleKeyDown(eventKey, event);
    }

    private boolean handleKeyDown(int eventKey, GuiScreenEvent.KeyboardInputEvent.Pre event) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.player;
        GuiScreen gui = event.getGui();
        if (eventKey == KEY_PLACE.getKeyCode()) {
            if (player != null && gui instanceof GuiContainer) {
                GuiContainer guiContainer = (GuiContainer) gui;
                ItemStack hoveredStack = guiContainer.getSlotUnderMouse() != null ? guiContainer.getSlotUnderMouse().getStack() : ItemStack.EMPTY;
                if (!hoveredStack.isEmpty()) {
                    int meta = hoveredStack.getMetadata();
                    String text = "<" + Objects.requireNonNull(hoveredStack.getItem().getRegistryName()) + (meta == 0 ? "" : ":" + meta) + ">";
                    if (hoveredStack.serializeNBT().hasKey("tag")) {
                        String nbt = hoveredStack.serializeNBT().getTag("tag").toString();
                        if (nbt.length() > 0)
                            text += ".withTag(" + nbt + ")";
                    }
                    player.sendMessage(new TextComponentString("Item: " + text));
                    setSysClipboardText(text);
                }
                //player.sendMessage(new TextComponentString("Current GUI: " + guiContainer.getClass().getSimpleName()));
            }
        }
        return false;
    }
}
