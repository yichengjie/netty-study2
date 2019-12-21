package com.yicj.chapter5_1;

import com.yicj.chapter5.Constants;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Slf4j
public class EchoServerHandler extends ChannelHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("TimeServerHandler.channelRead called ...");
        ByteBuf buf = (ByteBuf)msg ;
        byte [] req = new byte[buf.readableBytes()] ;
        buf.readBytes(req) ;
        String body = new String(req,"UTF-8") ;
        log.info("The time server receive order : " + body);
        ByteBuf resp = Unpooled.copiedBuffer(body.getBytes()) ;
        ctx.writeAndFlush(resp) ;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.warn("Unexpected exception from downstream : {}", cause.getMessage());
        ctx.close() ;
    }
}
