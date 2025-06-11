package io.github.spigotrce.paradiseclientfabric.inject.mixin.network.connection;

import io.github.spigotrce.paradiseclientfabric.inject.accessor.ClientConnectionAccessor;
import io.netty.channel.Channel;
import net.minecraft.network.ClientConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ClientConnection.class)
public class ClientConnection_2Mixin implements ClientConnectionAccessor {
    @Shadow private Channel channel;

    @Override
    public Channel paradiseClient_Fabric$getChannel() {
        return channel;
    }
}
