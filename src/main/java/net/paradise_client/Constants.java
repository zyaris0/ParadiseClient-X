/**
 * This class contains various constants used throughout the ParadiseClient mod.
 *
 * @author SpigotRCE
 * @version 2.22
 * @since 1.0
 */
package net.paradise_client;

import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Constants {
    /**
     * The ID of the mod.
     */
    public static final String MOD_ID = "paradiseclient";

    /**
     * The version of the mod.
     */
    // todo: move this to metainf's implementation version?
    public static final String VERSION = FabricLoader.getInstance().getModContainer(MOD_ID)
            .orElseThrow(() -> new IllegalStateException("Mod container not found for " + MOD_ID))
            .getMetadata().getVersion().getFriendlyString();

    /**
     * The name of the mod.
     */
    public static final String MOD_NAME = "ParadiseClient";

    /**
     * The logger for the mod.
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    /**
     * The edition of the mod.
     */
    public static String EDITION = "PUBLIC"; // For API

    /**
     * Window title
     * Not final because MiscMod#isClientOutdated is dynamic
     */
    public static String WINDOW_TITLE = MOD_NAME + " [" + Constants.EDITION + "] " + Constants.VERSION;

    public static void reloadTitle() {
        ParadiseClient.MINECRAFT_CLIENT.updateWindowTitle();
    }
}
