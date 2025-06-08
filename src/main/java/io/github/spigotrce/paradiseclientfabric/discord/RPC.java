package io.github.spigotrce.paradiseclientfabric.discord;

import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.CreateParams;
import de.jcm.discordgamesdk.activity.Activity;
import io.github.spigotrce.paradiseclientfabric.Constants;
import io.github.spigotrce.paradiseclientfabric.ParadiseClient_Fabric;
import net.minecraft.client.MinecraftClient;

import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.time.Instant;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Implements Runnable interface to run Discord Rich Presence functionality.
 * Downloads and sets up the Discord SDK if needed.
 *
 * @author SpigotRCE
 * @since 2.17
 */
@SuppressWarnings("BusyWait")
public class RPC implements Runnable {

    @Override
    public void run() {
        try {
            installDiscordSDK();
        } catch (IOException e) {
            Constants.LOGGER.error("Failed to download Discord SDK: {}", e.getMessage());
            return;
        }

        try {
            File sdkFile = new File(MinecraftClient.getInstance().runDirectory,
                    "paradise/discord/discord_game_sdk.dll");
            Core.init(sdkFile);
        } catch (Exception e) {
            Constants.LOGGER.error("Failed to initialize Discord SDK: {}", e.getMessage());
            return;
        }

        try (CreateParams params = new CreateParams()) {
            params.setClientID(1164104022265974784L);
            params.setFlags(CreateParams.getDefaultFlags());

            try (Core core = new Core(params)) {
                try (Activity activity = new Activity()) {
                    activity.assets().setLargeImage("af9df3fa19b7374e5e7582865f9fb1e7");
                    activity.setDetails("In Menu");
                    activity.timestamps().setStart(Instant.now());

                    core.activityManager().updateActivity(activity);

                    while (true) {
                        try {
                            core.runCallbacks();

                            if (ParadiseClient_Fabric.NETWORK_MOD.isConnected) {
                                activity.setDetails("Playing on a server");
                                activity.setState(Objects.isNull(MinecraftClient.getInstance().getCurrentServerEntry())
                                        ? "Hidden"
                                        : MinecraftClient.getInstance().getCurrentServerEntry().address);
                            } else {
                                activity.setDetails("In Menu");
                                activity.setState("");
                            }

                            core.activityManager().updateActivity(activity);
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            Constants.LOGGER.error("Interrupted Discord RPC thread: {}", e.getMessage());
                            break;
                        } catch (Exception e) {
                            Constants.LOGGER.error("Error while updating Discord activity: {}", e.getMessage());
                        }
                    }
                }
            }
        }
    }

    /**
     * Downloads and installs the Discord Game SDK DLL.
     */
    public static void installDiscordSDK() throws IOException {
        File targetDir = new File(MinecraftClient.getInstance().runDirectory, "paradise/discord");
        File outputFile = new File(targetDir, "discord_game_sdk.dll");

        if (outputFile.exists()) {
            return;
        }

        String url = "https://dl-game-sdk.discordapp.net/2.5.6/discord_game_sdk.zip";
        Path tempZipPath = Files.createTempFile("discord_sdk", ".zip");

        try (InputStream in = new URL(url).openStream()) {
            Files.copy(in, tempZipPath, StandardCopyOption.REPLACE_EXISTING);
            Constants.LOGGER.info("Downloaded Discord SDK ZIP.");
        }

        boolean extracted = false;

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(tempZipPath.toFile()))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().equals("lib/x86_64/discord_game_sdk.dll")) {
                    if (!targetDir.exists()) targetDir.mkdirs();
                    Files.copy(zis, outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    Constants.LOGGER.info("Extracted Discord SDK DLL to {}", outputFile.getAbsolutePath());
                    extracted = true;
                    break;
                }
                zis.closeEntry();
            }
        }

        Files.deleteIfExists(tempZipPath);

        if (!extracted) {
            throw new FileNotFoundException("DLL not found inside Discord SDK ZIP");
        }
    }
}
