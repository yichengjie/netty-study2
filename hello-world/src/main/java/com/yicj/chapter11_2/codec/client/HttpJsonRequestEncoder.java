package com.yicj.chapter11_2.codec.client;

import com.yicj.chapter11_2.codec.AbstractHttpJsonEncoder;
import com.yicj.chapter11_2.codec.entity.HttpJsonRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import java.net.InetAddress;
import java.util.List;

public class HttpJsonRequestEncoder extends AbstractHttpJsonEncoder<HttpJsonRequest> {

    @Override
    protected void encode(ChannelHandlerContext ctx, HttpJsonRequest msg, List<Object> list) throws Exception {
        ByteBuf body = encode0(msg.getBody()) ;
        FullHttpRequest request = msg.getRequest() ;
        if(request == null){
            request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1,
                    HttpMethod.GET,"/do", body) ;
            HttpHeaders headers = request.headers() ;
            InetAddress localHost = InetAddress.getLocalHost();
            headers.set(HttpHeaderNames.HOST,localHost.getHostAddress()) ;
            //headers.set(HttpHeaderNames.CONNECTION,HttpHeaderValues.CLOSE) ;
            headers.set(HttpHeaderNames.ACCEPT_ENCODING,HttpHeaderValues.GZIP.toString()+","
                + HttpHeaderValues.DEFLATE.toString()) ;
            headers.set(HttpHeaderNames.ACCEPT_CHARSET,"ISO-8859-1,utf-8,q=0.7,*;q=0.7") ;
            headers.set(HttpHeaderNames.ACCEPT_LANGUAGE,"zh") ;
            headers.set(HttpHeaderNames.USER_AGENT,"Netty xml Http Client side") ;
            String accept = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8" ;
            headers.set(HttpHeaderNames.ACCEPT,accept) ;
        }
        HttpUtil.setContentLength(request,body.readableBytes());
        list.add(request) ;
    }
}
