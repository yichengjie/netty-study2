package com.yicj.chapter10_1;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaderNames.LOCATION;


public class HttpHelloServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    //消息接入方法
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        this.sendError(ctx,HttpResponseStatus.METHOD_NOT_ALLOWED);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause){
        cause.printStackTrace();
        if(ctx.channel().isActive()){
            sendError(ctx,HttpResponseStatus.INTERNAL_SERVER_ERROR);
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

    private void setContentTypeHeader(HttpResponse response,File file){
        MimetypesFileTypeMap mimetypesTypeMap=new MimetypesFileTypeMap();
        response.headers().set(CONTENT_TYPE,mimetypesTypeMap.getContentType(file.getPath()));
    }
}
