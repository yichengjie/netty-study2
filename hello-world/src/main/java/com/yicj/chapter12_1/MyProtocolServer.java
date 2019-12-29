package com.yicj.chapter12_1;

import com.yicj.chapter12_1.codec.MyProtocolDecoder;
import com.yicj.chapter12_1.handler.MyProtocolServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
public class MyProtocolServer {

    private static final int MAX_FRAME_LENGTH = 1024 * 1024 ;//最大长度
    private static final int LENGTH_FIELD_OFFSET = 2 ; // 长度偏移
    private static final int LENGTH_FIELD_LENGTH = 4 ; //长度字段所占的字节数
    private static final int LENGTH_ADJUSTMENT = 0 ;
    private static final int INITIAL_BYTES_TO_STRIP = 0 ;

    public void bind(int port) throws InterruptedException {
        //配置服务端的NIO线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap() ;
            b.group(bossGroup,workGroup) ;
            b.channel(NioServerSocketChannel.class) ;
            b.option(ChannelOption.SO_BACKLOG,128) ;
            b.childOption(ChannelOption.SO_KEEPALIVE, true);
            b.localAddress(new InetSocketAddress(port));
            b.handler(new LoggingHandler(LogLevel.INFO)) ;
            b.childHandler(new ChannelInitializer<SocketChannel>(){
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    MyProtocolDecoder decoder = new MyProtocolDecoder(MAX_FRAME_LENGTH,LENGTH_FIELD_OFFSET,
                            LENGTH_FIELD_LENGTH,LENGTH_ADJUSTMENT,INITIAL_BYTES_TO_STRIP,false) ;
                    pipeline.addLast(decoder) ;
                    pipeline.addLast(new MyProtocolServerHandler()) ;

                }
            }) ;
            //绑定端口，同步等待成功
            ChannelFuture f = b.bind(port).sync();
            System.out.println("Server start listen at " + port);
            f.channel().closeFuture().sync() ;
        }finally {
            //优雅退出，释放线程池资源
            bossGroup.shutdownGracefully() ;
            workGroup.shutdownGracefully() ;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        int port = 8080 ;
        new MyProtocolServer().bind(port);
    }
}
