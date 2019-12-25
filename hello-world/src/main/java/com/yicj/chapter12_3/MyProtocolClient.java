package com.yicj.chapter12_3;

import com.yicj.chapter12_3.codec.MyProtocolEncoder;
import com.yicj.chapter12_3.entity.MyProtocolBean;
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
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline p = ch.pipeline();
                    p.addLast(new MyProtocolEncoder()) ;
                    p.addLast(new EchoClientHandler()) ;
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

    class EchoClientHandler extends ChannelHandlerAdapter {
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            byte type = 0xA ;
            byte flag = 0xC ;
            int length = "Hello,Netty".length() ;
            String content = "Hello,Netty" ;
            MyProtocolBean bean = new MyProtocolBean(type,flag,length,content) ;
            log.info("=============> channelActive write :{}", bean);
            ctx.writeAndFlush(bean) ;
        }



        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            log.error("Unexpected exception from downstream ", cause);
            ctx.close() ;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        int port = 8080 ;
        new MyProtocolClient().connect(port,"127.0.0.1");
    }
}
