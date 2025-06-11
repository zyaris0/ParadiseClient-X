package io.github.spigotrce.paradiseclientfabric.netty;

import io.github.spigotrce.paradiseclientfabric.Helper;
import io.github.spigotrce.paradiseclientfabric.ParadiseClient_Fabric;
import io.github.spigotrce.paradiseclientfabric.inject.accessor.DirectionDataAccessor;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.Protocol;

public class ParadiseEncoder extends MessageToByteEncoder<DefinedPacket> {
    protected void encode(ChannelHandlerContext ctx, DefinedPacket msg, ByteBuf out) throws Exception {
        Protocol protocol = Helper.getBungeeProtocolForCurrentPhase();
        int protocolVersion = ParadiseClient_Fabric.NETWORK_CONFIGURATION.protocolVersion;

        int packetId = ((DirectionDataAccessor) (Object) protocol.TO_SERVER)
                .paradiseClient_Fabric$getId(msg.getClass(), protocolVersion);

        DefinedPacket.writeVarInt(packetId, out);
        msg.write(out, protocol, protocol.TO_SERVER.getDirection(), protocolVersion);
    }
}
