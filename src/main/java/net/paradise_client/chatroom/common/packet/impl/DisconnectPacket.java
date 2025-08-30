package net.paradise_client.chatroom.common.packet.impl;

import io.netty.buffer.ByteBuf;
import net.paradise_client.chatroom.common.packet.Packet;
import net.paradise_client.chatroom.common.packet.handler.AbstractPacketHandler;

public class DisconnectPacket extends Packet {
  private String message = "";

  public DisconnectPacket() {
  }

  public DisconnectPacket(String message) {
    this.message = message;
  }

  @Override public void encode(ByteBuf buffer) {
    writeString(buffer, message);
  }

  @Override public void decode(ByteBuf buffer) {
    message = readString(buffer);
  }

  @Override public void handle(AbstractPacketHandler handler) throws Exception {
    handler.handle(this);
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
