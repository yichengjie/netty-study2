package com.yicj.chapter11;

import com.yicj.chapter11.codec.entity.OrderFacoty;
import com.yicj.chapter11.codec.entity.HttpXmlRequest;
import com.yicj.chapter11.codec.entity.HttpXmlResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpXmlClientHandler extends SimpleChannelInboundHandler<HttpXmlResponse> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        HttpXmlRequest request = new HttpXmlRequest(null, OrderFacoty.create(123)) ;
        ctx.writeAndFlush(request) ;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpXmlResponse msg) throws Exception {
        log.info("The common receive response of http header is : {}", msg.getResponse().headers().names());
        log.info("The common receive response of http body is : {}", msg.getResult());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close() ;
    }
}
