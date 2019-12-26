package com.yicj.chapter10_2;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;

@Slf4j
public class HttpRequestClient {

    public static void main(String[] args) throws Exception {
        String url = "http://localhost:9090" ;
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
                    p.addLast("http-decompress",new HttpContentDecompressor()) ;
                    p.addLast(new HttpRequestClientHandler()) ;//入站
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

    class HttpRequestClientHandler extends ChannelHandlerAdapter{
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            URI uri = new URI("/hello-boot/getUserInfo") ;
            String content = "hello, world";
            FullHttpRequest req = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST,
                    uri.toASCIIString(), Unpooled.copiedBuffer(content.getBytes("UTF-8")));
            req.headers().set(HttpHeaderNames.HOST, "127.0.0.1");
            HttpHeaderUtil.setKeepAlive(req,false);
            //req.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            int length = req.content().readableBytes();
            req.headers().set(HttpHeaderNames.CONTENT_LENGTH,length+"");
            req.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
            log.info("req info : {}", req);
            //String requestContent = req.content().toString(0, req.content().capacity(), Charset.defaultCharset());
            //log.info("requestContent : {}",requestContent);
            ctx.writeAndFlush(req).sync();
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            FullHttpResponse response = (FullHttpResponse) msg ;
            log.info("response info : {}" , response);
            ByteBuf content = response.content();
            byte [] bytes = new byte[content.readableBytes()] ;
            content.readBytes(bytes) ;
            String respContent = new String(bytes,"UTF-8") ;
            log.info("respContent : {}" ,respContent);
        }
    }
}
