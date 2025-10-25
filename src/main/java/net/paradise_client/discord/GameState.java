package net.paradise_client.discord;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.screen.multiplayer.*;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.paradise_client.ParadiseClient;

/**
 * Utility class to determine the current game state for Discord Rich Presence updates.
 * <p>
 * This class provides a method to get a string representation of the current game state based on the Minecraft client's
 * current screen and connection status.
 * </p>
 *
 * @author 1nstagram
 */
public class GameState {
<<<<<<< Updated upstream
  /**
   * Gets detailed game state information including server details if available.
   *
   * @param client The MinecraftClient instance
   *
   * @return Detailed state string with server information when applicable
   */
  public static String getDetailedGameState(MinecraftClient client) {
    State state = getGameStateEnum(client);

    switch (state) {
      case IN_GAME_MULTIPLAYER:
      case PAUSED_MULTIPLAYER:
        if (ParadiseClient.HUD_MOD.isConnectedToServer(client)) {
          String serverStatus = ParadiseClient.HUD_MOD.getServerStatus(client);
          return serverStatus != null ? serverStatus : state.getDisplayName();
        }
        break;
      case IN_GAME_SINGLEPLAYER:
      case PAUSED_SINGLEPLAYER:
        if (client.world != null && client.world.getRegistryKey() != null) {
          String worldName = client.world.getRegistryKey().getValue().toString();
          return state.getDisplayName() + " - " + worldName;
        }
        break;
      default:
        break;
=======
    enum State {
        MAIN_MENU("In Main Menu"),
        MULTIPLAYER("Browsing Multiplayer Servers"),
        SINGLEPLAYER("Browsing Singleplayer Worlds"),
        PAUSED_MULTIPLAYER("Paused in Multiplayer"),
        PAUSED_SINGLEPLAYER("Paused in Singleplayer"),
        DISCONNECTED("Disconnected from Server"),
        CONNECTING("Connecting to Server..."),
        IN_GAME_MULTIPLAYER("Playing Multiplayer"),
        IN_GAME_SINGLEPLAYER("Singleplayer Mode"),
        UNKNOWN("Unknown State...");

        private final String displayName;

        State(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * Determines the current game state and returns the corresponding enum.
     *
     * @param client The MinecraftClient instance
     * @return The State enum representing the current game state
     */
    public static State getGameStateEnum(MinecraftClient client) {
        if (client.currentScreen != null) {
            if (client.currentScreen instanceof TitleScreen) {
                return State.MAIN_MENU;
            }
            if (client.currentScreen instanceof MultiplayerScreen) {
                return State.MULTIPLAYER;
            }
            if (client.currentScreen instanceof SelectWorldScreen) {
                return State.SINGLEPLAYER;
            }
            if (client.currentScreen instanceof GameMenuScreen) {
                return client.getCurrentServerEntry() != null ?
                        State.PAUSED_MULTIPLAYER : State.PAUSED_SINGLEPLAYER;
            }
            if (client.currentScreen instanceof DisconnectedScreen) {
                return State.DISCONNECTED;
            }
            if (client.currentScreen instanceof ConnectScreen) {
                return State.CONNECTING;
            }

            // If we have a screen but it's not one of the above, check if we're in-game
            if (ParadiseClient.HUD_MOD.isConnectedToServer(client)) {
                return State.IN_GAME_MULTIPLAYER;
            }

            return State.UNKNOWN;
        }

        // No screen means we're likely in-game
        if (ParadiseClient.HUD_MOD.isConnectedToServer(client)) {
            return State.IN_GAME_MULTIPLAYER;
        }

        if (client.isInSingleplayer()) {
            return State.IN_GAME_SINGLEPLAYER;
        }

        return State.UNKNOWN;
    }

    /**
     * Gets detailed game state information including server details if available.
     *
     * @param client The MinecraftClient instance
     * @return Detailed state string with server information when applicable
     */
    public static String getDetailedGameState(MinecraftClient client) {
        State state = getGameStateEnum(client);

        switch (state) {
            case IN_GAME_MULTIPLAYER:
            case PAUSED_MULTIPLAYER:
                if (ParadiseClient.HUD_MOD.isConnectedToServer(client)) {
                    String serverStatus = ParadiseClient.HUD_MOD.getServerStatus(client);
                    return serverStatus != null ? serverStatus : state.getDisplayName();
                }
                break;
            case IN_GAME_SINGLEPLAYER:
            case PAUSED_SINGLEPLAYER:
                if (client.world != null && client.world.getRegistryKey() != null) {
                    String worldName = client.world.getRegistryKey().getValue().toString();
                    return state.getDisplayName() + " - " + worldName;
                }
                break;
            default:
                break;
        }

        return state.getDisplayName();
    }

    /**
     * Checks if the player is currently in an active game (not in menus).
     *
     * @param client The MinecraftClient instance
     * @return true if the player is actively playing, false otherwise
     */
    public static boolean isInGame(MinecraftClient client) {
        State state = getGameStateEnum(client);
        return state == State.IN_GAME_MULTIPLAYER || state == State.IN_GAME_SINGLEPLAYER;
    }

    /**
     * Checks if the player is currently paused.
     *
     * @param client The MinecraftClient instance
     * @return true if the game is paused, false otherwise
     */
    public static boolean isPaused(MinecraftClient client) {
        State state = getGameStateEnum(client);
        return state == State.PAUSED_MULTIPLAYER || state == State.PAUSED_SINGLEPLAYER;
>>>>>>> Stashed changes
    }

    return state.getDisplayName();
  }

  /**
   * Determines the current game state and returns the corresponding enum.
   *
   * @param client The MinecraftClient instance
   *
   * @return The State enum representing the current game state
   */
  public static State getGameStateEnum(MinecraftClient client) {
    if (client.currentScreen != null) {
      if (client.currentScreen instanceof TitleScreen) {
        return State.MAIN_MENU;
      }
      if (client.currentScreen instanceof MultiplayerScreen) {
        return State.MULTIPLAYER;
      }
      if (client.currentScreen instanceof SelectWorldScreen) {
        return State.SINGLEPLAYER;
      }
      if (client.currentScreen instanceof GameMenuScreen) {
        return client.getCurrentServerEntry() != null ? State.PAUSED_MULTIPLAYER : State.PAUSED_SINGLEPLAYER;
      }
      if (client.currentScreen instanceof DisconnectedScreen) {
        return State.DISCONNECTED;
      }
      if (client.currentScreen instanceof ConnectScreen) {
        return State.CONNECTING;
      }

      // If we have a screen but it's not one of the above, check if we're in-game
      if (ParadiseClient.HUD_MOD.isConnectedToServer(client)) {
        return State.IN_GAME_MULTIPLAYER;
      }

      return State.UNKNOWN;
    }

    // No screen means we're likely in-game
    if (ParadiseClient.HUD_MOD.isConnectedToServer(client)) {
      return State.IN_GAME_MULTIPLAYER;
    }

    if (client.isInSingleplayer()) {
      return State.IN_GAME_SINGLEPLAYER;
    }

    return State.UNKNOWN;
  }

  /**
   * Checks if the player is currently in an active game (not in menus).
   *
   * @param client The MinecraftClient instance
   *
   * @return true if the player is actively playing, false otherwise
   */
  public static boolean isInGame(MinecraftClient client) {
    State state = getGameStateEnum(client);
    return state == State.IN_GAME_MULTIPLAYER || state == State.IN_GAME_SINGLEPLAYER;
  }

  /**
   * Checks if the player is currently paused.
   *
   * @param client The MinecraftClient instance
   *
   * @return true if the game is paused, false otherwise
   */
  public static boolean isPaused(MinecraftClient client) {
    State state = getGameStateEnum(client);
    return state == State.PAUSED_MULTIPLAYER || state == State.PAUSED_SINGLEPLAYER;
  }

  enum State {
    MAIN_MENU("In Main Menu"),
    MULTIPLAYER("Browsing Multiplayer Servers"),
    SINGLEPLAYER("Browsing Singleplayer Worlds"),
    PAUSED_MULTIPLAYER("Paused in Multiplayer"),
    PAUSED_SINGLEPLAYER("Paused in Singleplayer"),
    DISCONNECTED("Disconnected from Server"),
    CONNECTING("Connecting to Server..."),
    IN_GAME_MULTIPLAYER("Playing Multiplayer"),
    IN_GAME_SINGLEPLAYER("Singleplayer Mode"),
    UNKNOWN("Unknown State...");

    private final String displayName;

    State(String displayName) {
      this.displayName = displayName;
    }

    public String getDisplayName() {
      return displayName;
    }
  }
}