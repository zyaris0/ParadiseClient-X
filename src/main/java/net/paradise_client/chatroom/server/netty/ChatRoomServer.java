package net.paradise_client.chatroom.server.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;
import net.paradise_client.chatroom.common.model.ServerModel;
import net.paradise_client.chatroom.common.model.UserModel;
import net.paradise_client.chatroom.common.packet.PacketRegistry;
import net.paradise_client.chatroom.common.packet.impl.MessagePacket;
import net.paradise_client.chatroom.server.Logging;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatRoomServer {
    public static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    public static final ArrayList<UserModel> onlineUsers = new ArrayList<>();
    public static final HashMap<String, Long> lastConnectionTime = new HashMap<>();

    public static void startServer(ServerModel serverModel) throws Exception {
        Logging.info("Starting chatroom server on port: " + serverModel.port() + ", use_haproxy: " + serverModel.useHAProxy());

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChatRoomServerInitializer());

            serverBootstrap.bind(serverModel.port()).sync().channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void broadcastMessage(String message) {
        Logging.info("[CHAT] " + message);
        MessagePacket packet = new MessagePacket(message);
        channels.forEach(channel -> PacketRegistry.sendPacket(packet, channel));
    }
}
