package com.yicj.chapter10_2;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

@Slf4j
public class HttpRequestClient {

    public static void main(String[] args) throws Exception {
        String url = "http://localhost:9090/hello-boot/getUserInfo" ;
        URI uri = new URI(url) ;
        String host = uri.getHost();
        int port = uri.getPort();
        log.info("host :{}, port : {}",host,port);
        new HttpRequestClient().connect(host, port) ;
    }
    public void connect(String host,int port) throws Exception {
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
                    p.addLast("http-encoder",new HttpResponseDecoder()) ;//入站
                    p.addLast("http-aggregator",new HttpObjectAggregator(65536)) ;
                    p.addLast("http-decoder",new HttpRequestEncoder()) ;//出站
                    p.addLast("http-chunked",new ChunkedWriteHandler()) ;//出站
                    p.addLast(new HttpRequestClientHandler()) ;//入站

                }
            }) ;
            String content = "hello post";
            //发起异步连接操作，并同步等待连接成功
            ChannelFuture f = b.connect(host, port).sync();
            Channel channel = f.channel();
            FullHttpRequest req = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST,
                    "/hello-boot/getUserInfo", Unpooled.wrappedBuffer(content.getBytes("UTF-8")));

            req.headers().set(HttpHeaderNames.HOST, "127.0.0.1");
            req.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            int length = req.content().readableBytes();
            req.headers().set(HttpHeaderNames.CONTENT_LENGTH,length+"");
            req.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
            String str = req.content().toString(0, req.content().capacity(), Charset.defaultCharset());
            log.info("str : {}",str);
            channel.writeAndFlush(req).sync();
            //同步等待客户端链路关闭
            channel.closeFuture().sync() ;
        }finally {
            //优雅退出，释放NIO线程组
            group.shutdownGracefully();
        }
    }

    class HttpRequestClientHandler extends ChannelHandlerAdapter{
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            log.info("msg : {}" ,msg);
        }
    }
}
