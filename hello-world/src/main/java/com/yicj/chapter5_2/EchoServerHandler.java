package com.yicj.chapter5_2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EchoServerHandler extends ChannelHandlerAdapter {
    private int counter = 0 ;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String body = (String) msg ;
        log.info("This is " + (++counter) +" times receive client : ["+body+"]");
        ByteBuf echo = Unpooled.copiedBuffer(body.getBytes()) ;
        ctx.writeAndFlush(echo) ;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("exception : " , cause);
        ctx.close() ;
    }
}
