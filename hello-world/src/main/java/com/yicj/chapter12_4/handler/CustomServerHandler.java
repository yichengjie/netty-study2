package com.yicj.chapter12_4.handler;

import com.yicj.chapter12_4.entity.CustomMsg;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomServerHandler extends SimpleChannelInboundHandler<Object> {
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof CustomMsg) {
            CustomMsg customMsg = (CustomMsg) msg;
            log.info("Client->Server:" + ctx.channel().remoteAddress() + " send {}" , customMsg.getBody());
        }
    }
}