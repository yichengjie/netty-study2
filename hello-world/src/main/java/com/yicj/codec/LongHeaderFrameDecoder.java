package com.yicj.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class LongHeaderFrameDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext,
                          ByteBuf buf, List<Object> list) throws Exception {
        //总字节数<4，不够Long的长度，返回
        if(buf.readableBytes() < 4){
            return;
        }
        buf.markReaderIndex() ;
        //读取head的值，例如6，说明body的长度是6个字节
        int length = buf.readInt() ;
        //body的总字节数不够6，返回
        if(buf.readableBytes() < length){
            buf.resetReaderIndex() ;
            return;
        }
        //读取6各长度的body
        list.add(buf.readBytes(length)) ;
    }
}
