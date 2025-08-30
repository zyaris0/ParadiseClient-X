package net.paradise_client.protocol.packet.impl;

import io.netty.buffer.ByteBuf;
import net.paradise_client.protocol.packet.AbstractPacketHandler;
import net.paradise_client.protocol.packet.AbstractPacket;

public class HandshakePacket extends AbstractPacket {
  private int protocolVersion;
  private String host;
  private int port;
  private int requestedProtocol;

  public void read(ByteBuf buf) {
    this.protocolVersion = readVarInt(buf);
    this.host = readString(buf, 255);
    this.port = buf.readUnsignedShort();
    this.requestedProtocol = readVarInt(buf);
  }

  public void write(ByteBuf buf) {
    writeVarInt(this.protocolVersion, buf);
    writeString(this.host, buf);
    buf.writeShort(this.port);
    writeVarInt(this.requestedProtocol, buf);
  }

  public void handle(AbstractPacketHandler handler) throws Exception {
    handler.handle(this);
  }

  public int getProtocolVersion() {
    return this.protocolVersion;
  }

  public String getHost() {
    return this.host;
  }

  public int getPort() {
    return this.port;
  }

  public int getRequestedProtocol() {
    return this.requestedProtocol;
  }

  public void setProtocolVersion(int protocolVersion) {
    this.protocolVersion = protocolVersion;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public void setRequestedProtocol(int requestedProtocol) {
    this.requestedProtocol = requestedProtocol;
  }

  public String toString() {
    return "Handshake(protocolVersion=" +
      this.getProtocolVersion() +
      ", host=" +
      this.getHost() +
      ", port=" +
      this.getPort() +
      ", requestedProtocol=" +
      this.getRequestedProtocol() +
      ")";
  }

  public HandshakePacket() {
  }

  public HandshakePacket(int protocolVersion, String host, int port, int requestedProtocol) {
    this.protocolVersion = protocolVersion;
    this.host = host;
    this.port = port;
    this.requestedProtocol = requestedProtocol;
  }

  public boolean equals(Object o) {
    if (o == this) {
      return true;
    } else if (!(o instanceof HandshakePacket other)) {
      return false;
    } else {
      if (!other.canEqual(this)) {
        return false;
      } else if (this.getProtocolVersion() != other.getProtocolVersion()) {
        return false;
      } else if (this.getPort() != other.getPort()) {
        return false;
      } else if (this.getRequestedProtocol() != other.getRequestedProtocol()) {
        return false;
      } else {
        Object this$host = this.getHost();
        Object other$host = other.getHost();
        if (this$host == null) {
          return other$host == null;
        } else
          return this$host.equals(other$host);
      }
    }
  }

  protected boolean canEqual(Object other) {
    return other instanceof HandshakePacket;
  }

  public int hashCode() {
    int result = 1;
    result = result * 59 + this.getProtocolVersion();
    result = result * 59 + this.getPort();
    result = result * 59 + this.getRequestedProtocol();
    Object $host = this.getHost();
    result = result * 59 + ($host == null ? 43 : $host.hashCode());
    return result;
  }
}
