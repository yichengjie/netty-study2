package com.yicj.chapter11_2.codec.client;

import com.yicj.chapter11.codec.entity.HttpXmlResponse;
import com.yicj.chapter11_2.codec.AbstractHttpJsonDecoder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

//client端使用
//注意这里千万不要讲泛型的FullHttpResponse写成DefaultFullHttpResponse了，
//否则这个Decoder不会被调用，这里是真实为AggregatedFullHttpResponse
@Slf4j
public class HttpJsonResponseDecoder extends AbstractHttpJsonDecoder<FullHttpResponse> {
    private Class<?> clazz ;
    public HttpJsonResponseDecoder(Class<?> clazz){
        this(clazz,false) ;
    }
    public HttpJsonResponseDecoder(Class<?> clazz,boolean isPrint){
        super(isPrint);
        this.clazz = clazz ;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, FullHttpResponse msg, List<Object> list) throws Exception {
        log.info("common decode : ==============> {}", msg.getClass().getName() );
        HttpXmlResponse response = new HttpXmlResponse(msg,decode0(clazz,msg.content())) ;
        list.add(response) ;
    }
}
