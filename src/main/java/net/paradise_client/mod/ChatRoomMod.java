package net.paradise_client.mod;

import io.netty.channel.Channel;
import net.paradise_client.chatroom.common.model.UserModel;

public class ChatRoomMod {
  public boolean isConnected = false;
  public Channel channel;
  public String token;
  public UserModel user;
}
