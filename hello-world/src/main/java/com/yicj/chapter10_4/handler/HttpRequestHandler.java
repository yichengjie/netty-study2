package com.yicj.chapter10_4.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;

import java.io.File;
import java.net.URL;


public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    //private final String wsUri ;
    //private static final File INDEX ;

    static {
        URL location = HttpRequestHandler.class.getProtectionDomain()
                .getCodeSource().getLocation() ;
       /* try {
            String path = location.toURI() +"index.html" ;
        }*/
    }


    public HttpRequestHandler(){

    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest o) throws Exception {


    }
}
