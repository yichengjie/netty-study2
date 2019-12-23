package com.yicj.chapter7_1;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class EchoServer {

    public void bind(int port) throws InterruptedException {
        //配置服务端的NIO线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap() ;
            b.group(bossGroup,workGroup) ;
            b.channel(NioServerSocketChannel.class) ;
            b.option(ChannelOption.SO_BACKLOG,100) ;
            b.handler(new LoggingHandler(LogLevel.INFO)) ;
            b.childHandler(new ChildChannelHandler()) ;
            //绑定端口，同步等待成功
            ChannelFuture f = b.bind(port).sync();
            //同步等待服务端监听端口关闭
            f.channel().closeFuture().sync() ;
        }finally {
            //优雅退出，释放线程池资源
            bossGroup.shutdownGracefully() ;
            workGroup.shutdownGracefully() ;
        }
    }

    private class ChildChannelHandler extends ChannelInitializer<SocketChannel> {
        protected void initChannel(SocketChannel ch) throws Exception {
            ChannelPipeline p = ch.pipeline();
            //在MessagePack解码前增加LengthFieldBasedFrameDecoder，用于处理半包消息
            p.addLast("frameDecoder",new LengthFieldBasedFrameDecoder(65535,0,2,0,2)) ;
            p.addLast("decoder",new MsgpackDecoder()) ;
            //在MessagePack编码器之前增加LengthFieldPrepender
            p.addLast("frameEncoder",new LengthFieldPrepender(2)) ;
            p.addLast("encoder", new MsgpackEncoder()) ;
            p.addLast(new EchoServerHandler()) ;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        int port = 8080 ;
        new EchoServer().bind(port);
    }
}
