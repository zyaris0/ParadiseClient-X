package net.paradise_client.chatroom.server.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import net.paradise_client.chatroom.common.exception.BadPacketException;
import net.paradise_client.chatroom.common.packet.Packet;
import net.paradise_client.chatroom.common.packet.PacketRegistry;
import net.paradise_client.chatroom.common.packet.impl.DisconnectPacket;
import net.paradise_client.chatroom.server.Logging;
import net.paradise_client.chatroom.server.handler.ServerPacketHandler;

public class ChatRoomServerHandler extends SimpleChannelInboundHandler<ByteBuf> {
    private ServerPacketHandler packetHandler;

    @Override
    public void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        Packet packet = PacketRegistry.createAndDecode(msg.readInt(), msg);
        if (packet == null) throw new BadPacketException("Unknown packet");
        packet.handle(packetHandler);
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        packetHandler = new ServerPacketHandler(ctx.channel());
        ChatRoomServer.channels.add(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ChatRoomServer.channels.remove(ctx.channel());
        if (packetHandler.userModel != null) {
            ChatRoomServer.onlineUsers.remove(packetHandler.userModel);
            Logging.info("Disconnection: " + packetHandler.userModel.username() +
                    ctx.channel().attr(
                            AttributeKey.valueOf(
                                    "proxiedAddress"
                            )
                    ).get());
            ChatRoomServer.broadcastMessage(packetHandler.userModel.username() + " left the chat");
        }
        packetHandler = null;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        Logging.error("Exception caught on netty thread", cause);
        PacketRegistry.sendPacket(
                new DisconnectPacket(
                        "Error in netty thread, check server console."
                ),
                ctx.channel()
        );
        ctx.close();
    }
}
