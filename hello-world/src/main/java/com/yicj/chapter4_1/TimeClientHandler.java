package com.yicj.chapter4_1;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TimeClientHandler extends ChannelHandlerAdapter {
    private int counter ;
    private String lineSeparator = System.getProperty("line.separator");
    private byte [] req  ;

    public TimeClientHandler(){
        String msg = "QUERY TIME ORDER" + lineSeparator ;
        req = msg.getBytes();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("method channelInactive called ...");
        ByteBuf message ;
        for (int i = 0 ; i < 100 ; i++){
            message = Unpooled.buffer(req.length) ;
            message.writeBytes(req) ;
            ctx.writeAndFlush(message) ;
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("TimeClientHandler.channelRead called ...");
        String body = (String) msg ;
        log.info("Now is : " + body +" ; the counter is : " + (++ counter));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.warn("Unexpected exception from downstream : {}", cause.getMessage());
        ctx.close() ;
    }
}
