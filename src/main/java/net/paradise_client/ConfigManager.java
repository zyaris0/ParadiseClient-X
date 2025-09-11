package net.paradise_client;

import com.google.gson.*;
import net.paradise_client.wallpaper.Theme;

import java.io.*;

// TODO: Migrate to Boosted yaml
public class ConfigManager {
  private static final File CONFIG_FILE = new File("config/paradiseclient.json");
  private static JsonObject config;

  static {
    loadConfig();
  }

  // Load configuration from file
  private static void loadConfig() {
    try {
      if (CONFIG_FILE.exists()) {
        FileReader reader = new FileReader(CONFIG_FILE);
        config = JsonParser.parseReader(reader).getAsJsonObject();
        reader.close();
      } else {
        config = new JsonObject();
        saveConfig();
      }
    } catch (IOException e) {
      config = new JsonObject();
    }
  }

  // Save configuration to file
  private static void saveConfig() {
    try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
      writer.write(config.toString());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Gets the current theme from the configuration. If the theme is not set, it defaults to LEGACY.
   *
   * @return The current theme.
   */
  private static Theme migrate() {
    return switch (config.get("theme").getAsString()) {
      case "ParadiseHack" -> Theme.MATRIX;
      case "ParadiseParticle" -> Theme.PARTICLE;
      case "ParadiseLegacy" -> Theme.LEGACY;
      default -> Theme.LEGACY;
    };
  }

  public static Theme getTheme() {
    try {
      return config.has("theme") ? Theme.class.getEnumConstants()[config.get("theme").getAsInt()] : Theme.LEGACY;
    } catch (NumberFormatException e) {
      setTheme(migrate());
      return getTheme();
    }
  }

  public static void setTheme(Theme type) {
    config.addProperty("theme", type.ordinal()); // Update
    saveConfig(); // Immediate backup
  }

}
