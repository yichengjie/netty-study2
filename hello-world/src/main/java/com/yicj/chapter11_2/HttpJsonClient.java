package com.yicj.chapter11_2;

import com.yicj.chapter11_2.codec.client.HttpJsonRequestEncoder;
import com.yicj.chapter11_2.codec.client.HttpJsonResponseDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;

public class HttpJsonClient {
    public void connect(String host, int port) throws InterruptedException {
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
                    //在MessagePack解码器之前增加LengthFieldBasedFrameDecoder
                    p.addLast("http-decoder",new HttpResponseDecoder()) ;
                    p.addLast("http-aggregator", new HttpObjectAggregator(65536)) ;
                    p.addLast("json-decoder",new HttpJsonResponseDecoder(true)) ;//XML解码器
                    //
                    p.addLast("http-encoder", new HttpRequestEncoder()) ;
                    p.addLast("json-encoder", new HttpJsonRequestEncoder()) ;
                    p.addLast("json-clientHandler", new HttpJsonClientHandler()) ;
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
}
