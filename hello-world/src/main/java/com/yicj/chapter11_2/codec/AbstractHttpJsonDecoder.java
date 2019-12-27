package com.yicj.chapter11_2.codec;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.extern.slf4j.Slf4j;
import java.nio.charset.Charset;

@Slf4j
public abstract class AbstractHttpJsonDecoder<T> extends MessageToMessageDecoder<T> {
    private boolean isPrint ;
    private final static String CHARSET_NAME = "UTF-8";
    private final static Charset UTF_8 = Charset.forName(CHARSET_NAME) ;

    protected AbstractHttpJsonDecoder(boolean isPrint){
        this.isPrint = isPrint ;
    }

    //从buf中转为对象
    protected Object decode0(ByteBuf body) throws Exception {
        String content = body.toString(UTF_8) ;
        if(isPrint){
            log.info("The body is : " + content);
        }
        Object result = JSON.parse(content);
        return result ;
    }
}
