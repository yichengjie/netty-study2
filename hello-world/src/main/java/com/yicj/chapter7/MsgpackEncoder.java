package com.yicj.chapter7;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;
import org.msgpack.MessagePack;

//将Object类型的POJO对象编码为byte数组，然后写入到ByteBuf中
@Slf4j
public class MsgpackEncoder extends MessageToByteEncoder<Object> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        MessagePack msgpack = new MessagePack() ;
        byte[] bytes = msgpack.write(o);
        byteBuf.writeBytes(bytes) ;
    }
}
