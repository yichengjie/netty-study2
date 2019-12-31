package com.yicj.idle;

import com.yicj.idle.handler.HeartBeatServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

@Slf4j
public class HeartBeatServer {

    public static void main(String[] args) throws InterruptedException {
        new HeartBeatServer().start(8080);
    }

    public void start(int port) throws InterruptedException {

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup() ;
        try {
            ServerBootstrap bootstrap = new ServerBootstrap() ;
            bootstrap.group(bossGroup,workerGroup) ;
            bootstrap.channel(NioServerSocketChannel.class) ;
            bootstrap.handler(new LoggingHandler(LogLevel.INFO)) ;
            bootstrap.localAddress(new InetSocketAddress(port)) ;
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast(
                            new IdleStateHandler(5,0,0, TimeUnit.SECONDS)) ;
                    pipeline.addLast("decoder", new StringDecoder()) ;
                    pipeline.addLast("encoder",new StringEncoder()) ;
                    pipeline.addLast(new HeartBeatServerHandler()) ;
                }
            }) ;
            log.info("Server start listen at : {}",port);
            ChannelFuture f = bootstrap.bind(port).sync();
            f.channel().closeFuture().sync() ;
        }finally {
           bossGroup.shutdownGracefully() ;
           workerGroup.shutdownGracefully() ;
        }

    }
}
