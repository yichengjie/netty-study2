package com.yicj.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

public class LongHeaderFrameDecoder2 extends ReplayingDecoder<Void> {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext,
                          ByteBuf buf, List<Object> list) throws Exception {
        //读取head的值，例如6，说明body的长度是6个字节
        int length = buf.readInt() ;
        //读取6个长度的body
        list.add(buf.readBytes(length)) ;
    }
}
