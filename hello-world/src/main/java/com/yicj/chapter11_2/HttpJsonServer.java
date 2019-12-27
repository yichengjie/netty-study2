package com.yicj.chapter11_2;

import com.yicj.chapter11.entity.Order;
import com.yicj.chapter11_2.codec.server.HttpJsonRequestDecoder;
import com.yicj.chapter11_2.codec.server.HttpJsonResponseEncoder;
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

public class HttpJsonServer {

    public static void main(String[] args) throws InterruptedException {
        new HttpJsonServer().bind(8080);
    }

    public void bind(int port) throws InterruptedException {
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
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline p = ch.pipeline();
                    p.addLast("http-decoder",new HttpRequestDecoder()) ;
                    p.addLast("http-aggregator",new HttpObjectAggregator(65536)) ;
                    p.addLast("json-decoder",new HttpJsonRequestDecoder(Order.class,true)) ;
                    //
                    p.addLast("http-encoder",new HttpResponseEncoder()) ;
                    p.addLast("json-encoder",new HttpJsonResponseEncoder()) ;
                    p.addLast("json-serverHandler", new HttpJsonServerHandler()) ;
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
}
