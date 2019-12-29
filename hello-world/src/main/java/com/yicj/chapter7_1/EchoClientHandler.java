package com.yicj.chapter7_1;

import com.yicj.chapter7.entity.UserInfo;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EchoClientHandler extends ChannelInboundHandlerAdapter {
    private int sendNumber ;
    private int count;
    public EchoClientHandler(int sendNumber){
        this.sendNumber = sendNumber ;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        UserInfo[] infos = initUserInfos() ;
        for (UserInfo info : infos){
            ctx.write(info) ;
        }
        ctx.flush() ;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("this is client receive msg【  "+ ++count +"  】times:【"+msg+"】");
        /*if(count<1000){ //控制运行次数，因为不加这个控制直接调用下面代码的话，客户端和服务端会形成闭环循环，一直运行
            ctx.write(msg);
        }*/
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
