package net.paradise_client.inject.mixin.network.connection;

import net.paradise_client.Constants;
import net.paradise_client.netty.NettyConstants;
import net.paradise_client.netty.ParadiseEncoder;
import net.paradise_client.netty.ParadiseS2CPluginMessageHandler;
import net.paradise_client.netty.ParadiseC2SHandshakeHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.handler.HandlerNames;
import net.minecraft.network.handler.PacketSizeLogger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientConnection.class, priority = 1001)
public class ClientConnection_1Mixin {
    /**
     * Responsible for injecting our handlers into
     * the pipeline to decode packets before via version does.
     */
    @Inject(method = "addHandlers", at = @At("RETURN"))
    private static void onAddHandlers(
            ChannelPipeline pipeline,
            NetworkSide side,
            boolean local,
            PacketSizeLogger packetSizeLogger,
            CallbackInfo ci
    ) {
        if (pipeline.channel() instanceof SocketChannel) {
            pipeline.addBefore(
                    HandlerNames.ENCODER,
                    NettyConstants.PARADISE_C2S_HANDSHAKE_HANDLER,
                    new ParadiseC2SHandshakeHandler()
            );
            pipeline.addBefore(HandlerNames.INBOUND_CONFIG,
                    NettyConstants.PARADISE_S2C_PLUGIN_MESSAGE_HANDLER,
                    new ParadiseS2CPluginMessageHandler()
            );
            pipeline.addLast(NettyConstants.PARADISE_ENCODER,
                    new ParadiseEncoder()
            );
        } else
            Constants.LOGGER.warn("Channel not an instance of netty socket");
    }
}
