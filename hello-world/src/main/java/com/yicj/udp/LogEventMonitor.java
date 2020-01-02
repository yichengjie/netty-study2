package com.yicj.udp;

import com.yicj.udp.codec.LogEventDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
public class LogEventMonitor {
    private final EventLoopGroup group ;
    private final Bootstrap bootstrap ;

    public LogEventMonitor(InetSocketAddress address){
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap() ;
        bootstrap.group(group) ;
        bootstrap.channel(NioDatagramChannel.class) ;
        bootstrap.option(ChannelOption.SO_BROADCAST,true) ;
        bootstrap.handler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ChannelPipeline p = ch.pipeline();
                p.addLast(new LogEventDecoder()) ;
                p.addLast(new LogEventHandler()) ;
            }
        }) ;
        bootstrap.localAddress(address) ;
    }

    public Channel bind(){
        return bootstrap.bind().syncUninterruptibly().channel() ;
    }

    public void stop(){
        group.shutdownGracefully() ;
    }

    public static void main(String[] args) throws InterruptedException {
        LogEventMonitor monitor = new LogEventMonitor(new InetSocketAddress(8080));
        try {
            Channel channel = monitor.bind() ;
            log.info("LogEventMonitor running");
            channel.closeFuture().sync() ;
        }finally {
            monitor.stop();
        }
    }
}
