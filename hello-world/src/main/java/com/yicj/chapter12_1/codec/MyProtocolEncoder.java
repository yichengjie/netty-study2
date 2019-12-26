package com.yicj.chapter12_1.codec;

import com.yicj.chapter12_1.entity.MyProtocolBean;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

@Slf4j
public class MyProtocolEncoder extends MessageToByteEncoder<MyProtocolBean> {

    @Override
    protected void encode(ChannelHandlerContext ctx, MyProtocolBean msg, ByteBuf out) throws Exception {
        if(msg == null){
            throw new Exception("msg is null") ;
        }
        log.info("============> {}" , msg);
        out.writeByte(msg.getType()) ;
        out.writeByte(msg.getFlag()) ;
        out.writeInt(msg.getLength()) ;
        out.writeBytes(msg.getContent().getBytes(Charset.forName("UTF-8"))) ;
    }
}
