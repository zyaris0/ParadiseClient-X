package net.paradise_client.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.Protocol;
import net.minecraft.SharedConstants;
import net.paradise_client.Helper;
import net.paradise_client.ParadiseClient;
import net.paradise_client.inject.accessor.DirectionDataAccessor;

public class ParadiseEncoder extends MessageToByteEncoder<DefinedPacket> {
    protected void encode(ChannelHandlerContext ctx, DefinedPacket msg, ByteBuf out) throws Exception {
        Protocol protocol = Helper.getBungeeProtocolForCurrentPhase();
        int protocolVersion = SharedConstants.getProtocolVersion();

        int packetId = ((DirectionDataAccessor) (Object) protocol.TO_SERVER)
                .paradiseClient$getId(msg.getClass(), protocolVersion);

        DefinedPacket.writeVarInt(packetId, out);
        msg.write(out, protocol, protocol.TO_SERVER.getDirection(), protocolVersion);
    }
}
