package net.paradise_client;

import com.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL11;

public class GLHelper {
    public static void enableDepthTest() {
        RenderSystem.assertOnRenderThread();
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    public static void disableDepthTest() {
        RenderSystem.assertOnRenderThread();
        GL11.glDisable(GL11.GL_DEPTH_TEST);
    }
}