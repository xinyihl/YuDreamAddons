package com.yudream.yudreamaddons.common.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.GuiScreenEvent.KeyboardInputEvent.Pre;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ClientEventHandler {
    public static final KeyBinding KEY_GET_ITEM_ID = new KeyBinding("key.yudreamaddons.getItemId", Keyboard.KEY_K, "key.yudreamaddons.desc");
    public static final KeyBinding KEY_GET_ITEM_ID_LIST = new KeyBinding("key.yudreamaddons.getItemIdList", Keyboard.KEY_L, "key.yudreamaddons.desc");
    private static final List<String> itemList = new ArrayList<>();

    public ClientEventHandler() {
        ClientRegistry.registerKeyBinding(ClientEventHandler.KEY_GET_ITEM_ID);
        ClientRegistry.registerKeyBinding(ClientEventHandler.KEY_GET_ITEM_ID_LIST);
    }

    public static void setSysClipboardText(String writeMe) {
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(writeMe), null);
    }

    @SubscribeEvent
    public void onGuiKeyboardEvent(Pre event) {
        if (handleKeyEvent(event)) {
            event.setCanceled(true);
        }
    }

    private boolean handleKeyEvent(Pre event) {
        char typedChar = Keyboard.getEventCharacter();
        int eventKey = Keyboard.getEventKey();
        return eventKey != 0 && typedChar >= 32 && Keyboard.getEventKeyState() && handleKeyDown(event, eventKey);
    }

    private boolean handleKeyDown(Pre event, int eventKey) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.player;
        GuiScreen gui = event.getGui();
        if (eventKey == KEY_GET_ITEM_ID.getKeyCode()) {
            if (player != null && gui instanceof GuiContainer) {
                GuiContainer guiContainer = (GuiContainer) gui;
                ItemStack hoveredStack = guiContainer.getSlotUnderMouse() != null ? guiContainer.getSlotUnderMouse().getStack() : ItemStack.EMPTY;
                if (!hoveredStack.isEmpty()) {
                    String item = getItemId(hoveredStack);
                    player.sendMessage(new TextComponentString("Item: " + item));
                    setSysClipboardText(item);
                    return true;
                }
                //player.sendMessage(new TextComponentString("Current GUI: " + guiContainer.getClass().getSimpleName()));
            }
        }
        if (eventKey == KEY_GET_ITEM_ID_LIST.getKeyCode()) {
            if (player != null && gui instanceof GuiContainer) {
                GuiContainer guiContainer = (GuiContainer) gui;
                ItemStack hoveredStack = guiContainer.getSlotUnderMouse() != null ? guiContainer.getSlotUnderMouse().getStack() : ItemStack.EMPTY;
                if (!hoveredStack.isEmpty()) {
                    String item = getItemId(hoveredStack);
                    itemList.add(item);
                    player.sendMessage(new TextComponentString("ItemList Add: " + item));
                    return true;
                } else {
                    player.sendMessage(new TextComponentString("ItemList: " + itemList));
                    setSysClipboardText(itemList.toString());
                    itemList.clear();
                }
            }
        }
        return false;
    }

    private String getItemId(ItemStack itemStack) {
        int meta = itemStack.getMetadata();
        String text = "<" + Objects.requireNonNull(itemStack.getItem().getRegistryName()) + (meta == 0 ? "" : ":" + meta) + ">";
        if (itemStack.serializeNBT().hasKey("tag")) {
            String nbt = itemStack.serializeNBT().getTag("tag").toString();
            if (!nbt.isEmpty())
                text += ".withTag(" + nbt + ")";
        }
        return text;
    }
}
