package com.example.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

public class AppClient implements Serializable {
    public void run() throws InterruptedException {
        //定义线程池
        NioEventLoopGroup group = new NioEventLoopGroup();
        //启动客户端的辅助类
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .remoteAddress(new InetSocketAddress(8080))
                    //选择初始化一个什么样的channel
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new MyChannelHandlerClient());
                        }
                    });
            //netty所有的操作都是异步的，所以会调用sync()同步等待
            //尝试连接服务器（同步等待返回future）
            ChannelFuture channelFuture = bootstrap.connect().sync();
            //获取channel，并且写出数据
            channelFuture.channel().writeAndFlush(Unpooled.copiedBuffer("hello netty".getBytes(Charset.forName("UTF-8"))));
            //阻塞程序，等待程序接收（同步等待将来正常关闭channel）
            channelFuture.channel().closeFuture().sync();
        }
        finally {
            group.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new AppClient().run();
    }
}
