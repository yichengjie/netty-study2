package com.yicj.chapter4;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TimeClientHandler extends ChannelInboundHandlerAdapter {
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
        ByteBuf message = null ;
        for (int i = 0 ; i < 100 ; i++){
            message = Unpooled.buffer(req.length) ;
            message.writeBytes(req) ;
            ctx.writeAndFlush(message) ;
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("EchoClientHandler.channelRead called ...");
        ByteBuf buf = (ByteBuf) msg ;
        byte [] req = new byte[buf.readableBytes()] ;
        buf.readBytes(req) ;
        String body = new String(req,"UTF-8") ;
        log.info("Now is : " + body +" ; the counter is : " + (++ counter));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.warn("Unexpected exception from downstream : {}", cause.getMessage());
        ctx.close() ;
    }
}
