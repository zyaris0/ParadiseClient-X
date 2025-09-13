package net.paradise_client.discord;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.paradise_client.ParadiseClient;

/**
 * Utility class to determine the current game state for Discord Rich Presence updates.
 * <p>
 * This class provides a method to get a string representation of the current game state
 * based on the Minecraft client's current screen and connection status.
 * </p>
 *
 * @author 1nstagram
 * @since 1.10
 */
public class GameState {
    private static final RichPresenceUpdater richPresenceUpdater = new RichPresenceUpdater();

    public static String getGameState(MinecraftClient client) {
        if (client.currentScreen != null) {
            if (client.currentScreen instanceof TitleScreen) return "In Main Menu";
            if (client.currentScreen instanceof MultiplayerScreen) return "Browsing Multiplayer Servers";
            if (client.currentScreen instanceof SelectWorldScreen) return "Browsing Singleplayer Worlds";
            if (client.currentScreen instanceof GameMenuScreen)
                return client.getCurrentServerEntry() != null ? "Paused in Multiplayer" : "Paused in Singleplayer";
            if (client.currentScreen instanceof DisconnectedScreen) return "Disconnected from Server";
            if (client.currentScreen instanceof ConnectScreen) return "Connecting to Server...";

            if (ParadiseClient.HUD_MOD.isConnectedToServer(client)) {
                return ParadiseClient.HUD_MOD.getServerStatus(client);
            }

            return richPresenceUpdater.getRichPresence().state;
        }

        if (ParadiseClient.HUD_MOD.isConnectedToServer(client)) {
            return ParadiseClient.HUD_MOD.getServerStatus(client);
        }

        return client.isInSingleplayer() ? "Singleplayer Mode" : "Unknown State...";
    }
}