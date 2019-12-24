package com.yicj.chapter11.codec.client;

import com.yicj.chapter11.codec.AbstractHttpXmlDecoder;
import com.yicj.chapter11.codec.entity.HttpXmlResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;

import java.util.List;
//client端使用
public class HttpXmlResponseDecoder extends AbstractHttpXmlDecoder<DefaultFullHttpResponse> {

    public HttpXmlResponseDecoder(Class<?> clazz){
        this(clazz,false) ;
    }
    public HttpXmlResponseDecoder(Class<?> clazz, boolean isPrint){
        super(clazz,isPrint);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, DefaultFullHttpResponse msg, List<Object> list) throws Exception {
        HttpXmlResponse response = new HttpXmlResponse(msg,decode0(ctx,msg.content())) ;
        list.add(response) ;
    }
}
