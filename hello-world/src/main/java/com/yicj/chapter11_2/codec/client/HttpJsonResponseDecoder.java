package com.yicj.chapter11_2.codec.client;

import com.yicj.chapter11.codec.entity.HttpXmlResponse;
import com.yicj.chapter11_2.codec.AbstractHttpJsonDecoder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import java.util.List;

//client端使用
public class HttpJsonResponseDecoder extends AbstractHttpJsonDecoder<DefaultFullHttpResponse> {

    public HttpJsonResponseDecoder(boolean isPrint){
        super(isPrint);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, DefaultFullHttpResponse msg, List<Object> list) throws Exception {
        HttpXmlResponse response = new HttpXmlResponse(msg,decode0(msg.content())) ;
        list.add(response) ;
    }
}
