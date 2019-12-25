package com.yicj.chapter12.handler;

import com.yicj.chapter12.entity.Header;
import com.yicj.chapter12.entity.MessageType;
import com.yicj.chapter12.entity.NettyMessage;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoginAuthReqHandler extends ChannelHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //当客户端与服务端TCP三次握手成功之后，由客户端构造握手请求消息发送给服务端，
        //由于采用IP白名单认证机制，因此不需要携带消息体，消息体为空，
        // 消息类型为‘3’: 握手请求消息发送之后，按照协议规范，服务端需要返回握手应答消息。
        ctx.writeAndFlush(buildLoginReq()) ;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage) msg ;
        //如果是握手应答消息，需要判断是否认证成功
        //对应答消息进行处理，首先判断消息是否是握手应答消息，如果不是，直接传递给后面的ChannelHandler进行处理，
        //如果是握手应答消息，则对应答结果进行判断，如果非0，说明认证失败，关闭链路，重新发送链接
        if (message.getHeader() != null
                && message.getHeader().getType() == MessageType.LOGIN_RESP.value()){
            byte loginResult = (byte) message.getBody() ;
            if(loginResult != (byte) 0){//握手失败，关闭连接
                ctx.close() ;
            }else {
                log.info("Login is ok : " + message);
                ctx.fireChannelRead(msg) ;
            }
        }else {
            ctx.fireChannelRead(msg) ;
        }
    }

    private NettyMessage buildLoginReq() {
        NettyMessage message  = new NettyMessage() ;
        Header header = new Header() ;
        header.setType(MessageType.LOGIN_REQ.value());
        message.setHeader(header);
        return message ;
    }
}
