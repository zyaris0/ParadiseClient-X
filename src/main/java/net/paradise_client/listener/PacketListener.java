package net.paradise_client.listener;

import io.github.spigotrce.eventbus.event.EventHandler;
import io.github.spigotrce.eventbus.event.listener.Listener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.s2c.common.ResourcePackSendS2CPacket;
import net.minecraft.network.packet.s2c.play.CommandSuggestionsS2CPacket;
import net.minecraft.text.Text;
import net.paradise_client.Helper;
import net.paradise_client.event.packet.incoming.PacketIncomingPreEvent;

import java.util.List;

/**
 * Listener responsible for packet related events.
 */
public class PacketListener implements Listener {
    /**
     * Responsible for notifying the server
     * resource pack url.
     *
     * @param event the event
     */
    @EventHandler
    public void onResourcePackPacketReceive(PacketIncomingPreEvent event) {
        if (!(event.getPacket() instanceof ResourcePackSendS2CPacket packet)) return;
        String url = packet.url();
        Helper.printChatMessage(Text.of("Server resource pack url: " + url));
    }

    /**
     * Responsible for sending bungeecord ip commands
     * when the server sends a tab completion packet.
     *
     * @param event
     */
    @EventHandler
    public void onIncomingPacketReceive(PacketIncomingPreEvent event) {
        if (!(event.getPacket() instanceof CommandSuggestionsS2CPacket packet)) return;
        if (packet.id() != 1234689045) return;
        Helper.printChatMessage("Command suggestions received! Dumping");
        List<CommandSuggestionsS2CPacket.Suggestion> suggestions = packet.suggestions();

        new Thread(() -> {
            try {
                suggestions.forEach(suggestion -> {
                    MinecraftClient.getInstance().getNetworkHandler().sendChatCommand("ip " + suggestion.text());
                });
            } catch (Exception ignored) {
            }
        }).start();
    }
}
