package com.yudream.yudreamaddons.client.gui;

import com.yudream.yudreamaddons.Tags;
import com.yudream.yudreamaddons.YuDreamAddons;
import com.yudream.yudreamaddons.common.api.NetworkStatus;
import com.yudream.yudreamaddons.common.container.NetworkHubContainer;
import com.yudream.yudreamaddons.common.network.PacketClientToServer;
import mezz.jei.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLockIconButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.yudream.yudreamaddons.common.network.PacketClientToServer.ClientToServer.BUTTON_ACTION;

@SideOnly(Side.CLIENT)
public class NetworkHubGuiContainer extends GuiContainer {
    private static final int SCROLL_BAR_WIDTH = 6;
    private static final int SCROLL_BAR_LEFT = 96;
    private final List<NetButton> networkButtons = new ArrayList<>();
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
    private boolean isScrolling;
    private final NetworkHubContainer networkHubContainer;
    private int oldNetworksHash;

    private GuiTextField textField;
    private boolean isCreating = false;

    public NetworkHubGuiContainer(NetworkHubContainer networkHubContainer) {
        super(networkHubContainer);
        this.xSize = 200;
        this.ySize = 166;
        this.networkHubContainer = networkHubContainer;
    }

    private boolean closeJei = false;

    private NetworkStatus showInfo() {
        return networkHubContainer.networks.getOrDefault(networkHubContainer.selectedNetwork, new NetworkStatus(new UUID(0, 0), "Unknown", true, 0, new BlockPos(0, 0, 0)));
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        if(closeJei){
            Config.toggleOverlayEnabled();
        }
    }

    @Override
    public void initGui() {
        super.initGui();

        if(Config.isOverlayEnabled()){
            Config.toggleOverlayEnabled();
            closeJei = true;
        }

        this.createButton = new GuiButton(995, guiLeft + 26, guiTop + 110, 70, 18, "创建");
        this.deleteButton = new GuiButton(996, guiLeft + 26, guiTop + 135, 70, 18, "删除");
        this.connectButton = new GuiButton(997, guiLeft + 103, guiTop + 110, 70, 18, "连接");
        this.disConnectButton = new GuiButton(998, guiLeft + 103, guiTop + 135, 70, 18, "断开");
        this.lockButton = new GuiLockIconButton(999, guiLeft + 201, guiTop + 12) {
            @Override
            public void drawButton(@Nonnull Minecraft mc, int mouseX, int mouseY, float partialTicks) {
                super.drawButton(mc, mouseX, mouseY, partialTicks);
                if (this.visible) {
                    int relx = mouseX - this.x, rely = mouseY - this.y;
                    if (relx >= 0 && rely >= 0 && relx < this.width && rely < this.height) {
                        mc.fontRenderer.drawString("切换网络是否公开", mouseX + 5, mouseY + 5, 0xFFFFFF);
                    }
                }
            }
        };
        this.textField = new GuiTextField(1000, this.fontRenderer, guiLeft + 26, guiTop + 110, 70, 18);

        if (this.networkHubContainer.networkHub.isConnected()) {
            this.createButton.enabled = false;
        }

        if (this.networkHubContainer.networkHub.isHead()) {
            this.createButton.enabled = false;
            this.connectButton.enabled = false;
            this.disConnectButton.enabled = false;
        }

        this.lockButton.setLocked(!this.showInfo().isPublic());
        this.textField.setVisible(false);
        this.textField.setMaxStringLength(10);

        this.buttonList.add(this.createButton);
        this.buttonList.add(this.deleteButton);
        this.buttonList.add(this.connectButton);
        this.buttonList.add(this.disConnectButton);
        this.buttonList.add(this.lockButton);

        updateNetworksButtons();
    }

    private boolean isButUpdate() {
        int newNetworksHash = this.networkHubContainer.networks.hashCode();
        if (this.oldNetworksHash != newNetworksHash) {
            this.oldNetworksHash = newNetworksHash;
            return true;
        }
        return false;
    }

    private void updateNetworksButtons() {
        if (!this.isButUpdate() && scrollOffset == lastScrollOffset) return;
        this.networkButtons.clear();
        List<NetworkStatus> networks = new ArrayList<>(networkHubContainer.networks.values());
        for (int i = 0; i < 4; i++) {
            int index = scrollOffset / 20 + i;
            if (index >= networks.size()) break;
            NetButton btn = new NetButton(index, guiLeft + 8, guiTop + 18 + i * 20 - scrollOffset % 20, 85, 18, networks.get(index).getNetworkName(), networks.get(index));
            this.networkButtons.add(btn);
        }
        this.lastScrollOffset = scrollOffset;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        this.mc.getTextureManager().bindTexture(new ResourceLocation(Tags.MOD_ID, "textures/gui/network_hub.png"));
        this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();

        boolean isHead = this.networkHubContainer.networkHub.isHead();
        this.lockButton.setLocked(!showInfo().isPublic());
        this.createButton.enabled = !isHead && this.networkHubContainer.networkHub.isConnected();
        this.disConnectButton.enabled = !isHead;
        this.connectButton.enabled = !isHead;


        int listHeight = this.networkHubContainer.networks.size() * 20;
        int visibleHeight = 80;
        this.maxScroll = Math.max(0, listHeight - visibleHeight);

        updateNetworksButtons();

        ScissorHelper.enableScissor(mc, guiLeft + 8, guiTop + 15, 110, visibleHeight + 2);
        for (NetButton btn : networkButtons) {
            btn.drawButton(mc, mouseX, mouseY, partialTicks);
        }
        ScissorHelper.disableScissor();

        if (maxScroll > 0) {
            this.scrollBarHeight = (int) ((float) visibleHeight / listHeight * visibleHeight);
            this.scrollBarY = guiTop + 17 + (int) ((float) scrollOffset / maxScroll * (visibleHeight - scrollBarHeight));
            drawRect(guiLeft + SCROLL_BAR_LEFT, scrollBarY, guiLeft + SCROLL_BAR_LEFT + SCROLL_BAR_WIDTH, scrollBarY + scrollBarHeight, 0xFF404040);
        }

        if (isCreating) {
            this.textField.drawTextBox();
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        int rightPanelX = 110;
        int rightPanelY = 19;
        this.fontRenderer.drawString("无线连接器", 7, 5, 0xFF404040);
        this.fontRenderer.drawString("名称: " + this.showInfo().getNetworkName(), rightPanelX, rightPanelY, 0xFFFFFF);
        this.fontRenderer.drawString("剩余频道: " + this.showInfo().getSurplusChannels(), rightPanelX, rightPanelY + 15, 0xFFFFFF);
        this.fontRenderer.drawString("维度ID: " + this.showInfo().getDimensionId(), rightPanelX, rightPanelY + 30, 0xFFFFFF);
        this.fontRenderer.drawString("是否公开: " + (this.showInfo().isPublic() ? "是" : "否"), rightPanelX, rightPanelY + 45, 0xFFFFFF);
        this.fontRenderer.drawString("连接状态: " + (networkHubContainer.networkHub.isConnected() ? "已连接" : "未连接"), rightPanelX, rightPanelY + 60, 0xFFFFFF);
    }

    @Override
    protected void actionPerformed(@Nonnull GuiButton ba) {
        if (createButton.id == ba.id) {
            this.createButton.enabled = false;
            this.createButton.visible = false;
            this.isCreating = true;
            this.textField.setVisible(true);
            this.textField.setFocused(true);
            return;
        }
        if (ba instanceof NetButton) {
            NetButton button = (NetButton) ba;
            if (networkButtons.contains(button)) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setInteger("button", 0);
                tag.setUniqueId("networkUuid", button.networkStatus.getUuid());
                YuDreamAddons.instance.networkWrapper.sendToServer(new PacketClientToServer(BUTTON_ACTION, tag));
                return;
            }
        }
        if (networkHubContainer.selectedNetwork.equals(new UUID(0, 0))) {
            return;
        }

        if (ba.id >= 900 && ba.id <= 999) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("button", ba.id);
            YuDreamAddons.instance.networkWrapper.sendToServer(new PacketClientToServer(BUTTON_ACTION, tag));
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (isCreating) {
            this.textField.textboxKeyTyped(typedChar, keyCode);
            if (keyCode == Keyboard.KEY_RETURN || keyCode == Keyboard.KEY_NUMPADENTER) {
                this.connectButton.enabled = false;
                this.disConnectButton.enabled = false;
                this.createButton.visible = true;
                this.isCreating = false;
                this.textField.setVisible(false);

                NBTTagCompound tag = new NBTTagCompound();
                tag.setInteger("button", 1);
                tag.setString("name", textField.getText());
                YuDreamAddons.instance.networkWrapper.sendToServer(new PacketClientToServer(BUTTON_ACTION, tag));
                this.textField.setText("");
            }
        } else {
            super.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int mouseWheel = Mouse.getEventDWheel();
        if (mouseWheel != 0) {
            this.scrollOffset = (int) MathHelper.clamp(scrollOffset - (Math.signum(mouseWheel) * 20), 0, maxScroll);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (isCreating && !isMouseOverTextField(this.textField, mouseX, mouseY)) {
            this.createButton.enabled = true;
            this.createButton.visible = true;
            this.isCreating = false;
            this.textField.setVisible(false);
            return;
        }
        for (NetButton btn : networkButtons) {
            if (btn.mousePressed(mc, mouseX, mouseY)) {
                btn.playPressSound(mc.getSoundHandler());
                this.actionPerformed(btn);
            }
        }
        if (mouseButton == 0 && isMouseOverScrollBar(mouseX, mouseY)) {
            this.isScrolling = true;
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if (this.isScrolling) {
            float normalized = (float) (mouseY - guiTop - 17) / 80;
            this.scrollOffset = (int) (normalized * maxScroll);
            this.scrollOffset = MathHelper.clamp(scrollOffset, 0, maxScroll);
        }
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        this.isScrolling = false;
        super.mouseReleased(mouseX, mouseY, state);
    }

    private boolean isMouseOverTextField(GuiTextField textField, int mouseX, int mouseY) {
        return mouseX >= textField.x && mouseX < textField.x + textField.width && mouseY >= textField.y && mouseY < textField.y + textField.height;
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
