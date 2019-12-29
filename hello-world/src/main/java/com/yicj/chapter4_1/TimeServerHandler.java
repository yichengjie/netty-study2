package com.yicj.chapter4_1;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import java.util.Date;

@Slf4j
public class TimeServerHandler extends ChannelInboundHandlerAdapter {
    String lineSeparator = System.getProperty("line.separator");
    private int counter ;

    public static void main(String[] args) {
        String lineSeparator = System.getProperty("line.separator");
        System.out.println("["+lineSeparator+"], " + lineSeparator.length());
        System.out.println("\r\n".equals(lineSeparator));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("TimeServerHandler.channelRead called ...");
        String body = (String) msg ;
        //删除报文结尾的换行符
        log.info("The time server receive order : " + body +" ; the counter is : " + (++counter));
        String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ?
                new Date(System.currentTimeMillis()).toString() : "BAD ORDER";
        currentTime = currentTime + lineSeparator ;
        ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes()) ;
        ctx.write(resp) ;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush() ;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.warn("Unexpected exception from downstream : {}", cause.getMessage());
        ctx.close() ;
    }
}
