package net.paradise_client;

import io.github.spigotrce.eventbus.event.EventManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.paradise_client.addon.AddonLoader;
import net.paradise_client.command.CommandManager;
import net.paradise_client.exploit.ExploitManager;
import net.paradise_client.listener.ChannelListener;
import net.paradise_client.listener.PacketListener;
import net.paradise_client.mod.*;
import net.paradise_client.packet.*;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.util.Objects;

/**
 * The main class for the ParadiseClient Fabric mod.
 * <p>
 * This class implements the {@link ModInitializer} interface and is responsible for initializing
 * the various components and modules of the mod when the mod is loaded.
 * </p>
 *
 * @author SpigotRCE
 * @since 1.0
 */
public class ParadiseClient implements ModInitializer, ClientModInitializer {

    public static final MinecraftClient MINECRAFT_CLIENT = MinecraftClient.getInstance();
    public static final EventManager EVENT_MANAGER = new EventManager();
    public static final NetworkConfiguration NETWORK_CONFIGURATION = new NetworkConfiguration();
    public static ParadiseClient INSTANCE;
    public static BungeeSpoofMod BUNGEE_SPOOF_MOD;
    public static MiscMod MISC_MOD;
    public static HudMod HUD_MOD;
    public static ChatRoomMod CHAT_ROOM_MOD;
    public static ExploitMod EXPLOIT_MOD;
    public static CommandManager COMMAND_MANAGER;
    public static ExploitManager EXPLOIT_MANAGER;
    public static NetworkMod NETWORK_MOD;

    @Override
    public void onInitializeClient() {
        INSTANCE = this;
        registerChannels();
        initializeMods();
        initializeManagers();
        initializeListeners();
        setupKeyBindings();
        checkForUpdates();
        AddonLoader.loadAddons();
    }

    @Override
    public void onInitialize() {
    }

    private void registerChannels() {
        PayloadTypeRegistry.playC2S().register(VelocityReportPayloadPacket.ID, VelocityReportPayloadPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(PurpurExploitPayloadPacket.ID, PurpurExploitPayloadPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(AuthMeVelocityPayloadPacket.ID, AuthMeVelocityPayloadPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(ChatSentryPayloadPacket.ID, ChatSentryPayloadPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(ECBPayloadPacket.ID, ECBPayloadPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(SignedVelocityPayloadPacket.ID, SignedVelocityPayloadPacket.CODEC);
    }

    private void initializeMods() {
        BUNGEE_SPOOF_MOD = new BungeeSpoofMod();
        MISC_MOD = new MiscMod();
        HUD_MOD = new HudMod();
        CHAT_ROOM_MOD = new ChatRoomMod();
        EXPLOIT_MOD = new ExploitMod();
        NETWORK_MOD = new NetworkMod();
    }

    private void initializeManagers() {
        EXPLOIT_MANAGER = new ExploitManager(MINECRAFT_CLIENT);
        EXPLOIT_MANAGER.init();

        COMMAND_MANAGER = new CommandManager(MINECRAFT_CLIENT);
        COMMAND_MANAGER.init();
    }

    private void initializeListeners() {
        EVENT_MANAGER.registerListener(new PacketListener());
        EVENT_MANAGER.registerListener(COMMAND_MANAGER);
        EVENT_MANAGER.registerListener(new ChannelListener());
    }

    private void setupKeyBindings() {
        KeyBinding keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Open paradise command",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_COMMA,
                Constants.MOD_NAME
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyBinding.wasPressed()) {
                client.setScreen(new ChatScreen(COMMAND_MANAGER.prefix));
            }
        });
    }

    private void checkForUpdates() {
        new Thread(() -> {
            try {
                String latestVersion = Helper.getLatestReleaseTag();
                if (latestVersion == null) return;

                MISC_MOD.latestVersion = latestVersion;
                MISC_MOD.isClientOutdated = !Objects.equals(latestVersion, Constants.VERSION);
                Constants.reloadTitle();

            } catch (IOException e) {
                Constants.LOGGER.error("Error checking for latest release tag", e);
            }
        }).start();
    }
}
