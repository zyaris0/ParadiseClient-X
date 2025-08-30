package net.paradise_client.protocol.packet;

import net.paradise_client.protocol.packet.impl.*;

public abstract class AbstractPacketHandler {
  public void handle(PluginMessagePacket packet) throws Exception {
    throw new IllegalAccessException("No handler for PluginMessagePacket in current phase!");
  }

  public void handle(HandshakePacket packet) throws Exception {
    throw new IllegalAccessException("No handler for HandshakePacket in current phase!");
  }
}
