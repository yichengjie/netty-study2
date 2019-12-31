package com.yicj.heartbeat2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Slf4j
public class HeartBeatClientHandler extends ChannelInboundHandlerAdapter {
    private static final ByteBuf HEARTBEAT_SEQUENCE =
            Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Heartbeat", CharsetUtil.UTF_8)) ;
    private static final int TRY_TIMES = 3 ;
    private int currentTime = 0 ;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("激活时间是: {}",new Date());
        log.info("HeartBeatClientHandler channelActive ..");
        ctx.fireChannelActive() ;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("停止时间是 : {}", new Date());
        log.info("HeartBeatClientHandler channelInactive .. ");
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        log.info("循环触发时间 : {}",new Date());
        if(evt instanceof IdleStateEvent){
            IdleStateEvent event = IdleStateEvent.class.cast(evt) ;
            if(event.state() == IdleState.WRITER_IDLE){
                if(currentTime <= TRY_TIMES){
                    log.info("currentTime : {}" , currentTime);
                    currentTime ++ ;
                    ctx.channel().writeAndFlush(HEARTBEAT_SEQUENCE.duplicate()) ;
                }
            }
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String message = (String)msg ;
        log.info("msg : {}", msg);
        if(message.equals("Heartbeat")){
            ctx.writeAndFlush("has read message from server") ;
        }
        ReferenceCountUtil.release(msg) ;
    }
}
