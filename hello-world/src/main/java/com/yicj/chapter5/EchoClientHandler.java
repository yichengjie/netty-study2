package com.yicj.chapter5;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EchoClientHandler extends ChannelHandlerAdapter {
    private int counter = 0 ;
    private static final String ECHO_REQ = "Hi, yicj. Welcom to Netty.$_" ;


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        for(int i = 0 ; i < 10 ; i++){
            ByteBuf msg = Unpooled.copiedBuffer(ECHO_REQ.getBytes());
            ctx.writeAndFlush(msg) ;
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("This is " + (++counter)
                +" times receive server : ["+msg+"]");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush() ;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("===> ",cause) ;
        ctx.close() ;
    }
}
