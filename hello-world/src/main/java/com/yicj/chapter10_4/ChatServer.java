package com.yicj.chapter10_4;

import com.yicj.chapter10_4.initializer.ChatServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.ImmediateEventExecutor;

import java.net.InetSocketAddress;

public class ChatServer {

    private final ChannelGroup channelGroup = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE) ;
    private final EventLoopGroup group = new NioEventLoopGroup() ;
    private Channel channel ;

    public ChannelFuture start(InetSocketAddress address){
        ServerBootstrap bootstrap = new ServerBootstrap() ;
        bootstrap.group(group) ;
        bootstrap.channel(NioServerSocketChannel.class) ;
        bootstrap.childHandler(createInitializer(channelGroup)) ;
        ChannelFuture future = bootstrap.bind(address);
        future.syncUninterruptibly() ;
        channel = future.channel();
        return future ;
    }

    protected ChannelHandler createInitializer(ChannelGroup channelGroup) {
        return  new ChatServerInitializer(channelGroup) ;
    }

    //处理服务器关闭，并释放所有的资源
    public void destroy(){
        if(channel != null){
            channel.close() ;
        }
        channelGroup.close() ;
        group.shutdownGracefully() ;
    }

    public static void main(String[] args) throws Exception {
        int port = 8080;
        final ChatServer endpoint = new ChatServer() ;
        ChannelFuture future = endpoint.start(new InetSocketAddress(port));
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                endpoint.destroy();
            }
        });
        future.channel().closeFuture().syncUninterruptibly() ;
    }

}
