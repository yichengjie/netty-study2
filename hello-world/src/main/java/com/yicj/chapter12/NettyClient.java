package com.yicj.chapter12;

import com.yicj.chapter12.codec.NettyMessageEncoder;
import com.yicj.chapter12.codec.NettyMessageDecoder;
import com.yicj.chapter12.entity.NettyConstant;
import com.yicj.chapter12.handler.HeartBeatReqHandler;
import com.yicj.chapter12.handler.LoginAuthReqHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NettyClient {
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1) ;
    private EventLoopGroup group = new NioEventLoopGroup() ;

    public static void main(String[] args) throws InterruptedException {
        new NettyClient().connect(NettyConstant.PORT, NettyConstant.REMOTEIP);
    }

    public void connect(int port, String host) throws InterruptedException {
        //配置客户端NIO线程组
        try {
            Bootstrap b = new Bootstrap() ;
            b.group(group) ;
            b.channel(NioSocketChannel.class) ;
            b.option(ChannelOption.TCP_NODELAY,true) ;
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline p = ch.pipeline();
                    p.addLast(new NettyMessageDecoder(1024 * 1024, 4,4 )) ;
                    p.addLast("MessageEncoder",new NettyMessageEncoder()) ;
                    p.addLast("readTimeoutHandler",new ReadTimeoutHandler(50)) ;
                    p.addLast("loginAuthHandler",new LoginAuthReqHandler()) ;
                    p.addLast("heartBeatHandler",new HeartBeatReqHandler()) ;
                }
            }) ;
            //发起异步连接操作
            InetSocketAddress address1 = new InetSocketAddress(host, port);
            InetSocketAddress address2 = new InetSocketAddress(NettyConstant.LOCALIP, NettyConstant.LOCAL_PORT);
            ChannelFuture future = b.connect(address1, address2).sync();
        }finally {
            //所有的资源释放之后，清空资源，再次发起重连操作
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        TimeUnit.SECONDS.sleep(5);
                        try {
                            connect(NettyConstant.PORT, NettyConstant.REMOTEIP); // 发起重连操作
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            });
        }
    }

}
