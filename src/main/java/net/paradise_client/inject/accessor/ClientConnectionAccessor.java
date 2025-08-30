package net.paradise_client.inject.accessor;

import io.netty.channel.Channel;

public interface ClientConnectionAccessor {
  Channel paradiseClient$getChannel();
}
