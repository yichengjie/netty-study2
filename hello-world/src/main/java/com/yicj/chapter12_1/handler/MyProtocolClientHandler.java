package com.yicj.chapter12_1.handler;

import com.yicj.chapter12_1.entity.MyProtocolBean;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class MyProtocolClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        byte type = (byte) 0xAB ;
        byte flag = (byte) 0xCD ;
        int length = "Hello,Netty".length();
        String body = "Hello,Netty";
        MyProtocolBean bean = new MyProtocolBean(type, flag, length, body);
        ctx.writeAndFlush(bean);
    }
}