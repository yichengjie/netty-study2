package com.yicj.chapter5_1;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EchoClientHandler extends ChannelHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        for (int i= 0 ; i < 10 ; i ++){
            byte[] bytes = ("Hi, yicj. Welcom to Netty." + Constants.END_STR).getBytes();
            ByteBuf buf = Unpooled.copiedBuffer(bytes) ;
            ctx.writeAndFlush(buf) ;
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("EchoClientHandler.channelRead called ...");
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
