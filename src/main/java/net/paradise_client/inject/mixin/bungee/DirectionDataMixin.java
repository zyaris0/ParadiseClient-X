package net.paradise_client.inject.mixin.bungee;

import net.paradise_client.inject.accessor.DirectionDataAccessor;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.Protocol;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = Protocol.DirectionData.class, remap = false)
public abstract class DirectionDataMixin implements DirectionDataAccessor {

    @Shadow abstract int getId(Class<? extends DefinedPacket> packet, int version);

    @Override
    public int paradiseClient_Fabric$getId(Class<? extends DefinedPacket> packet, int version) {
        return getId(packet, version);
    }
}
