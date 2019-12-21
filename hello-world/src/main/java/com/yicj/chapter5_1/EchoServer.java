package com.yicj.chapter5_1;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EchoServer {
    public void bind(int port) throws Exception{
        //配置服务端NIO线程组
        EventLoopGroup boosGroup = new NioEventLoopGroup() ;
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap() ;
            b.group(boosGroup,workGroup) ;
            b.channel(NioServerSocketChannel.class) ;
            b.option(ChannelOption.SO_BACKLOG,1024) ;
            b.childHandler(new ChildChannelHandler()) ;
            //绑定端口，同步等待成功
            ChannelFuture future = b.bind(port).sync();
            //等待服务端监听端口关闭
            future.channel().closeFuture().sync() ;
        }finally {
            //优雅退出，释放线程池资源
            boosGroup.shutdownGracefully() ;
            workGroup.shutdownGracefully() ;
        }

    }

    private class ChildChannelHandler extends ChannelInitializer<SocketChannel> {
        protected void initChannel(SocketChannel socketChannel) throws Exception {
            ChannelPipeline pipeline = socketChannel.pipeline();
            pipeline.addLast(new EchoServerHandler()) ;
        }
    }


    public static void main(String[] args) throws Exception {
        int port = 8080 ;
        System.out.println("server is start !");
        new EchoServer().bind(port);
    }
}
