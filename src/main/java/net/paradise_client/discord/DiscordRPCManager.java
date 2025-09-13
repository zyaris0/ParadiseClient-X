package net.paradise_client.discord;

import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordUser;
import net.arikia.dev.drpc.callbacks.ReadyCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.paradise_client.ParadiseClient;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Manages the Discord Rich Presence integration for the ParadiseClient mod.
 * <p>
 * This class initializes the Discord RPC, updates the rich presence status based on the game state,
 * and handles enabling/disabling the integration.
 * </p>
 *
 * @author 1nstagram
 * @since 1.10
 */
public class DiscordRPCManager implements ReadyCallback {
    private final MinecraftClient client;
    private final RichPresenceUpdater richPresenceUpdater;
    private Screen lastScreen;
    private boolean enabled = true;

    public DiscordRPCManager(
            MinecraftClient client
    ) {
        this.client = client;
        this.richPresenceUpdater = new RichPresenceUpdater();
        this.lastScreen = null;
        init();
        startTask();
    }

    @Override
    public void apply(DiscordUser discordUser) {
        System.out.println("DiscordRPC Launched for user: " + discordUser.username);
    }

    private void init() {
        DiscordEventHandlers handlers = new DiscordEventHandlers.Builder()
                .setReadyEventHandler(
                        user -> System.out.printf(
                                "Connected to Discord: %s#%s (%s)%n",
                                user.username,
                                user.discriminator,
                                user.userId
                        )
                )
                .build();
        try {
            DiscordRPC.discordInitialize("1164104022265974784", handlers, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startTask() {
        Executors.newSingleThreadScheduledExecutor()
                .scheduleWithFixedDelay(
                        () -> {
                            String playerName = ParadiseClient.BUNGEE_SPOOF_MOD.usernameFake;
                            String state = GameState.getGameState(client);
                            richPresenceUpdater.updatePresence(
                                    playerName,
                                    state
                            );
                        },
                        10,
                        10,
                        TimeUnit.SECONDS
                );
    }

    public void shutdown() {
        DiscordRPC.discordShutdown();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled == enabled) return;

        this.enabled = enabled;
        if (enabled) {
            init();
            startTask();
        } else {
            shutdown();
        }
    }
}
