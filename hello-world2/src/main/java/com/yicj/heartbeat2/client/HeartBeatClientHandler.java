package com.yicj.heartbeat2.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import java.util.Date;

@Slf4j
public class HeartBeatClientHandler extends ChannelInboundHandlerAdapter {

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
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String message = (String)msg ;
        log.info("msg : {}", msg);
        if(message.equals("Heartbeat")){
            ctx.writeAndFlush("has read message from server") ;
        }
        ReferenceCountUtil.release(msg) ;
    }
}
