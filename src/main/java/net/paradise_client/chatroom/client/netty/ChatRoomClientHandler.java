package net.paradise_client.chatroom.client.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.paradise_client.Constants;
import net.paradise_client.Helper;
import net.paradise_client.ParadiseClient;
import net.paradise_client.chatroom.client.TokenStore;
import net.paradise_client.chatroom.client.handler.ClientPacketHandler;
import net.paradise_client.chatroom.common.Version;
import net.paradise_client.chatroom.common.exception.BadPacketException;
import net.paradise_client.chatroom.common.packet.Packet;
import net.paradise_client.chatroom.common.packet.PacketRegistry;
import net.paradise_client.chatroom.common.packet.impl.DisconnectPacket;
import net.paradise_client.chatroom.common.packet.impl.HandshakePacket;

public class ChatRoomClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
    private ClientPacketHandler packetHandler;

    @Override
    public void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        Packet packet = PacketRegistry.createAndDecode(msg.readInt(), msg);
        if (packet == null) throw new BadPacketException("Unknown packet");
        packet.handle(packetHandler);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        packetHandler = new ClientPacketHandler(ctx.channel());
        PacketRegistry.sendPacket(new HandshakePacket(Version.PROTOCOL_VERSION, TokenStore.readToken()), ctx.channel());
        ParadiseClient.CHAT_ROOM_MOD.isConnected = true;
        ParadiseClient.CHAT_ROOM_MOD.channel = ctx.channel();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Helper.printChatMessage("[ChatRoom] Disconnected from chat server");
        packetHandler = null;
        ParadiseClient.CHAT_ROOM_MOD.isConnected = false;
        ParadiseClient.CHAT_ROOM_MOD.channel = null;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        Helper.printChatMessage("[ChatRoom] Exception caught on netty thread");
        Constants.LOGGER.error("[ChatRoom] Exception caught on netty thread", cause);
        PacketRegistry.sendPacket(new DisconnectPacket(), ctx.channel());
        ctx.close();
    }
}
