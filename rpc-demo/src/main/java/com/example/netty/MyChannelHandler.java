package com.example.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

public class MyChannelHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
        //处理收到的消息，并反馈消息到客户端

        ByteBuf in = (ByteBuf) msg;
        System.out.println("收到消息："+in.toString(CharsetUtil.UTF_8));
        //写入并发送消息到客户端
        channelHandlerContext.channel().writeAndFlush(Unpooled.copiedBuffer("我是服务端，已经收到客户端消息",CharsetUtil.UTF_8));

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //出现异常时执行的动作
        cause.printStackTrace();
        ctx.close();
    }
}
