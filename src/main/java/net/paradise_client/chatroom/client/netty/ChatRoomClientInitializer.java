package net.paradise_client.chatroom.client.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import net.paradise_client.chatroom.common.netty.CommonChannelInitializer;

public class ChatRoomClientInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        CommonChannelInitializer.init(pipeline);

        pipeline.addLast(new ChatRoomClientHandler());
    }
}
