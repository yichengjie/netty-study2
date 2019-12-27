package com.yicj.chapter11_2.codec.server;

import com.yicj.chapter11_2.codec.AbstractHttpJsonEncoder;
import com.yicj.chapter11_2.codec.entity.HttpJsonResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import java.util.List;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;

public class HttpJsonResponseEncoder extends AbstractHttpJsonEncoder<HttpJsonResponse> {

    @Override
    protected void encode(ChannelHandlerContext ctx, HttpJsonResponse msg, List<Object> list) throws Exception {
        ByteBuf body = encode0(msg.getResult()) ;
        FullHttpResponse response = msg.getResponse() ;
        if(response == null){
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, body) ;
        }else {
            response = new DefaultFullHttpResponse(msg.getResponse().protocolVersion(),msg.getResponse().status(),body) ;
        }
        response.headers().set(CONTENT_TYPE,"text/xml") ;
        HttpHeaderUtil.setContentLength(response,body.readableBytes());
        list.add(response) ;
    }
}
