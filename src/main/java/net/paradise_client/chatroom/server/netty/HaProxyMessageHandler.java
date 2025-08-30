package net.paradise_client.chatroom.server.netty;

import io.netty.channel.*;
import io.netty.handler.codec.haproxy.HAProxyMessage;
import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;

public class HaProxyMessageHandler extends SimpleChannelInboundHandler<HAProxyMessage> {
  @Override protected void channelRead0(ChannelHandlerContext ctx, HAProxyMessage msg) throws Exception {
    ctx.channel()
      .attr(AttributeKey.valueOf("proxiedAddress"))
      .set(new InetSocketAddress(msg.sourceAddress(), msg.sourcePort()));
    ctx.fireChannelRead(msg.retain());
  }

}
