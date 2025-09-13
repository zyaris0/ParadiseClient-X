package net.paradise_client.discord;

import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;

/**
 * Updates the Discord Rich Presence status for the ParadiseClient mod.
 *
 * <p>
 * This class manages the Discord Rich Presence integration, allowing the mod to display
 * the player's current status and activity in Discord.
 * </p>
 *
 * @author 1nstagram
 */
public class RichPresenceUpdater {
    private final DiscordRichPresence richPresence;
    private long gameStartTime;
    private boolean timestampActive = false;

    public RichPresenceUpdater() {
        richPresence = new DiscordRichPresence.Builder("")
                .setBigImage(
                        "af9df3fa19b7374e5e7582865f9fb1e7",
                        "ParadiseClient"
                )
                .setDetails("Launching Client...")
                .setStartTimestamps(System.currentTimeMillis())
                .build();

        gameStartTime = System.currentTimeMillis();
    }

    /**
     * Enhanced method with detailed game state information
     *
     * @param playerName The player's name
     * @param detailedState The detailed game state description
     * @param isInGame Whether the player is actively in game
     * @param isPaused Whether the game is paused
     * @param currentState The GameState enum for additional logic
     */
    public void updatePresence(String playerName, String detailedState, boolean isInGame, boolean isPaused, GameState.State currentState) {
        // Update details based on game state
        if (playerName != null && !playerName.isEmpty()) {
            if (isInGame) {
                richPresence.details = "Playing as " + playerName;
            } else {
                richPresence.details = "Logged in as " + playerName;
            }
        } else {
            richPresence.details = "ParadiseClient";
        }

        // Set the state
        richPresence.state = detailedState;

        // Handle timestamps for active gameplay
        if (isInGame && !isPaused) {
            if (!timestampActive) {
                // Start new timestamp when entering active gameplay
                gameStartTime = System.currentTimeMillis();
                richPresence.startTimestamp = gameStartTime;
                timestampActive = true;
            }
            // Keep existing timestamp if already active
            richPresence.startTimestamp = gameStartTime;
        } else {
            // Clear timestamp when not actively playing
            richPresence.startTimestamp = 0;
            timestampActive = false;
        }

        // Add pause indicator if paused
        if (isPaused && currentState != null) {
            switch (currentState) {
                case PAUSED_MULTIPLAYER, PAUSED_SINGLEPLAYER:
                    richPresence.state = "⏸️ " + detailedState;
                    break;
              default:
                    break;
            }
        }

        // Add connection status indicators
        if (currentState != null) {
            switch (currentState) {
                case CONNECTING:
                    richPresence.state = "🔄 " + detailedState;
                    break;
                case DISCONNECTED:
                    richPresence.state = "❌ " + detailedState;
                    break;
                default:
                    break;
            }
        }

        DiscordRPC.discordUpdatePresence(richPresence);
    }
}