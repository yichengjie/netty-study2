package com.yicj.chapter12.handler;

import com.yicj.chapter12.entity.Header;
import com.yicj.chapter12.entity.MessageType;
import com.yicj.chapter12.entity.NettyMessage;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class LoginAuthRespHandler extends ChannelHandlerAdapter {
    private Map<String,Boolean> nodeCheck = new ConcurrentHashMap<>() ;
    private String [] whiteList = {"127.0.0.1","192.168.1.104"} ;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage) msg ;
        //如果是握手请求消息则处理，其他消息传递
        if(message.getHeader() != null
                && message.getHeader().getType() == MessageType.LOGIN_REQ.value()){
            String nodeIndex = ctx.channel().remoteAddress().toString() ;
            NettyMessage loginResp = null ;
            //重复登录，拒绝
            if(nodeCheck.containsKey(nodeIndex)){
                loginResp = buildResopnse((byte) -1) ;
            }else {
                InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress() ;
                String ip = address.getAddress().getHostAddress();
                boolean isOk = false ;
                for(String wip : whiteList){
                    if(wip.equals(ip)){
                        isOk = true ;
                        break;
                    }
                }
                loginResp = isOk ? buildResopnse((byte)0): buildResopnse((byte) -1) ;
                if(isOk){
                    nodeCheck.put(nodeIndex,true) ;
                }
            }
            log.info("The login response is : " + loginResp + " body [{}]",loginResp.getBody());
            ctx.writeAndFlush(loginResp) ;
        }else {
            ctx.fireChannelRead(msg) ;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        nodeCheck.remove(ctx.channel().remoteAddress().toString()) ; //删除缓存
        ctx.close() ;
        ctx.fireExceptionCaught(cause) ;
    }

    private NettyMessage buildResopnse(byte result) {
        NettyMessage message = new NettyMessage() ;
        Header header = new Header() ;
        header.setType(MessageType.LOGIN_RESP.value());
        message.setHeader(header);
        message.setBody(result);
        return message ;
    }
}
