package net.paradise_client.inject.mixin.bungee;

import net.md_5.bungee.protocol.*;
import net.paradise_client.inject.accessor.DirectionDataAccessor;
import org.spongepowered.asm.mixin.*;

@Mixin(value = Protocol.DirectionData.class, remap = false)
public abstract class DirectionDataMixin implements DirectionDataAccessor {

  @Override public int paradiseClient$getId(Class<? extends DefinedPacket> packet, int version) {
    return getId(packet, version);
  }

  @Shadow abstract int getId(Class<? extends DefinedPacket> packet, int version);
}
