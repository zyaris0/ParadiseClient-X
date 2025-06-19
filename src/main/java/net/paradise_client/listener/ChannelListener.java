package net.paradise_client.listener;

import io.github.spigotrce.eventbus.event.EventHandler;
import io.github.spigotrce.eventbus.event.listener.Listener;
import net.minecraft.network.PacketByteBuf;
import net.paradise_client.Constants;
import net.paradise_client.Helper;
import net.paradise_client.ParadiseClient;
import net.paradise_client.event.channel.PluginMessageEvent;

import java.nio.charset.Charset;
import java.util.Objects;

/**
 * Listener responsible for notifying when a plugin message
 * is sent from the client
 */
public class ChannelListener implements Listener {
    @SuppressWarnings("unused")
    @EventHandler
    public void onChannelRegister(PluginMessageEvent event) {
        String channelName = event.getChannel();
        PacketByteBuf buf = event.getBuf();

        Helper.printChatMessage("&8&m-----------------------------------------------------", false);
        try {
            if (Objects.equals(channelName, "minecraft:register")
                    || Objects.equals(channelName, "REGISTER")) {
                Helper.printChatMessage("&b&l[Channel Registration]");
                String[] channels = buf.toString(Charset.defaultCharset()).split("\000");

                for (String channel : channels) {
                    boolean isKnown = ParadiseClient.NETWORK_MOD.getRegisteredChannelsByName().contains(channel);
                    String color = isKnown ? "&c" : "&a";
                    Helper.printChatMessage("&7 - &fChannel: " + color + channel);

                    if (isKnown) {
                        Helper.showNotification("Exploit Detected!", "Channel: " + channel);
                    }
                }
            } else {
                Helper.printChatMessage("&b&l[Channel Data]");
                String color = ParadiseClient.NETWORK_MOD.getRegisteredChannelsByName()
                        .contains(channelName) ? "&c" : "&a";
                Helper.printChatMessage("&7 - &fChannel: " + color + channelName);
                Helper.printChatMessage("&7 - &fRaw Data: " + color + buf.toString(Charset.defaultCharset()));
            }
        } catch (Exception e) {
            Helper.printChatMessage("&4&l[Error] &cException while processing plugin message:");
            Helper.printChatMessage("&7 - &fChannel: &a" + channelName);
            Helper.printChatMessage("&7 - &fMessage: &a" + e.getMessage());

            Constants.LOGGER.error("Error handling listener for channel: {} {}", channelName, e);
        }

        Helper.printChatMessage("&8&m-----------------------------------------------------", false);
    }
}
