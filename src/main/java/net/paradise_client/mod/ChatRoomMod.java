package net.paradise_client.mod;

import net.paradise_client.chatroom.common.model.UserModel;
import io.netty.channel.Channel;

public class ChatRoomMod {
    public boolean isConnected = false;
    public Channel channel;
    public String token;
    public UserModel user;
}
