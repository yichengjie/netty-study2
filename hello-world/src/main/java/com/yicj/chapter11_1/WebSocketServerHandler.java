package com.yicj.chapter11_1;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Slf4j
public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {
    private WebSocketServerHandshaker handshaker ;

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {
        //传统的HTTP接入
        //第一次握手请求消息由http协议承载，所以它是一个http消息，执行handleHttpRequest方法处理
        //WebSocket握手请求。
        if(FullHttpRequest.class.isInstance(msg)){
            handleHttpRequest(ctx, (FullHttpRequest) msg) ;
        }else if(WebSocketFrame.class.isInstance(msg)){//WebSocket接入
            handleWebSocketFrame(ctx, (WebSocketFrame) msg) ;
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush() ;
    }


    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
        //如果http解码失败，返回http异常
        //如果消息头中没有包含Upgrade字段，或则它的值不是websocket，则返回404
        if (!req.decoderResult().isSuccess() ||
                (!"websocket".equals(req.headers().get("Upgrade")))){
            DefaultFullHttpResponse badResp = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST);
            sendHttpResponse(ctx, req, badResp) ;
            return ;
        }
        //构造握手响应返回，本机测试
        //握手请求简单校验之后，开始构建握手工厂，构建握手响应消息返回给客户端，同时将WebSocket相关的编码和解码
        //类动态添加到ChannelPipeline中，用于WebSocket消息编解码
        String wsurl = "ws://localhost:8080/websocket" ;
        WebSocketServerHandshakerFactory wsFactory =
                new WebSocketServerHandshakerFactory(wsurl,null,false) ;
        handshaker  = wsFactory.newHandshaker(req) ;
        if(handshaker == null){
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel()) ;
        }else{
            handshaker.handshake(ctx.channel(), req) ;
        }
    }

    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        //判断是否是关闭链路指令
        if (CloseWebSocketFrame.class.isInstance(frame)){
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain()) ;
            return;
        }
        //判断是否为ping消息
        if(PingWebSocketFrame.class.isInstance(frame)){
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain())) ;
            return;
        }
        //本例程仅支持文本消息，不支持二进制消息
        if(!TextWebSocketFrame.class.isInstance(frame)){
            String formatStr = String.format("%s frame types not supported",
                    frame.getClass().getName());
            throw new UnsupportedOperationException(formatStr) ;
        }
        //返回应答消息
        String reqMsg = ((TextWebSocketFrame) frame).text() ;
        log.info("{} receive {}", ctx.channel(), reqMsg);
        TextWebSocketFrame respFrame = new TextWebSocketFrame(reqMsg
                + " , 欢迎使用Netty WebSocket服务,现在时刻: " + new Date().toString()) ;
        ctx.channel().write(respFrame) ;
    }

    private void sendHttpResponse(ChannelHandlerContext ctx,
              FullHttpRequest req, DefaultFullHttpResponse resp) {
        //返回应答给客户端
        if(resp.status().code() != 200){
            ByteBuf buf = Unpooled.copiedBuffer(resp.status().toString(), CharsetUtil.UTF_8) ;
            resp.content().writeBytes(buf) ;
            buf.release() ;
            HttpHeaderUtil.setContentLength(resp, resp.content().readableBytes());
        }
        //如果是非Keep-Alive,关闭连接
        ChannelFuture f = ctx.channel().writeAndFlush(resp);
        if (!HttpHeaderUtil.isKeepAlive(req) || resp.status().code() != 200){
            f.addListener(ChannelFutureListener.CLOSE) ;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("=====>" , cause);
        ctx.close() ;
    }
}
