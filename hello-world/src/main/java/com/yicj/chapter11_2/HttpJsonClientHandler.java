package com.yicj.chapter11_2;

import com.yicj.chapter11.codec.entity.OrderFacoty;
import com.yicj.chapter11_2.codec.entity.HttpJsonRequest;
import com.yicj.chapter11_2.codec.entity.HttpJsonResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpJsonClientHandler extends SimpleChannelInboundHandler<HttpJsonResponse> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        HttpJsonRequest request = new HttpJsonRequest(null, OrderFacoty.create(123)) ;
        ctx.writeAndFlush(request) ;
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, HttpJsonResponse msg) throws Exception {
        log.info("The client receive response of http header is : {}", msg.getResponse().headers().names());
        log.info("The client receive response of http body is : {}", msg.getResult());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close() ;
    }
}
