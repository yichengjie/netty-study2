package com.yicj.chapter11_2.codec;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.MessageToMessageEncoder;
import java.nio.charset.Charset;

//client使用
public abstract class AbstractHttpJsonEncoder<T> extends MessageToMessageEncoder<T> {
    final static String CHARSET_NAME = "UTF-8" ;
    final static Charset UTF_8 = Charset.forName(CHARSET_NAME) ;

    //将对象转为json
    protected ByteBuf encode0(Object body) throws Exception {
        String jsonStr = JSON.toJSONString(body);
        ByteBuf encodeBuf = Unpooled.copiedBuffer(jsonStr,UTF_8) ;
        return encodeBuf ;
    }
}
