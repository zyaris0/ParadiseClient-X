package net.paradise_client.netty;

import io.netty.buffer.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.paradise_client.*;
import net.paradise_client.protocol.Protocol;
import net.paradise_client.protocol.packet.AbstractPacket;
import net.paradise_client.protocol.packet.impl.RawBytesPacket;

public class ParadiseEncoder extends MessageToByteEncoder<AbstractPacket> {
  protected void encode(ChannelHandlerContext ctx, AbstractPacket msg, ByteBuf out) throws Exception {
    Protocol protocol = Helper.getBungeeProtocolForCurrentPhase();
    int protocolVersion = ParadiseClient.NETWORK_CONFIGURATION.protocolVersion;

    int packetId;
    if (msg instanceof RawBytesPacket packet) {
      packetId = packet.getId();
    } else {
      packetId = protocol.TO_SERVER.getId(msg.getClass(), protocolVersion);
    }
    AbstractPacket.writeVarInt(packetId, out);
    msg.write(out, protocol, protocol.TO_SERVER.getDirection(), protocolVersion);
  }
}
