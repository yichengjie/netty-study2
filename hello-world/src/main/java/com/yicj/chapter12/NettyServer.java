package com.yicj.chapter12;

import com.yicj.chapter12.codec.NettyMessageDecoder;
import com.yicj.chapter12.codec.NettyMessageEncoder;
import com.yicj.chapter12.entity.NettyConstant;
import com.yicj.chapter12.handler.HeartBeatRespHandler;
import com.yicj.chapter12.handler.LoginAuthRespHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyServer {

    public static void main(String[] args) throws InterruptedException {
        new NettyServer().bind();
    }

    public void bind() throws InterruptedException {
        //配置服务端NIO的线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap() ;
            b.group(bossGroup,workerGroup) ;
            b.channel(NioServerSocketChannel.class) ;
            b.option(ChannelOption.SO_BACKLOG,100) ;
            b.handler(new LoggingHandler(LogLevel.INFO)) ;
            b.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline p = ch.pipeline();
                    p.addLast(new NettyMessageDecoder(1024 * 1024 , 4, 4)) ;
                    p.addLast(new NettyMessageEncoder()) ;
                    p.addLast("readTimeoutHandler", new ReadTimeoutHandler(50)) ;
                    p.addLast(new LoginAuthRespHandler()) ;
                    p.addLast("heartBeatHandler",new HeartBeatRespHandler()) ;
                }
            }) ;
            //绑定端口，同步等待成功
            ChannelFuture f = b.bind(NettyConstant.REMOTEIP, NettyConstant.PORT).sync();
            log.info("Netty server start ok :  {} : {}" , NettyConstant.REMOTEIP , NettyConstant.PORT );
            //同步等待服务端监听端口关闭
            f.channel().closeFuture().sync() ;
        }finally {
            bossGroup.shutdownGracefully() ;
            workerGroup.shutdownGracefully() ;
        }
    }
}
