package com.yicj.chapter9;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.TooLongFrameException;

import java.util.List;

public class FrameChunkDecoder extends ByteToMessageDecoder {
    private final int maxFrameSize ;

    //指定将要产生的帧的最大允许大小
    public FrameChunkDecoder(int maxFrameSize){
        this.maxFrameSize = maxFrameSize ;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx,
                          ByteBuf in, List<Object> out) throws Exception {
        int readableBytes = in.readableBytes();
        //如果该帧太大，则丢弃他并抛出一个TooLongFrameException...
        if(readableBytes > maxFrameSize){//discard the bytes
            in.clear() ;
            throw new TooLongFrameException();
        }
        //否则，从ByteBuf中读取一个新的帧
        ByteBuf buf = in.readBytes(readableBytes) ;
        //将该帧添加到解码消息的List中
        out.add(buf) ;
    }
}
