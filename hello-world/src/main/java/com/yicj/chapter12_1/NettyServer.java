package com.yicj.chapter12_1;

import com.yicj.chapter12_1.codec.MyProtocolDecoder;
import com.yicj.chapter12_1.handler.ServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
//https://www.jianshu.com/p/c90ec659397c
@Slf4j
public class NettyServer {

    private static final int MAX_FRAME_LENGTH = 1024 * 1024 ;//最大长度
    private static final int LENGTH_FIELD_OFFSET = 2 ; // 长度偏移
    private static final int LENGTH_FIELD_LENGTH = 4 ; //长度字段所占的字节数
    private static final int LENGTH_ADJUSTMENT = 0 ;
    private static final int INITIAL_BYTES_TO_STRIP = 0 ;
    private int port ;

    //1.ch.pipeline().addLast(new MyProtocolDecoder(MAX_FRAME_LENGTH,LENGTH_FIELD_OFFSET,LENGTH_FIELD_LENGTH,LENGTH_ADJUSTMENT,INITIAL_BYTES_TO_STRIP,false));
    //2.ch.pipeline().addLast(new MyProtocolDecoder(MAX_FRAME_LENGTH,LENGTH_FIELD_OFFSET,LENGTH_FIELD_LENGTH,LENGTH_ADJUSTMENT,6,false));
    //如果把1换成2，那么我们就可以直接跳过Header和length字段,从而解码的时候,
    // 就只需要对body字段解码就行.其他字段跳过就忽略了.这也就是调用父类的方法的原因
    // (使最后拿到的ByteBuf只有body部分,而没有其他部分).
    public NettyServer(int port){
        this.port = port ;
    }

    public static void main(String[] args) throws InterruptedException {
        int port = 8080 ;
        new NettyServer(port).start();
    }

    public void start() throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workGroup = new NioEventLoopGroup() ;
        try {
            ServerBootstrap b = new ServerBootstrap() ;
            b.group(bossGroup,workGroup) ;
            b.channel(NioServerSocketChannel.class) ;
            b.localAddress(new InetSocketAddress(port)) ;
            b.option(ChannelOption.SO_BACKLOG,128) ;
            b.childOption(ChannelOption.SO_KEEPALIVE,true) ;
            b.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline p = ch.pipeline();
                    p.addLast(new MyProtocolDecoder(MAX_FRAME_LENGTH,LENGTH_FIELD_OFFSET,
                            LENGTH_FIELD_LENGTH,LENGTH_ADJUSTMENT,INITIAL_BYTES_TO_STRIP,false)) ;
                    p.addLast(new ServerHandler()) ;
                }
            }) ;
            //绑定端口，开始接收进来的连接
            ChannelFuture future = b.bind(port).sync();
            log.info("Server start listen at " + port);
            future.channel().closeFuture().sync() ;
        }finally {
            bossGroup.shutdownGracefully() ;
            workGroup.shutdownGracefully() ;
        }
    }




}
