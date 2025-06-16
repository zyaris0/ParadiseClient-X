package net.paradise_client.chatroom.client.netty;

import net.paradise_client.chatroom.common.netty.CommonChannelInitializer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class ChatRoomClientInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        CommonChannelInitializer.init(pipeline);

        pipeline.addLast(new ChatRoomClientHandler());
    }
}
