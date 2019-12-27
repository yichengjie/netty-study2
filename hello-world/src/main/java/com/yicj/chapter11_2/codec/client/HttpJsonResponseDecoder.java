package com.yicj.chapter11_2.codec.client;

import com.yicj.chapter11.codec.entity.HttpXmlResponse;
import com.yicj.chapter11_2.codec.AbstractHttpJsonDecoder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import java.util.List;

//client端使用
public class HttpJsonResponseDecoder extends AbstractHttpJsonDecoder<DefaultFullHttpResponse> {
    private Class<?> clazz ;
    public HttpJsonResponseDecoder(Class<?> clazz){
        this(clazz,false) ;
    }
    public HttpJsonResponseDecoder(Class<?> clazz,boolean isPrint){
        super(isPrint);
        this.clazz = clazz ;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, DefaultFullHttpResponse msg, List<Object> list) throws Exception {
        HttpXmlResponse response = new HttpXmlResponse(msg,decode0(clazz,msg.content())) ;
        list.add(response) ;
    }
}
