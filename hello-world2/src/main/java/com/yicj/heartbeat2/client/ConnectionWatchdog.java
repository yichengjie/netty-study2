package com.yicj.heartbeat2.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public abstract class ConnectionWatchdog extends ChannelInboundHandlerAdapter implements TimerTask, ChannelHandlerHolder {
    private final Bootstrap bootstrap ;
    private final Timer timer ;
    private final int port ;
    private final String host ;
    private volatile boolean reconnect = true;
    private int attempts ;

    public ConnectionWatchdog(Bootstrap bootstrap, Timer timer,
                              int port,String host, boolean reconnect){
        this.bootstrap = bootstrap ;
        this.timer = timer ;
        this.port = port ;
        this.host = host ;
        this.reconnect = reconnect ;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("当前链路已经激活，重连尝试次数重置为0");
        attempts = 0 ;
        ctx.fireChannelActive() ;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("链路关闭");
        if(reconnect){
            log.info("链路关闭，将进行重连");
            if(attempts < 12){
                attempts ++ ;
                //重连时间间隔时间会越来越长
                int timeout = 2<< attempts ;
                timer.newTimeout(this,timeout, TimeUnit.MILLISECONDS) ;
            }
        }
        ctx.fireChannelInactive() ;
    }

    @Override
    public void run(Timeout timeout) throws Exception {
        ChannelFuture future ;
        //bootstrap已经初始化好了，只需要将handler填入就可以了
        synchronized (bootstrap){
            bootstrap.handler(new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel ch) throws Exception {
                    ChannelPipeline p = ch.pipeline();
                    p.addLast(handlers()) ;
                }
            }) ;
            future = bootstrap.connect(host,port) ;
        }
        //future对象
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture f) throws Exception {
                boolean succeed = f.isSuccess() ;
                //如果重连失败，则调用channelInactive方法,再次触发重连事件，
                // 一直尝试12次，如果失败则不再重连
                if(!succeed){
                    log.info("重连失败");
                    f.channel().pipeline().fireChannelInactive() ;
                }else {
                    log.info("重连成功");
                }
            }
        }) ;
    }
}
