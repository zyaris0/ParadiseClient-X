package io.github.spigotrce.paradiseclientfabric.inject.accessor;

import io.netty.channel.Channel;

public interface ClientConnectionAccessor {
    Channel paradiseClient_Fabric$getChannel();
}
