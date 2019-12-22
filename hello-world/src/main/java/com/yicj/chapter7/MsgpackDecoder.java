package com.yicj.chapter7;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.msgpack.MessagePack;
import org.msgpack.type.Value;

import java.util.List;

public class MsgpackDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext,
                          ByteBuf byteBuf, List<Object> list) throws Exception {
        //1.从byteBuf中获取需要解码的byte数组
        final byte [] array ;
        final int length = byteBuf.readableBytes() ;
        array = new byte[length] ;
        byteBuf.getBytes(byteBuf.readerIndex(),array,0,length) ;
        MessagePack msgpack = new MessagePack() ;
        //2.调用read方法将其反序列化为Object对象，将解码后的对象
        Value readObj = msgpack.read(array);
        //3.将反序列化后的对象加入到解码列表list中，这样就完成了MessagePack的解码操作
        list.add(readObj) ;
    }
}
