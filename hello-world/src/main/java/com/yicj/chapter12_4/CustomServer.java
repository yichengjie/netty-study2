package com.yicj.chapter12_4;

import com.yicj.chapter12_4.codec.CustomDecoder;
import com.yicj.chapter12_4.entity.CustomMsg;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;  
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;  

@Slf4j
public class CustomServer {  
      
    private static final int MAX_FRAME_LENGTH = 1024 * 1024;  
    private static final int LENGTH_FIELD_LENGTH = 4;  
    private static final int LENGTH_FIELD_OFFSET = 2;  
    private static final int LENGTH_ADJUSTMENT = 0;  
    private static final int INITIAL_BYTES_TO_STRIP = 0;  
  
    private int port;  
      
    public CustomServer(int port) {  
        this.port = port;  
    }  
      
    public void start(){  
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);  
        EventLoopGroup workerGroup = new NioEventLoopGroup();  
        try {  
            ServerBootstrap sbs = new ServerBootstrap().group(bossGroup,workerGroup).channel(NioServerSocketChannel.class).localAddress(new InetSocketAddress(port))  
                    .childHandler(new ChannelInitializer<SocketChannel>() {  
                          
                        protected void initChannel(SocketChannel ch) throws Exception {  
                             ch.pipeline().addLast(new CustomDecoder(MAX_FRAME_LENGTH,LENGTH_FIELD_LENGTH,LENGTH_FIELD_OFFSET,LENGTH_ADJUSTMENT,INITIAL_BYTES_TO_STRIP,false));
                             ch.pipeline().addLast(new CustomServerHandler());  
                        };  
                          
                    }).option(ChannelOption.SO_BACKLOG, 128)     
                    .childOption(ChannelOption.SO_KEEPALIVE, true);  
             // 绑定端口，开始接收进来的连接  
             ChannelFuture future = sbs.bind(port).sync();    
               
             System.out.println("Server start listen at " + port );  
             future.channel().closeFuture().sync();  
        } catch (Exception e) {  
            bossGroup.shutdownGracefully();  
            workerGroup.shutdownGracefully();  
        }  
    }  
      
    public static void main(String[] args) throws Exception {  
        int port;  
        if (args.length > 0) {  
            port = Integer.parseInt(args[0]);  
        } else {  
            port = 8080;  
        }  
        new CustomServer(port).start();  
    }


    class CustomServerHandler extends SimpleChannelInboundHandler<Object> {
        @Override
        protected void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {
            if(msg instanceof CustomMsg) {
                CustomMsg customMsg = (CustomMsg)msg;
                log.info("Client->Server:"+ctx.channel().remoteAddress()+" send "+ customMsg.getBody());
            }
        }
    }
}  