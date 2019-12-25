package com.yicj.chapter12_3.handler;

import com.yicj.chapter12_3.entity.MyProtocolBean;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyProtocolServerHandler extends SimpleChannelInboundHandler<Object> {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof MyProtocolBean){
            MyProtocolBean bean = (MyProtocolBean) msg;
            log.info("Client->Server:" + ctx.channel().remoteAddress() + " send {}", bean.getBody());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("exception : ", cause);
        ctx.close();
    }
}