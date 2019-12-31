package com.yicj.heartbeat2;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HeartBeatServerHandler extends SimpleChannelInboundHandler {
    private int lossConnectTime = 0 ;


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent)evt ;
            if(((IdleStateEvent) evt).state() == IdleState.READER_IDLE){
                lossConnectTime ++ ;
                log.info("5秒没有接受到客户端的消息了");
                if(lossConnectTime > 2){
                    log.info("关闭这个不活跃的channel");
                    ctx.channel().close() ;
                }
            }
        }else {
            super.userEventTriggered(ctx,evt) ;
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("server channelRead...");
        log.info("{} -> Server : {}",ctx.channel().remoteAddress() , msg.toString());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("error : " , cause);
        ctx.close() ;
    }
}
