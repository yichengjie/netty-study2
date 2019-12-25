package com.yicj.chapter12_3.handler;

import com.yicj.chapter12_3.entity.MyProtocolBean;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class MyProtocolClientHandler extends ChannelHandlerAdapter {
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