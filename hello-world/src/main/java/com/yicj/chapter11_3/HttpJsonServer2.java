package com.yicj.chapter11_3;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.yicj.chapter11_2.codec.server.HttpJsonResponseEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class HttpJsonServer2 {

    public static void main(String[] args) throws InterruptedException {
        //重复引用的全局配置关闭解决方式
        JSON.DEFAULT_GENERATE_FEATURE |= SerializerFeature.DisableCircularReferenceDetect.getMask();
        new HttpJsonServer2().bind(8080);
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
                    p.addLast("http-decoder",new HttpServerCodec()) ;
                    p.addLast("http-aggregator",new HttpObjectAggregator(65536)) ;
                    //出站消息
                    p.addLast("json-encoder",new HttpJsonResponseEncoder()) ;
                    p.addLast("json-serverHandler", new HttpJsonServerHandler2()) ;
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
