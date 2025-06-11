package io.github.spigotrce.paradiseclientfabric.netty;

import io.github.spigotrce.paradiseclientfabric.Helper;
import io.github.spigotrce.paradiseclientfabric.ParadiseClient_Fabric;
import io.github.spigotrce.paradiseclientfabric.event.channel.PluginMessageEvent;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.Protocol;
import net.md_5.bungee.protocol.ProtocolConstants;
import net.md_5.bungee.protocol.packet.PluginMessage;
import net.minecraft.network.PacketByteBuf;

import java.util.List;

public class ParadiseS2CPluginMessageHandler extends MessageToMessageDecoder<ByteBuf> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        PacketByteBuf b = Helper.byteBufToPacketBuf(ctx.alloc().buffer().writeBytes(in));

        Protocol protocol = Helper.getBungeeProtocolForCurrentPhase();

        if (protocol != null) {
            int id = b.readVarInt();
            int protocolVersion = ParadiseClient_Fabric.NETWORK_CONFIGURATION.protocolVersion;

            DefinedPacket packet = protocol.TO_CLIENT.createPacket(id, protocolVersion);
            if (packet instanceof PluginMessage message) {
                message.read(b.asByteBuf(), ProtocolConstants.Direction.TO_CLIENT, id);

                PluginMessageEvent event = new PluginMessageEvent(
                        message.getTag(),
                        new PacketByteBuf(
                                Unpooled.buffer()
                                        .writeBytes(message.getData()
                                        )
                        )
                );
                ParadiseClient_Fabric.EVENT_MANAGER.fireEvent(event);

                if (event.isCancel()) {
                    return;
                }
            }
        }

        out.add(in.resetReaderIndex().retain());
    }
}
