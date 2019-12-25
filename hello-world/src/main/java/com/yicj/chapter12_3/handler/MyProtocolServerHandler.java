package com.yicj.chapter12_3.handler;

import com.yicj.chapter12_3.entity.MyProtocolBean;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyProtocolServerHandler extends ChannelHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MyProtocolBean bean = (MyProtocolBean) msg;
        log.info("content : {}", bean.getBody());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("exception : ", cause);
        ctx.close();
    }
}