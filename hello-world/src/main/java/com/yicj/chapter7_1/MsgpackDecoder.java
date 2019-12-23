package com.yicj.chapter7_1;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.extern.slf4j.Slf4j;
import org.msgpack.MessagePack;

import java.util.List;

@Slf4j
public class MsgpackDecoder extends MessageToMessageDecoder<ByteBuf> {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext,
                          ByteBuf byteBuf, List<Object> list) throws Exception {
        //1.从byteBuf中获取需要解码的byte数组
        final byte [] bytes ;
        final int length = byteBuf.readableBytes() ;
        bytes = new byte[length] ;
        byteBuf.getBytes(byteBuf.readerIndex(),bytes,0,length) ;
        MessagePack msgpack = new MessagePack() ;
        //2.调用read方法将其反序列化为Object对象，将解码后的对象
        list.add(msgpack.read(bytes)) ;
        //3.将反序列化后的对象加入到解码列表list中，这样就完成了MessagePack的解码操作

    }
}
