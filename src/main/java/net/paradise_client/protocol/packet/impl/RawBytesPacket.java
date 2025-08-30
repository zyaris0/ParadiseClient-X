package net.paradise_client.protocol.packet.impl;

import io.netty.buffer.*;
import net.paradise_client.protocol.*;
import net.paradise_client.protocol.packet.*;

/**
 * This packet allows to send raw bytes over any packet id
 *
 * @author SpigotRCE
 */
public class RawBytesPacket extends AbstractPacket {
  private final byte[] bytes;
  private final int id;

  public RawBytesPacket(byte[] bytes, int id) {
    this.bytes = bytes;
    this.id = id;
  }

  @Override public void write(ByteBuf b, Protocol protocol, ProtocolVersion.Direction direction, int protocolVersion) {
    b.writeBytes(bytes);
  }

  @Override public void handle(AbstractPacketHandler handler) throws Exception {
  }

  public byte[] getBytes() {
    return bytes;
  }

  public int getId() {
    return id;
  }

  @Override public boolean equals(Object o) {
    return false;
  }

  @Override public int hashCode() {
    return 0;
  }

  @Override public String toString() {
    return "";
  }
}
