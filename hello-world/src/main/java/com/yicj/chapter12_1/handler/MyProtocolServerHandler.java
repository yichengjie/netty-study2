package com.yicj.chapter12_1.handler;

import com.yicj.chapter12_1.entity.MyProtocolBean;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyProtocolServerHandler extends SimpleChannelInboundHandler<Object> {
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof MyProtocolBean){
            MyProtocolBean bean = (MyProtocolBean) msg;
            log.info("Client->Server:" + ctx.channel().remoteAddress() + " send {}", bean.getBody());
        }
    }
}