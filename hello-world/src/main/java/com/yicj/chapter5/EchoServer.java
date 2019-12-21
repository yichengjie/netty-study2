package com.yicj.chapter5;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
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
            b.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ByteBuf delimiter = Unpooled.copiedBuffer(Constants.END_STR.getBytes()) ;
                    //Decoder为inBound，先加入的先执行
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast(new DelimiterBasedFrameDecoder(1024,delimiter)) ;
                    pipeline.addLast(new StringDecoder()) ;
                    pipeline.addLast(new EchoServerHandler()) ;
                }
            }) ;
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

    public static void main(String[] args) throws InterruptedException {
        int port = 8080 ;
        new EchoServer().bind(port);
    }
}
