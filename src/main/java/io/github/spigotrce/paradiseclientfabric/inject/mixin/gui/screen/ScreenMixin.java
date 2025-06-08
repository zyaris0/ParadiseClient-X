package io.github.spigotrce.paradiseclientfabric.inject.mixin.gui.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin for the Screen class to customize background rendering.
 * This mixin replaces the default background for specific screens with a custom texture.
 *
 * @author SpigotRCE
 * @since 1.9
 */
@Mixin(Screen.class)
public abstract class ScreenMixin {
    @Shadow
    protected MinecraftClient client;

    @Shadow protected abstract void renderPanoramaBackground(DrawContext context, float deltaTicks);

    /**
     * Injects custom background rendering into the renderBackground method.
     * This method draws a custom texture for specific screens and cancels the original rendering.
     *
     * @param context The draw context used for rendering.
     * @param mouseX  The X coordinate of the mouse.
     * @param mouseY  The Y coordinate of the mouse.
     * @param delta   The time delta since the last frame.
     * @param ci      The callback information for the method.
     */
    @Inject(method = "renderBackground", at = @At(value = "HEAD"), cancellable = true)
    private void renderBackground(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (this.client.world == null) {
            this.renderPanoramaBackground(context, delta);
        }
        ci.cancel();
    }
}
