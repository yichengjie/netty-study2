package com.yicj.chapter10_4.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

//处理文本帧
public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private final ChannelGroup group ;

    public TextWebSocketFrameHandler(ChannelGroup group){
        this.group = group ;
    }

    //重写userEventTriggered处理自定义事件
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        //如果握手成功，则从这个pipeline中移除HttpRequestHandler，因为将不会接受任何http详细了
        if(evt == WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE){
            ctx.pipeline().remove(HttpRequestHandler.class) ;
            group.writeAndFlush(new TextWebSocketFrame(
                "Client " + ctx.channel() + " joined"
            )) ;
            group.add(ctx.channel()) ;
        }else {
            super.userEventTriggered(ctx,evt);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        group.writeAndFlush(msg.retain()) ;
    }
}
