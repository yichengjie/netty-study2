package com.yicj.chapter11_3;

import com.yicj.chapter11.codec.entity.OrderFacoty;
import com.yicj.chapter11.entity.Order;
import com.yicj.chapter11_2.codec.entity.HttpJsonResponse;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class HttpJsonServerHandler2 extends SimpleChannelInboundHandler<FullHttpRequest> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
            log.info("req info : {}", request);
            Order order = OrderFacoty.create(123);
            ChannelFuture future = ctx.writeAndFlush(new HttpJsonResponse(null, order));
            if(!HttpUtil.isKeepAlive(request)){
                future.addListener(ChannelFutureListener.CLOSE) ;
            }
        }
    }