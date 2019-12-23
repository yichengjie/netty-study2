package com.yicj.chapter7;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class EchoClient {
    private final String host ;
    private final int port ;
    private final int sendNumber ;

    public EchoClient(String host, int port, int sendNumber){
        this.host = host ;
        this.port = port ;
        this.sendNumber = sendNumber ;
    }

    public void connect() throws InterruptedException {
        //配置客户端NIO线程组
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap() ;
            b.group(group) ;
            b.channel(NioSocketChannel.class) ;
            b.option(ChannelOption.TCP_NODELAY,true) ;
            b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS,3000) ;
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline p = ch.pipeline();
                    p.addLast("msgpack-decoder",new MsgpackDecoder()) ;
                    p.addLast("msgpack-encoder", new MsgpackEncoder()) ;
                    p.addLast(new EchoClientHandler(sendNumber)) ;
                }
            }) ;
            //发起异步连接操作，并同步等待连接成功
            ChannelFuture f = b.connect(host, port).sync();
            //同步等待客户端链路关闭
            f.channel().closeFuture().sync() ;
        }finally {
            //优雅退出，释放NIO线程组
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        String host = "127.0.0.1" ;
        int port = 8080 ;
        int sendNumber = 6 ;
        new EchoClient(host,port,sendNumber).connect();
    }
}
