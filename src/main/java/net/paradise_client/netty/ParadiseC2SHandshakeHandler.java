package net.paradise_client.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import net.md_5.bungee.protocol.packet.Handshake;
import net.minecraft.network.PacketByteBuf;
import net.paradise_client.Helper;
import net.paradise_client.ParadiseClient;
import net.paradise_client.protocol.ProtocolVersion;

import java.util.List;

public class ParadiseC2SHandshakeHandler extends MessageToMessageEncoder<ByteBuf> {
    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        PacketByteBuf b = Helper.byteBufToPacketBuf(ctx.alloc().buffer().writeBytes(in));

        b.readVarInt();
        decodeHandshake(b);

        ctx.pipeline().remove(this);

        out.add(in.resetReaderIndex().retain());
    }

    private void decodeHandshake(PacketByteBuf b) {
        Handshake handshake = new Handshake();
        handshake.read(b.asByteBuf());
        int protocolVersion = handshake.getProtocolVersion();
        if (!(ProtocolVersion.isSupported(protocolVersion))) {
            throw new IllegalArgumentException("Protocol version: " +
                    protocolVersion +
                    " is not supported by ParadiseClient!");
        }
        ParadiseClient.NETWORK_CONFIGURATION.protocolVersion = protocolVersion;
    }
}
