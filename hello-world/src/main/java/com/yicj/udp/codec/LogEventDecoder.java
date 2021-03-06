package com.yicj.udp.codec;

import com.yicj.udp.entity.LogEvent;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class LogEventDecoder extends MessageToMessageDecoder<DatagramPacket> {
    @Override
    protected void decode(ChannelHandlerContext ctx,
                          DatagramPacket datagramPacket, List<Object> out) throws Exception {
        ByteBuf data = datagramPacket.content() ;
        int idx = data.indexOf(0, data.readableBytes(), LogEvent.SEPARATOR) ;
        String filename = data.slice(0, idx).toString(CharsetUtil.UTF_8) ;
        String logMsg = data.slice(idx + 1, (data.readableBytes() - (idx + 1))).toString(CharsetUtil.UTF_8) ;
        LogEvent event =
                new LogEvent(datagramPacket.sender(),System.currentTimeMillis(),filename,logMsg) ;
        out.add(event) ;
    }
}
