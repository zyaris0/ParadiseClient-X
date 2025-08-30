package net.paradise_client.chatroom.common.netty;

import io.netty.channel.ChannelPipeline;

public class CommonChannelInitializer {
  public static void init(ChannelPipeline pipeline) {
    pipeline.addLast(new LengthPrefixedDecoder());
    pipeline.addLast(new LengthPrefixedEncoder());
  }
}
