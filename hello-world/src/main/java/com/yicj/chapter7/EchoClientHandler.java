package com.yicj.chapter7;

import com.yicj.entity.UserInfo;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EchoClientHandler extends ChannelHandlerAdapter {
    private int sendNumber ;
    public EchoClientHandler(int sendNumber){
        this.sendNumber = sendNumber ;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        UserInfo [] infos = initUserInfos() ;
        for (UserInfo info : infos){
            ctx.write(info) ;
        }
        ctx.flush() ;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("Client receive the msgpack message : " + msg);
        //ctx.write(msg) ;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.warn("Unexpected exception from downstream : {}", cause.getMessage());
        ctx.close() ;
    }

    private UserInfo [] initUserInfos(){
        UserInfo [] userInfos = new UserInfo[sendNumber] ;
        UserInfo userInfo = null ;
        for (int i = 0 ; i < sendNumber ; i++){
            userInfo = new UserInfo() ;
            userInfo.setAge(i);
            userInfo.setUserName("ABCDEFG ---->" + i);
            userInfos[i] = userInfo ;
        }
        return userInfos ;
    }
}
