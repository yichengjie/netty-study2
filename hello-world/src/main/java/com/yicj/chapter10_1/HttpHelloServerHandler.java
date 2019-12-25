package com.yicj.chapter10_1;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.CharsetUtil;
import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaderNames.LOCATION;
import static io.netty.handler.codec.http.HttpHeaderUtil.isKeepAlive;
import static io.netty.handler.codec.http.HttpHeaderUtil.setContentLength;


public class HttpHelloServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    //消息接入方法
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        this.sendError(ctx,HttpResponseStatus.METHOD_NOT_ALLOWED);
        //this.sendChunkfile(ctx,request);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause){
        cause.printStackTrace();
        if(ctx.channel().isActive()){
            sendError(ctx,HttpResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void sendChunkfile(ChannelHandlerContext ctx, FullHttpRequest request) throws IOException {
        File file = new File("D:\\tools\\jboss-5.1.0.GA.zip") ;
        //以只读的方式打开文件，如果打开失败返回404错误
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
        //获取文件的长度构造成功的HTTP应答消息
        long fileLength = randomAccessFile.length();
        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        setContentLength(response, fileLength);
        setContentTypeHeader(response, file);
        //判断是否是keepAlive，如果是就在响应头中设置CONNECTION为keepAlive
        if(isKeepAlive(request)){
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }
        ctx.write(response);

        ChannelFuture sendFileFuture;
        //通过Netty的ChunkedFile对象直接将文件写入到发送缓冲区中
        sendFileFuture = ctx.write(new ChunkedFile(randomAccessFile,0,fileLength,8192),ctx.newProgressivePromise());
        //为sendFileFuture添加监听器，如果发送完成打印发送完成的日志
        sendFileFuture.addListener(new ChannelProgressiveFutureListener() {
            @Override
            public void operationComplete(ChannelProgressiveFuture future)
                    throws Exception {
                System.out.println("Transfer complete.");
            }
            @Override
            public void operationProgressed(ChannelProgressiveFuture future, long progress, long total)
                    throws Exception {
                if(total<0){
                    System.out.println("Transfer progress: "+progress);
                }else {
                    System.out.println("Transfer progress: "+progress+"/"+total);
                }
            }
        });
        //如果使用chunked编码，最后需要发送一个编码结束的空消息体，将LastHttpContent.EMPTY_LAST_CONTENT发送到缓冲区中，
        //来标示所有的消息体已经发送完成，同时调用flush方法将发送缓冲区中的消息刷新到SocketChannel中发送
        ChannelFuture lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        //如果是非keepAlive的，最后一包消息发送完成后，服务端要主动断开连接
        if(!isKeepAlive(request)){
            lastContentFuture.addListener(ChannelFutureListener.CLOSE);
        }
    }

    //发送目录的链接到客户端浏览器
    private void sendListing(ChannelHandlerContext ctx){
        //创建成功的http响应消息
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        //设置消息头的类型是html文件，不要设置为text/plain，客户端会当做文本解析
        response.headers().set(CONTENT_TYPE,"text/html;charset=UTF-8");
        //构造返回的html页面内容
        StringBuilder buf = new StringBuilder();
        buf.append("hello world .....");
        //分配消息缓冲对象
        ByteBuf buffer = Unpooled.copiedBuffer(buf,CharsetUtil.UTF_8);
        //将缓冲区的内容写入响应对象，并释放缓冲区
        response.content().writeBytes(buffer);
        buffer.release();
        //将响应消息发送到缓冲区并刷新到SocketChannel中
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private void sendRedirect(ChannelHandlerContext ctx,String newUri){
        FullHttpResponse response=new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FOUND);
        response.headers().set(LOCATION,newUri);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private void sendError(ChannelHandlerContext ctx,HttpResponseStatus status){
        FullHttpResponse response=new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,status,
                Unpooled.copiedBuffer("Failure: "+status.toString()+"\r\n",CharsetUtil.UTF_8));
        response.headers().set(CONTENT_TYPE,"text/html;charset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private void setContentTypeHeader(HttpResponse response, File file){
        MimetypesFileTypeMap mimetypesTypeMap = new MimetypesFileTypeMap();
        response.headers().set(CONTENT_TYPE, mimetypesTypeMap.getContentType(file.getPath()));
    }
}
