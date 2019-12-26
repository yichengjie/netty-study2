package com.yicj.chapter12_1.handler;

import com.yicj.chapter12_3.entity.MyProtocolBean;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class ClientHandler extends ChannelHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        byte type = 0xA;
        byte flag = 0xC;
        int length = "Hello,Netty".length();
        String content = "Hello,Netty";
        MyProtocolBean bean = new MyProtocolBean(type, flag, length, content);
        ctx.writeAndFlush(bean);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}