package com.yicj.chapter5;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EchoClientHandler extends ChannelHandlerAdapter {
    private int counter = 0 ;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        for(int i = 0 ; i < 10 ; i++){
            String msg = "Hi, yicj. Welcom to Netty." + Constants.END_STR ;
            ByteBuf buf = Unpooled.copiedBuffer(msg.getBytes());
            ctx.writeAndFlush(buf) ;
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("EchoClientHandler.channelRead called ...");
        String body = (String) msg ;
        log.info("Now is : " + body +" ; the counter is : " + (++ counter));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.warn("Unexpected exception from downstream : {}", cause.getMessage());
        ctx.close() ;
    }

  /*  @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String body = (String) msg ;
        log.info("This is " + (++counter)
                +" times receive server : ["+body+"]");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush() ;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("===> ",cause) ;
        ctx.close() ;
    }*/
}
