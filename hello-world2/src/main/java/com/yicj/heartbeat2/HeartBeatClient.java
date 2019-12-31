package com.yicj.heartbeat2;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.TimeUnit;

@Slf4j
public class HeartBeatClient {
    public static void main(String[] args) throws InterruptedException {
        new HeartBeatClient().connect(8080,"127.0.0.1");
    }
    public void connect(int port, String host) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        ChannelFuture f = null ;
        try {
            Bootstrap b = new Bootstrap() ;
            b.group(group) ;
            b.channel(NioSocketChannel.class) ;
            b.option(ChannelOption.TCP_NODELAY,true) ;
            b.handler(new LoggingHandler(LogLevel.INFO)) ;
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline p = ch.pipeline();
                    p.addLast("ping", new IdleStateHandler(0,
                            4, 0, TimeUnit.SECONDS)) ;
                    p.addLast("decoder", new StringDecoder()) ;
                    p.addLast("encoder",new StringEncoder()) ;
                    p.addLast(new HeartBeatClientHandler()) ;
                }
            }) ;
            f = b.connect(host, port).sync();
            f.channel().closeFuture().sync();
        }finally {
            //group.shutdownGracefully() ;
            if(f != null){
                if(f.channel() != null && f.channel().isOpen()){
                    f.channel().close() ;
                }
            }
            log.info("准备重连");
            connect(port,host);
            log.info("重连成功!");
        }
    }


}
