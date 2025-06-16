package net.paradise_client.chatroom.common.packet.impl;

import net.paradise_client.chatroom.common.packet.Packet;
import net.paradise_client.chatroom.common.packet.handler.AbstractPacketHandler;
import io.netty.buffer.ByteBuf;

public class HandshakePacket extends Packet {
    private int pvn = 0;
    private String token = "";

    public HandshakePacket() {
    }

    public HandshakePacket(int pvn, String token) {
        this.pvn = pvn;
        this.token = token;
    }

    @Override
    public void encode(ByteBuf buffer) {
        writeInt(buffer, pvn);
        writeString(buffer, token);
    }

    @Override
    public void decode(ByteBuf buffer) {
        pvn = readInt(buffer);
        token = readString(buffer);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle(this);
    }

    public int getPvn() {
        return pvn;
    }

    public void setPvn(int pvn) {
        this.pvn = pvn;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
