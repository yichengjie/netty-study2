package com.yicj.chapter12.handler;

import com.yicj.chapter12.entity.Header;
import com.yicj.chapter12.entity.MessageType;
import com.yicj.chapter12.entity.NettyMessage;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
public class HeartBeatReqHandler extends ChannelHandlerAdapter {
    private volatile ScheduledFuture<?> scheduledFuture ;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage) msg ;
        //握手成功，主动发送心跳信息
        //当握手成功以后，握手请求Handler会继续将握手成功消息向下传递，HeartBeatReqHandler接收到之后，
        // 对消息进行判断，如果是握手成功消息，则启动无限循环定时器用于定期发送心跳消息。
        // 由于NioEvenLoop是一个Schedule，因此它支持定时器的执行，心跳单位是毫秒，默认为5000，即5秒发送一条心跳消息。
        if(message.getHeader() != null
                && message.getHeader().getType() == MessageType.LOGIN_RESP.value()){
            Runnable task = new HeartBeatReqHandler.HeartBeatTask(ctx) ;
            scheduledFuture = ctx.executor().scheduleAtFixedRate(task,0,5000, TimeUnit.MICROSECONDS) ;
        }else if(message.getHeader() != null
                && message.getHeader().getType() == MessageType.HEARTBEAT_RESP.value()){
            //为了统一在一个Handler中处理所有的心跳消息，因此这里用于接收服务端发送的心跳应答消息，
            // 并打印客户端接收和发送的心跳消息
            log.info("Client receive server heart beat message : ----> {}",message);
        }else {
            ctx.fireChannelRead(msg) ;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if(scheduledFuture != null){
            scheduledFuture.cancel(true) ;
            scheduledFuture = null ;
        }
        ctx.fireExceptionCaught(cause) ;
    }

    private class HeartBeatTask implements Runnable{
        private ChannelHandlerContext ctx ;
        private HeartBeatTask(ChannelHandlerContext ctx){
            this.ctx = ctx ;
        }
        @Override
        public void run() {
            NettyMessage heartBeat = buildHeartBeat() ;
            log.info("Client send heart beat message to server : ----> {}" , heartBeat);
            ctx.writeAndFlush(heartBeat) ;
        }
        private NettyMessage buildHeartBeat(){
            NettyMessage message = new NettyMessage() ;
            Header header = new Header() ;
            header.setType(MessageType.HEARTBEAT_REQ.value());
            message.setHeader(header);
            return message ;
        }
    }


}
