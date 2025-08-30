package net.paradise_client.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.minecraft.SharedConstants;
import net.paradise_client.Helper;
import net.paradise_client.protocol.Protocol;
import net.paradise_client.protocol.packet.AbstractPacket;

public class ParadiseEncoder extends MessageToByteEncoder<AbstractPacket> {
  protected void encode(ChannelHandlerContext ctx, AbstractPacket msg, ByteBuf out) throws Exception {
    Protocol protocol = Helper.getBungeeProtocolForCurrentPhase();
    int protocolVersion = SharedConstants.getProtocolVersion();

    int packetId = protocol.TO_SERVER.getId(msg.getClass(), protocolVersion);

    AbstractPacket.writeVarInt(packetId, out);
    msg.write(out, protocol, protocol.TO_SERVER.getDirection(), protocolVersion);
  }
}
