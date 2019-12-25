package com.yicj.chapter12_3.codec;

import com.yicj.chapter12_1.entity.MyProtocolBean;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class MyProtocolDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> list) throws Exception {
        //在这里调用父类的方法，实现只得到想要的部分，这里全部都想要，也可以只要body部分
        //读取type字段
        byte type = msg.readByte() ;
        //读取flag字段
        byte flag = msg.readByte() ;
        //读取length字段
        int length = msg.readInt() ;
        if(msg.readableBytes() != length){
            throw new Exception("标记的长度不符合实际长度") ;
        }
        //读取body
        byte [] bytes = new byte[msg.readableBytes()] ;
        msg.readBytes(bytes) ;
        MyProtocolBean bean = new MyProtocolBean(type, flag, length, new String(bytes, "UTF-8"));
        list.add(bean) ;
    }
}
