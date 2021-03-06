package com.yicj.udp.codec;

import com.yicj.udp.entity.LogEvent;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;

@Slf4j
public class LogEventEncoder extends MessageToMessageEncoder<LogEvent> {
    private final InetSocketAddress remoteAddress ;

    public LogEventEncoder(InetSocketAddress remoteAddress){
        this.remoteAddress = remoteAddress ;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx,
                          LogEvent logEvent, List<Object> out) throws Exception {
        byte[] file = logEvent.getLogfile().getBytes(CharsetUtil.UTF_8);
        byte[] msg = logEvent.getMsg().getBytes(CharsetUtil.UTF_8) ;
        //log.info("msg : {}",logEvent.getMsg());
        ByteBuf buf = ctx.alloc().buffer(file.length + msg.length +1) ;
        buf.writeBytes(file) ;
        buf.writeByte(LogEvent.SEPARATOR) ;
        buf.writeBytes(msg) ;
        //log.info("server length : {}", buf.readableBytes());
        out.add(new DatagramPacket(buf,remoteAddress)) ;
    }

}
