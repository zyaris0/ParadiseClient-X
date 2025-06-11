package io.github.spigotrce.paradiseclientfabric.inject.accessor;

import net.md_5.bungee.protocol.DefinedPacket;

public interface DirectionDataAccessor {
    int paradiseClient_Fabric$getId(Class<? extends DefinedPacket> packet, int version);
}
