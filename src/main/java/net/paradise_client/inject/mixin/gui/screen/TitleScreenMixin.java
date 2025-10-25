package net.paradise_client.inject.mixin.gui.screen;

import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerWarningScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.*;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.realms.gui.screen.RealmsNotificationsScreen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.paradise_client.*;
import net.paradise_client.themes.*;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin class to customize the behavior of the Title Screen in Minecraft.
 * <p>
 * This class modifies the Title Screen with a custom layout.
 * </p>
 *
 * @author SpigotRCE
 * @since 2.9
 */
@SuppressWarnings("unused") @Mixin(TitleScreen.class) public abstract class TitleScreenMixin extends Screen {

  /**
   * The splash text renderer used to display splash texts on the Title Screen.
   */
  @Nullable @Shadow private SplashTextRenderer splashText;

  /**
   * The Realms Notifications Screen displayed on the Title Screen if active.
   */
  @Nullable @Shadow private RealmsNotificationsScreen realmsNotificationGui;

  /**
   * Alpha value for the background fade effect on the Title Screen.
   */
  @Mutable @Shadow private float backgroundAlpha;

  /**
   * Flag indicating whether the background fade effect is enabled.
   */
  @Mutable @Shadow private boolean doBackgroundFade;

  /**
   * The start time for the background fade effect, in milliseconds.
   */
  @Mutable @Shadow private long backgroundFadeStart;

  /**
   * The logo drawer used to render the logo on the Title Screen.
   */
  @Final @Shadow private LogoDrawer logoDrawer;

  @Unique private final MinecraftClient client = MinecraftClient.getInstance();

  @Unique private final Identifier logoImage = Identifier.of(Constants.MOD_ID, "textures/icon/icon.png");

  /**
   * Constructs a new instance of the TitleScreenMixin.
   *
   * @param title The title of the screen.
   */
  protected TitleScreenMixin(Text title) {
    super(title);
  }

  /**
   * Injects custom buttons with a clean layout
   */
  @Inject(method = "init", at = @At("TAIL")) private void initParadise(CallbackInfo ci) {
    this.clearChildren();

    int buttonWidth = 120;
    int buttonHeight = 20;
    int spacing = 8;

    int row1Count = 4;
    int row1Width = (buttonWidth * row1Count) + (spacing * (row1Count - 1));
    int row1X = (this.width - row1Width) / 2;
    int row1Y = this.height / 2 - 10;

    this.addDrawableChild(ButtonWidget.builder(Text.literal("Singleplayer"),
        (button) -> client.setScreen(new SelectWorldScreen(this)))
      .dimensions(row1X, row1Y, buttonWidth, buttonHeight)
      .build());

    this.addDrawableChild(ButtonWidget.builder(Text.literal("Multiplayer"), (button) -> {
      Screen screen =
        this.client.options.skipMultiplayerWarning ? new MultiplayerScreen(this) : new MultiplayerWarningScreen(this);
      client.setScreen(screen);
    }).dimensions(row1X + (buttonWidth + spacing), row1Y, buttonWidth, buttonHeight).build());

    this.addDrawableChild(ButtonWidget.builder(Text.literal("Realms"),
        (button) -> client.setScreen(new RealmsMainScreen(this)))
      .dimensions(row1X + (buttonWidth + spacing) * 2, row1Y, buttonWidth, buttonHeight)
      .build());

    this.addDrawableChild(ButtonWidget.builder(Text.literal("Options"),
        (button) -> client.setScreen(new OptionsScreen(this, client.options)))
      .dimensions(row1X + (buttonWidth + spacing) * 3, row1Y, buttonWidth, buttonHeight)
      .build());

    int row2Y = row1Y + buttonHeight + spacing + 4;
    int row2Count = ParadiseClient.MISC_MOD.isClientOutdated ? 3 : 2;
    int row2Width = (buttonWidth * row2Count) + (spacing * (row2Count - 1));
    int row2X = (this.width - row2Width) / 2;

    this.addDrawableChild(ButtonWidget.builder(Text.literal("Quit Game"), (button) -> client.scheduleStop())
      .dimensions(row2X, row2Y, buttonWidth, buttonHeight)
      .build());

    Theme currentTheme = ThemeManager.getTheme();
    this.addDrawableChild(ButtonWidget.builder(Text.literal("Theme: " + currentTheme.getName()), (button) -> {
      Theme[] themes = Theme.values();
      int nextOrdinal = (ThemeManager.getTheme().ordinal() + 1) % themes.length;
      Theme nextTheme = themes[nextOrdinal];
      ThemeManager.setTheme(nextTheme);
      client.setScreen(new TitleScreen());
    }).dimensions(row2X + (buttonWidth + spacing), row2Y, buttonWidth, buttonHeight).build());

    if (ParadiseClient.MISC_MOD.isClientOutdated) {
      this.addDrawableChild(ButtonWidget.builder(Text.literal("Update"), (button) -> {
        Util.getOperatingSystem().open("https://paradise-client.net/downloads");
        client.setScreen(new TitleScreen());
      }).dimensions(row2X + (buttonWidth + spacing) * 2, row2Y, buttonWidth, buttonHeight).build());
    }
  }

  /**
   * Renders the custom Paradise title screen
   */
  @Inject(method = "render", at = @At("HEAD"), cancellable = true) private void renderParadise(DrawContext context,
    int mouseX,
    int mouseY,
    float delta,
    CallbackInfo ci) {
    if (this.backgroundFadeStart == 0L && this.doBackgroundFade) {
      this.backgroundFadeStart = Util.getMeasuringTimeMs();
    }

    float f = 1.0F;
    if (this.doBackgroundFade) {
      float g = (float) (Util.getMeasuringTimeMs() - this.backgroundFadeStart) / 2000.0F;
      if (g > 1.0F) {
        this.doBackgroundFade = false;
        this.backgroundAlpha = 1.0F;
      } else {
        g = MathHelper.clamp(g, 0.0F, 1.0F);
        f = MathHelper.clampedMap(g, 0.5F, 1.0F, 0.0F, 1.0F);
        this.backgroundAlpha = MathHelper.clampedMap(g, 0.0F, 0.5F, 0.0F, 1.0F);
      }
      this.setWidgetAlpha(f);
    }

    this.renderPanoramaBackground(context, delta);

    int logoWidth = 128;
    int logoHeight = 128;
    int logoX = (this.width - logoWidth) / 2;
    int logoY = this.height / 2 - 150;
    context.drawTexture(RenderLayer::getGuiTextured,
      logoImage,
      logoX,
      logoY,
      0.0F,
      0.0F,
      logoWidth,
      logoHeight,
      logoWidth,
      logoHeight);

    super.render(context, mouseX, mouseY, delta);

    TextRenderer font = this.textRenderer;
    context.drawTextWithShadow(font, Constants.windowTitle, 2, this.height - 10, 0xFFFFFF);

    String copyright = "Copyright Mojang AB. Do not distribute!";
    int copyW = font.getWidth(copyright);
    context.drawTextWithShadow(font, copyright, this.width - copyW - 2, this.height - 10, 0xFFFFFF);

    if (this.isRealmsNotificationsGuiDisplayed() && f >= 1.0F) {
      this.realmsNotificationGui.render(context, mouseX, mouseY, delta);
    }

    ci.cancel();
  }

  /**
   * Sets the alpha value for widgets. This method is shadowed from the original TitleScreen class.
   *
   * @param alpha The alpha value to set.
   */
  @Shadow private void setWidgetAlpha(float alpha) {
  }

  /**
   * Checks if the Realms Notifications GUI is displayed. This method is shadowed from the original TitleScreen class.
   *
   * @return True if the Realms Notifications GUI is displayed, false otherwise.
   */
  @Shadow private boolean isRealmsNotificationsGuiDisplayed() {
    return false;
  }
}