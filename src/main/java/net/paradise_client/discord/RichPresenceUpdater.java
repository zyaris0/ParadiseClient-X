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

    public RichPresenceUpdater() {
        richPresence = new DiscordRichPresence.Builder("")
                .setBigImage(
                        "af9df3fa19b7374e5e7582865f9fb1e7",
                        "ParadiseClient"
                )
                .setDetails("Launching Client...")
                .setStartTimestamps(System.currentTimeMillis())
                .build();
    }

    public void updatePresence(String playerName, String state) {
        richPresence.details = "Playing as " + playerName;
        richPresence.state = state;
        DiscordRPC.discordUpdatePresence(richPresence);
    }

    public DiscordRichPresence getRichPresence() {
        return richPresence;
    }
}
