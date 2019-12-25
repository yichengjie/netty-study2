package com.yicj.chapter10_1;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

public class HttpHelloServer {
    public void bind(final int port) throws InterruptedException {
        //配置服务端的NIO线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap() ;
            b.group(bossGroup,workGroup) ;
            b.channel(NioServerSocketChannel.class) ;
            b.option(ChannelOption.SO_BACKLOG,100) ;
            b.handler(new LoggingHandler(LogLevel.INFO)) ;
            b.childHandler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline p = ch.pipeline();
                    p.addLast("http-decoder",new HttpRequestDecoder()) ;
                    p.addLast("http-aggregator",new HttpObjectAggregator(65536)) ;
                    p.addLast("http-encoder",new HttpResponseEncoder()) ;
                    p.addLast("http-chunked",new ChunkedWriteHandler()) ;
                    p.addLast("fileServerHandler",new HttpHelloServerHandler()) ;
                }
            }) ;
            //绑定端口，同步等待成功
            ChannelFuture f = b.bind(port).sync();
            //同步等待服务端监听端口关闭
            f.channel().closeFuture().sync() ;
        }finally {
            //优雅退出，释放线程池资源
            bossGroup.shutdownGracefully() ;
            workGroup.shutdownGracefully() ;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        int port = 8080 ;
        new HttpHelloServer().bind(port);
    }
}
