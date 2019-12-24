package com.yicj.chapter11.codec.client;

import com.yicj.chapter11.codec.AbstractHttpXmlEncoder;
import com.yicj.chapter11.codec.entity.HttpXmlRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;

import java.net.InetAddress;
import java.util.List;

public class HttpXmlRequestEncoder extends AbstractHttpXmlEncoder<HttpXmlRequest> {

    @Override
    protected void encode(ChannelHandlerContext ctx, HttpXmlRequest msg, List<Object> list) throws Exception {
        ByteBuf body = encode0(ctx,msg.getBody()) ;
        FullHttpRequest request = msg.getRequest() ;
        if(request == null){
            request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1,
                    HttpMethod.GET,"/do", body) ;
            HttpHeaders headers = request.headers() ;
            InetAddress localHost = InetAddress.getLocalHost();
            headers.set(HttpHeaderNames.HOST,localHost.getHostAddress()) ;
            headers.set(HttpHeaderNames.CONNECTION,HttpHeaderValues.CLOSE) ;
            headers.set(HttpHeaderNames.ACCEPT_ENCODING,HttpHeaderValues.GZIP.toString()+","
                + HttpHeaderValues.DEFLATE.toString()) ;
            headers.set(HttpHeaderNames.ACCEPT_CHARSET,"ISO-8859-1,utf-8,q=0.7,*;q=0.7") ;
            headers.set(HttpHeaderNames.ACCEPT_LANGUAGE,"zh") ;
            headers.set(HttpHeaderNames.USER_AGENT,"Netty xml Http Client side") ;
            String accept = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8" ;
            headers.set(HttpHeaderNames.ACCEPT,accept) ;
        }
        HttpHeaderUtil.setContentLength(request,body.readableBytes());
        list.add(request) ;
    }
}
