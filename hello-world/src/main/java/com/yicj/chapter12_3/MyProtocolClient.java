package com.yicj.chapter12_3;

import com.yicj.chapter12_3.codec.MyProtocolEncoder;
import com.yicj.chapter12_3.handler.MyProtocolClientHandler;
import com.yicj.chapter12_4.codec.CustomEncoder;
import com.yicj.chapter12_4.handler.CustomClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyProtocolClient {

    public void connect(int port, String host) throws InterruptedException {
        //配置客户端NIO线程组
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap() ;
            b.group(group) ;
            b.channel(NioSocketChannel.class) ;
            b.option(ChannelOption.TCP_NODELAY,true) ;
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline p = ch.pipeline();
                    p.addLast(new MyProtocolEncoder()) ;
                    p.addLast(new MyProtocolClientHandler()) ;
                }
            }) ;
            //发起异步连接操作，并同步等待连接成功
            ChannelFuture future = b.connect(host, port).sync();
            future.channel().writeAndFlush("Hello Netty Server ,I am a common client");
            //同步等待客户端链路关闭
            future.channel().closeFuture().sync() ;
        }finally {
            //优雅退出，释放NIO线程组
            group.shutdownGracefully();
        }
    }



    public static void main(String[] args) throws InterruptedException {
        int port = 8080 ;
        new MyProtocolClient().connect(port,"127.0.0.1");
    }
}
