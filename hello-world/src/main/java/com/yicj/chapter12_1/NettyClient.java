package com.yicj.chapter12_1;

import com.yicj.chapter12_1.codec.MyProtocolEncoder;
import com.yicj.chapter12_1.entity.MyProtocolBean;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;


public class NettyClient {
    private static final String HOST = "127.0.0.1" ;
    private static final int PORT = 8080 ;
    private static final int SIZE = 256 ;

    public static void main(String[] args) {

    }

    public void connect() throws InterruptedException {
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
                    p.addLast(new MyProtocolEncoder()) ;
                    p.addLast(new ClientHandler()) ;
                }
            }) ;
            ChannelFuture future = b.connect(HOST, PORT).sync();
            //future.channel().writeAndFlush("Hello Netty Server, I am a common client") ;
            future.channel().closeFuture().sync() ;
        }finally {
            group.shutdownGracefully() ;
        }
    }

    class ClientHandler extends ChannelHandlerAdapter{
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            byte type = 0xA ;
            byte flag = 0xC ;
            int length = "Hello,Netty".length() ;
            String content = "Hello,Netty" ;
            MyProtocolBean bean = new MyProtocolBean(type,flag,length,content) ;
            ctx.writeAndFlush(bean) ;
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
        }
    }
}
