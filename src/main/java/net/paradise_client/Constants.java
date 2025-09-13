package net.paradise_client;

import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.*;

public final class Constants {
  public static final String MOD_ID = "paradiseclient";

  public static final String MOD_NAME = "ParadiseClient";

  public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

  public static final String VERSION = FabricLoader.getInstance()
    .getModContainer(MOD_ID)
    .orElseThrow(() -> new IllegalStateException("Mod container not found for " + MOD_ID))
    .getMetadata()
    .getVersion()
    .getFriendlyString();
  private static String windowTitle = buildWindowTitle();

  public static String getWindowTitle() {
    return windowTitle;
  }

  public static void reloadTitle() {
    windowTitle = buildWindowTitle();
    ParadiseClient.MINECRAFT_CLIENT.updateWindowTitle();
  }

  private static String buildWindowTitle() {
    return MOD_NAME + " " + VERSION;
  }
}
