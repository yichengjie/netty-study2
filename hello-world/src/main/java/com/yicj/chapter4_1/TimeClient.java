package com.yicj.chapter4_1;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

public class TimeClient {
    public void connect(int port ,String host) throws Exception {
        //配置客户端NIO线程组
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap() ;
            b.group(group) ;
            b.channel(NioSocketChannel.class) ;
            b.option(ChannelOption.TCP_NODELAY,true) ;
            b.handler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    ChannelPipeline pipeline = socketChannel.pipeline();
                    pipeline.addLast(new LineBasedFrameDecoder(1024)) ;
                    pipeline.addLast(new StringDecoder()) ;
                    pipeline.addLast(new TimeClientHandler()) ;
                }
            }) ;
            //发起异步连接操作
            ChannelFuture f = b.connect(host, port).sync();
            //等待客户端链路关闭
            f.channel().closeFuture().sync() ;
        }finally {
            //优雅退出，释放NIO线程组
            group.shutdownGracefully() ;
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 8080 ;
        System.out.println("common is start ...");
        new TimeClient().connect(port,"127.0.0.1");
    }
}
