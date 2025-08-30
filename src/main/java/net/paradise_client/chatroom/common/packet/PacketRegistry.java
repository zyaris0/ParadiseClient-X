package net.paradise_client.chatroom.common.packet;

import io.netty.buffer.*;
import io.netty.channel.Channel;
import net.paradise_client.chatroom.common.exception.BadPacketException;
import net.paradise_client.chatroom.common.packet.impl.*;

import java.util.*;
import java.util.function.Supplier;

public class PacketRegistry {
  private static final Map<Integer, Supplier<Packet>> packetMap = new HashMap<>();
  private static int nextPacketId = 0;

  public static void registerPackets() {
    packetMap.clear();
    registerPacket(HandshakePacket::new);
    registerPacket(HandshakeResponsePacket::new);
    registerPacket(KeepAlivePacket::new);
    registerPacket(DisconnectPacket::new);
    registerPacket(MessagePacket::new);
  }

  private static void registerPacket(Supplier<Packet> supplier) {
    packetMap.put(nextPacketId, supplier);
    nextPacketId++;
  }

  public static Packet createAndDecode(int id, ByteBuf buf) {
    Supplier<Packet> supplier = packetMap.get(id);
    if (supplier != null) {
      Packet packet = supplier.get();
      packet.decode(buf);
      return packet;
    }
    return null;
  }

  public static void sendPacket(Packet packet, Channel channel) {
    ByteBuf buf = Unpooled.buffer();
    buf.writeInt(getPacketId(packet));
    packet.encode(buf);
    channel.writeAndFlush(buf);
  }

  public static int getPacketId(Packet packet) {
    for (Map.Entry<Integer, Supplier<Packet>> entry : packetMap.entrySet()) {
      if (packet.getClass().equals(entry.getValue().get().getClass())) {
        return entry.getKey();
      }
    }
    throw new BadPacketException("Packet not registered: " + packet.getClass().getName());
  }
}
