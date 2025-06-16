package net.paradise_client.chatroom.common.packet.handler;

import io.netty.channel.Channel;
import net.paradise_client.chatroom.common.packet.Packet;
import net.paradise_client.chatroom.common.packet.impl.*;

public abstract class AbstractPacketHandler {
    public final Channel channel;

    public AbstractPacketHandler(Channel channel) {
        this.channel = channel;
    }

    public void handle(Packet packet) {
    }

    public void handle(HandshakePacket packet) throws Exception {
    }

    public void handle(HandshakeResponsePacket packet) throws Exception {
    }

    public void handle(KeepAlivePacket packet) throws Exception {
    }

    public void handle(DisconnectPacket packet) throws Exception {
    }

    public void handle(MessagePacket packet) throws Exception {
    }
}
