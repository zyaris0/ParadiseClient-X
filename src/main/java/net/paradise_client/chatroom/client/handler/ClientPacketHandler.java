package net.paradise_client.chatroom.client.handler;

import net.paradise_client.Constants;
import net.paradise_client.Helper;
import net.paradise_client.ParadiseClient;
import net.paradise_client.chatroom.common.model.UserModel;
import net.paradise_client.chatroom.common.packet.PacketRegistry;
import net.paradise_client.chatroom.common.packet.handler.AbstractPacketHandler;
import net.paradise_client.chatroom.common.packet.impl.DisconnectPacket;
import net.paradise_client.chatroom.common.packet.impl.HandshakeResponsePacket;
import net.paradise_client.chatroom.common.packet.impl.KeepAlivePacket;
import net.paradise_client.chatroom.common.packet.impl.MessagePacket;
import io.netty.channel.Channel;

public class ClientPacketHandler extends AbstractPacketHandler {
    private boolean isAuthenticated = false;
    private UserModel userModel;
    public long timeSinceKeepAlive = 0;
    public int keepAliveViolation = 0;

    public ClientPacketHandler(Channel channel) {
        super(channel);
    }

    @Override
    public void handle(HandshakeResponsePacket packet) {
        if (isAuthenticated) return;
        isAuthenticated = packet.isSuccess();
        userModel = packet.getUserModel();
        if (isAuthenticated)
            Helper.printChatMessage("[ChatRoom] Connected as " + userModel.username());
        else
            Helper.printChatMessage("[ChatRoom] Failed to authenticate: " + userModel.username());
        ParadiseClient.CHAT_ROOM_MOD.user = userModel;

        new Thread(() -> {
            while (channel.isOpen()) {
                try {
                    Thread.sleep(5000);
                    if (timeSinceKeepAlive + 5000 < System.currentTimeMillis()) {
                        keepAliveViolation++;
                        if (keepAliveViolation > 3) {
                            Helper.printChatMessage("[ChatRoom] Timed out");
                            channel.close();
                            break;
                        }
                    }
                } catch (InterruptedException e) {
                    Constants.LOGGER.error("Error occurred while waiting.", e);
                    Helper.printChatMessage("[ChatRoom] Error occurred while waiting");
                    channel.close();
                }
            }
        }).start();

        new Thread(() -> {
            while (channel.isOpen()) {
                try {
                    Thread.sleep(5000);
                    PacketRegistry.sendPacket(new KeepAlivePacket(1), channel);
                } catch (InterruptedException e) {
                    Constants.LOGGER.error("Error occurred while waiting.", e);
                    channel.close();
                }
            }
        }).start();
    }

    @Override
    public void handle(KeepAlivePacket packet) throws Exception {
        if (!isAuthenticated)
            throw new IllegalStateException("User not authenticated");
        timeSinceKeepAlive = System.currentTimeMillis();
    }

    @Override
    public void handle(DisconnectPacket packet) {
        Helper.printChatMessage("[ChatRoom] Disconnected from server: " + packet.getMessage());
        channel.close();
    }

    @Override
    public void handle(MessagePacket packet) {
        Helper.printChatMessage("[ChatRoom] " + packet.getMessage());
    }
}
