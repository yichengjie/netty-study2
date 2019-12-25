package com.yicj.chapter12_4.handler;

import com.yicj.chapter12_4.entity.CustomMsg;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class CustomClientHandler extends ChannelHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        byte type = (byte) 0xAB ;
        byte flag = (byte) 0xCD ;
        int length = "Hello,Netty".length() ;
        String body = "Hello,Netty" ;
        CustomMsg customMsg = new CustomMsg(type,flag,length,body);
        ctx.writeAndFlush(customMsg);
    }

}