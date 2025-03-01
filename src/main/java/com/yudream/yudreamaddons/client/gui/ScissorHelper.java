package com.yudream.yudreamaddons.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;

public class ScissorHelper {
    public static void enableScissor(Minecraft mc, int x, int y, int width, int height) {
        ScaledResolution res = new ScaledResolution(mc);
        int scaleFactor = res.getScaleFactor();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(
                x * scaleFactor,
                mc.displayHeight - (y + height) * scaleFactor,
                width * scaleFactor,
                height * scaleFactor);
    }

    public static void disableScissor() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }
}
