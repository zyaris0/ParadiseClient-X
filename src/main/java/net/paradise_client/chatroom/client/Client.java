package net.paradise_client.chatroom.client;

import net.paradise_client.ParadiseClient;
import net.paradise_client.chatroom.client.netty.ChatRoomClient;
import net.paradise_client.chatroom.common.packet.PacketRegistry;
import net.paradise_client.chatroom.common.packet.impl.DisconnectPacket;

public class Client {
  public static void connect() throws Exception {
    PacketRegistry.registerPackets();
    new ChatRoomClient(TokenStore.token.split("\\.")[3], 25565).connect();
    ParadiseClient.CHAT_ROOM_MOD.isConnected = false;
  }

  public static void stop() {
    if (!ParadiseClient.CHAT_ROOM_MOD.isConnected) {
      return;
    }
    PacketRegistry.sendPacket(new DisconnectPacket(), ParadiseClient.CHAT_ROOM_MOD.channel);
    ParadiseClient.CHAT_ROOM_MOD.channel.close();
  }
}
