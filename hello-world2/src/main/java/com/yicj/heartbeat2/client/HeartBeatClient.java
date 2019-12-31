package com.yicj.heartbeat2.client;

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
import io.netty.util.HashedWheelTimer;
import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.TimeUnit;

@Slf4j
public class HeartBeatClient {
    protected final HashedWheelTimer timer = new HashedWheelTimer() ;
    private final ConnectorIdleStateTrigger idleStateTrigger = new ConnectorIdleStateTrigger() ;
    Bootstrap boot = null ;
    public static void main(String[] args) throws InterruptedException {
        new HeartBeatClient().connect(8080,"127.0.0.1");
    }
    public void connect(int port, String host) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        boot = new Bootstrap() ;
        boot.group(group) ;
        boot.channel(NioSocketChannel.class) ;
        boot.option(ChannelOption.TCP_NODELAY,true) ;
        boot.handler(new LoggingHandler(LogLevel.INFO)) ;
        final ConnectionWatchdog watchdog = new ConnectionWatchdog(boot,timer,port,host,true){
            @Override
            public ChannelHandler[] handlers() {
                return new ChannelHandler[]{
                    this,
                    new IdleStateHandler(0,4,0,TimeUnit.SECONDS),
                    idleStateTrigger,
                    new StringDecoder(),
                    new StringEncoder(),
                    new HeartBeatClientHandler()
                };
            }
        } ;
        ChannelFuture future ;
        //进行连接
        synchronized (boot){
            boot.handler(new ChannelInitializer<Channel>() {
                //初始化channel
                @Override
                protected void initChannel(Channel ch) throws Exception {
                    ch.pipeline().addLast(watchdog.handlers()) ;
                }
            }) ;
            future = boot.connect(host,port) ;
        }
        //以下代码在synchronized同步块外面是安全的
        future.sync() ;
    }
}
