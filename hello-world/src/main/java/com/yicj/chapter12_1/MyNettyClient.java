package com.yicj.chapter12_1;

import com.yicj.chapter12_1.codec.MyNettyEncoder;
import com.yicj.chapter12_1.handler.MyClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;


public class MyNettyClient {
    public static void main(String[] args) throws InterruptedException {
        new MyNettyClient().connect(8080,"127.0.0.1");
    }

    public void connect(int port, String host) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap() ;
            b.group(group) ;
            b.channel(NioSocketChannel.class) ;
            b.option(ChannelOption.TCP_NODELAY,true) ;
            b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS,3000) ;
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline p = ch.pipeline();
                    p.addLast(new MyNettyEncoder()) ;
                    p.addLast(new MyClientHandler()) ;
                }
            }) ;
            ChannelFuture future = b.connect(host, port).sync();
            //future.channel().writeAndFlush("Hello Netty Server, I am a common client") ;
            future.channel().closeFuture().sync() ;
        }finally {
            group.shutdownGracefully() ;
        }
    }


}
