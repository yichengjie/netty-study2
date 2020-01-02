package com.yicj.udp;

import com.yicj.udp.entity.LogEvent;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogEventHandler extends SimpleChannelInboundHandler<LogEvent> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LogEvent msg) throws Exception {
        StringBuilder builder = new StringBuilder() ;
        builder.append(msg.getReceived()) ;
        builder.append(" [") ;
        builder.append(msg.getSource().toString()) ;
        builder.append("] [") ;
        builder.append(msg.getLogfile()) ;
        builder.append("] :") ;
        builder.append(msg.getMsg()) ;
        log.info(builder.toString());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("error : ", cause);
        ctx.close() ;
    }
}
