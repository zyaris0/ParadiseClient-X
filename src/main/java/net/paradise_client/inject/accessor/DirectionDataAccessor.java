package net.paradise_client.inject.accessor;

import net.md_5.bungee.protocol.DefinedPacket;

public interface DirectionDataAccessor {
    int paradiseClient$getId(Class<? extends DefinedPacket> packet, int version);
}
