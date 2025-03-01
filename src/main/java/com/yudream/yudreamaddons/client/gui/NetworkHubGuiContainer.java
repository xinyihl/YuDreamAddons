package com.yudream.yudreamaddons.client.gui;

import com.yudream.yudreamaddons.Tags;
import com.yudream.yudreamaddons.YuDreamAddons;
import com.yudream.yudreamaddons.common.api.NetworkStatus;
import com.yudream.yudreamaddons.common.container.NetworkHubContainer;
import com.yudream.yudreamaddons.common.network.PacketGuiAtion;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLockIconButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.yudream.yudreamaddons.common.api.GuiAtion.*;

@SideOnly(Side.CLIENT)
public class NetworkHubGuiContainer extends GuiContainer {
    private static final int SCROLL_BAR_WIDTH = 6;
    private static final int SCROLL_BAR_LEFT = 96;
    private final List<NetButton> networkButtons = new ArrayList<>();
    private final List<NetworkStatus> networks;
    private int scrollOffset;
    private int maxScroll;
    private int lastScrollOffset = -1;
    private int scrollBarHeight;
    private int scrollBarY;
    private GuiLockIconButton lockButton;
    private GuiButton createButton;
    private GuiButton deleteButton;
    private GuiButton connectButton;
    private GuiButton disConnectButton;
    private NetworkStatus selectedNetwork;
    private boolean isScrolling;

    public NetworkHubGuiContainer(NetworkHubContainer inventorySlotsIn) {
        super(inventorySlotsIn);
        this.networks = inventorySlotsIn.networkHub.getNetworks();
        this.xSize = 256;
        this.ySize = 166;
        selectedNetwork = networks.stream().filter(p -> p.getUuid().equals(((NetworkHubContainer) inventorySlots).networkHub.getNetworkUuid())).findFirst().orElse(new NetworkStatus(new UUID(0, 0), "null", true, 0, new BlockPos(0, 0, 0)));
    }

    @Override
    public void initGui() {
        super.initGui();
        networkButtons.clear();
        this.createButton = new GuiButton(995, guiLeft + 8, guiTop + 110, 70, 18, "创建");
        this.deleteButton = new GuiButton(996, guiLeft + 8, guiTop + 135, 70, 18, "删除");
        this.connectButton = new GuiButton(997, guiLeft + 85, guiTop + 110, 70, 18, "连接");
        this.disConnectButton = new GuiButton(998, guiLeft + 85, guiTop + 135, 70, 18, "断开");
        this.lockButton = new GuiLockIconButton(999, guiLeft + 200, guiTop + 18);

        if (((NetworkHubContainer) inventorySlots).networkHub.isConnected()) {
            createButton.enabled = false;
        }

        if (((NetworkHubContainer) inventorySlots).networkHub.isHead()) {
            createButton.enabled = false;
        }

        this.buttonList.add(this.createButton);
        this.buttonList.add(this.deleteButton);
        this.buttonList.add(this.connectButton);
        this.buttonList.add(this.disConnectButton);
        this.buttonList.add(this.lockButton);
        updateButtons();
    }

    private void updateButtons() {
        if (scrollOffset == lastScrollOffset) return;
        networkButtons.clear();
        int visibleRows = 4;
        int startIndex = scrollOffset / 20;
        for (int i = 0; i < visibleRows; i++) {
            int index = startIndex + i;
            if (index >= networks.size()) break;
            NetButton btn = new NetButton(index, guiLeft + 8, guiTop + 18 + i * 20 - scrollOffset % 20, 85, 18, networks.get(index).getNetworkName(), networks.get(index));
            networkButtons.add(btn);
        }
        lastScrollOffset = scrollOffset;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(new ResourceLocation(Tags.MOD_ID, "textures/gui/network_hub.png"));
        this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

        int listHeight = networks.size() * 20;
        int visibleHeight = 80;
        maxScroll = Math.max(0, listHeight - visibleHeight);

        updateButtons();
        ScissorHelper.enableScissor(mc, guiLeft + 8, guiTop + 15, 110, visibleHeight + 2);
        for (NetButton btn : networkButtons) {
            btn.drawButton(mc, mouseX, mouseY, partialTicks);
        }
        ScissorHelper.disableScissor();

        if (maxScroll > 0) {
            scrollBarHeight = (int) ((float) visibleHeight / listHeight * visibleHeight);
            scrollBarY = guiTop + 18 + (int) ((float) scrollOffset / maxScroll * (visibleHeight - scrollBarHeight));
            drawRect(guiLeft + SCROLL_BAR_LEFT, scrollBarY, guiLeft + SCROLL_BAR_LEFT + SCROLL_BAR_WIDTH, scrollBarY + scrollBarHeight, 0xFF404040);
        }

        lockButton.drawButton(mc, mouseX, mouseY, partialTicks);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRenderer.drawString("网络设置", 7, 5, 0xFF404040); // 100 15
        int rightPanelX = 110;
        int rightPanelY = 18;
        fontRenderer.drawString("名称: " + selectedNetwork.getNetworkName(), rightPanelX, rightPanelY, 0xFFFFFF);
        fontRenderer.drawString("剩余频道: ?" /*+ (64 - selectedNetwork.getTargetPos().size())*/, rightPanelX, rightPanelY + 15, 0xFFFFFF);
        fontRenderer.drawString("维度ID: " + selectedNetwork.getDimensionId(), rightPanelX, rightPanelY + 30, 0xFFFFFF);
        fontRenderer.drawString("是否公开: " + (selectedNetwork.isPublic() ? "是" : "否"), rightPanelX, rightPanelY + 45, 0xFFFFFF);
        fontRenderer.drawString("连接状态: " + (((NetworkHubContainer) inventorySlots).networkHub.isConnected() ? "已连接" : "未连接"), rightPanelX, rightPanelY + 60, 0xFFFFFF);
    }

    @Override
    protected void actionPerformed(@Nonnull GuiButton ba) {
        //System.out.println("点击了按钮：" + ba.displayString);
        if (ba instanceof NetButton) {
            NetButton button = (NetButton) ba;
            if (networkButtons.contains(button)) {
                selectedNetwork = button.networkStatus;
                this.lockButton.setLocked(!selectedNetwork.isPublic());
            }
        }
        if (createButton.id == ba.id) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString("name", "Button Create");
            YuDreamAddons.instance.networkWrapper.sendToServer(new PacketGuiAtion(CREATE_NETWORK, tag));
        }
        if (selectedNetwork.getUuid().equals(new UUID(0, 0))) {
            return;
        }
        if (disConnectButton.id == ba.id) {
            NBTTagCompound tag = new NBTTagCompound();
            YuDreamAddons.instance.networkWrapper.sendToServer(new PacketGuiAtion(DISCONNECT_NETWORK, tag));
        }
        if (connectButton.id == ba.id) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setUniqueId("networkUuid", selectedNetwork.getUuid());
            YuDreamAddons.instance.networkWrapper.sendToServer(new PacketGuiAtion(SET_NETWORK_UUID, tag));
        }
        if (lockButton.id == ba.id) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setBoolean("public", !lockButton.isLocked());
            tag.setUniqueId("networkUuid", selectedNetwork.getUuid());
            YuDreamAddons.instance.networkWrapper.sendToServer(new PacketGuiAtion(SET_NETWORK_PUBLIC, tag));
            this.lockButton.setLocked(!lockButton.isLocked());
        }
        if (deleteButton.id == ba.id) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setUniqueId("networkUuid", selectedNetwork.getUuid());
            YuDreamAddons.instance.networkWrapper.sendToServer(new PacketGuiAtion(DELETE_NETWORK, tag));
            selectedNetwork = new NetworkStatus(new UUID(0, 0), "null", true, 0, new BlockPos(0, 0, 0));
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int mouseWheel = Mouse.getEventDWheel();
        if (mouseWheel != 0) {
            scrollOffset = (int) MathHelper.clamp(scrollOffset - (Math.signum(mouseWheel) * 20), 0, maxScroll);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        for (NetButton btn : networkButtons) {
            if (btn.mousePressed(mc, mouseX, mouseY)) {
                btn.playPressSound(mc.getSoundHandler());
                actionPerformed(btn);
                return;
            }
        }
        if (mouseButton == 0 && isMouseOverScrollBar(mouseX, mouseY)) {
            isScrolling = true;
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if (isScrolling) {
            float normalized = (float) (mouseY - guiTop - 18) / 80;
            scrollOffset = (int) (normalized * maxScroll);
            scrollOffset = MathHelper.clamp(scrollOffset, 0, maxScroll);
        }
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        isScrolling = false;
        super.mouseReleased(mouseX, mouseY, state);
    }

    private boolean isMouseOverScrollBar(int mouseX, int mouseY) {
        return mouseX >= guiLeft + SCROLL_BAR_LEFT && mouseY >= scrollBarY && mouseX <= guiLeft + SCROLL_BAR_LEFT + SCROLL_BAR_WIDTH && mouseY <= scrollBarY + scrollBarHeight;
    }

    public static class NetButton extends GuiButton {
        public NetworkStatus networkStatus;

        public NetButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText, NetworkStatus networkStatus) {
            super(buttonId, x, y, widthIn, heightIn, buttonText);
            this.networkStatus = networkStatus;
        }
    }
}
