package com.yicj.chapter3;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TimeClientHandler extends ChannelHandlerAdapter {

    private final ByteBuf firstMessage ;

    public TimeClientHandler(){
        byte [] req = "QUERY TIME ORDER".getBytes() ;
        firstMessage = Unpooled.buffer(req.length) ;
        firstMessage.writeBytes(req);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("method channelInactive called ...");
        ctx.writeAndFlush(firstMessage) ;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("TimeClientHandler.channelRead called ...");
        ByteBuf buf = (ByteBuf) msg ;
        byte [] req = new byte[buf.readableBytes()] ;
        buf.readBytes(req) ;
        String body = new String(req,"UTF-8") ;
        log.info("Now is : " + body);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.warn("Unexpected exception from downstream : {}", cause.getMessage());
        ctx.close() ;
    }
}
