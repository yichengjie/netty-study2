package com.yicj.chapter10_4.handler;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedNioFile;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.net.URL;


@Slf4j
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final String wsUri ;
    private static final File INDEX ;

    static {
        //到target/classes目录中
        URL location = HttpRequestHandler.class.getProtectionDomain()
                .getCodeSource().getLocation() ;
        try {
            String path = location.toURI() +"index.html" ;
            INDEX = new File(path) ;
        }catch (URISyntaxException e){
            throw new IllegalStateException("Unable to locate index.html", e) ;
        }
    }

    public HttpRequestHandler(String wsUri){
        this.wsUri = wsUri ;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        //如果http请求指向了地址为/ws的URI,那么FullHttpRequest将调用request.retain(),
        //并通过调用fireChannelRead(msg)方法将它转发给下一个ChannelInBoundHandler
        if (wsUri.equalsIgnoreCase(request.uri())){
            ctx.fireChannelRead(request.retain()) ;
        }else {
            if(HttpUtil.is100ContinueExpected(request)){
                send100Continue(ctx) ;
            }
            RandomAccessFile file = new RandomAccessFile(INDEX,"r") ;
            HttpResponse response = new DefaultFullHttpResponse(
              request.protocolVersion(), HttpResponseStatus.OK
            ) ;
            request.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/html;charset=UTF-8") ;
            boolean keepAlive = HttpUtil.isKeepAlive(request);
            if(keepAlive){
                response.headers().set(HttpHeaderNames.CONTENT_LENGTH,file.length()) ;
                response.headers().set(HttpHeaderNames.CONNECTION,HttpHeaderValues.KEEP_ALIVE) ;
            }
            ctx.write(response) ;
            if(ctx.pipeline().get(SslHandler.class) == null){
                ctx.write(new DefaultFileRegion(
                        file.getChannel(),0 , file.length())) ;
            }else {//如果有SSL
                ctx.write(new ChunkedNioFile(file.getChannel())) ;
            }
            //将EMPTY_LAST_CONTENT写入来标记响应的结束
            ChannelFuture future =
                    ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
            if(!keepAlive){
                future.addListener(ChannelFutureListener.CLOSE) ;
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("error ==> ", cause);
        ctx.close() ;
    }

    private void send100Continue(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,HttpResponseStatus.CONTINUE
        ) ;
        ctx.writeAndFlush(response) ;
    }
}
