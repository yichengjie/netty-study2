package com.yicj.chapter10;

import com.sun.xml.internal.bind.v2.model.annotation.RuntimeAnnotationReader;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Pattern;

import static io.netty.handler.codec.http.HttpMethod.*;


public class HttpFileServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static final String BAD_REQUEST = "bad request";
    private static final String METHOD_NOT_ALLOWED = "method not allowed" ;
    private static final String FORBIDDEN = "forbidden" ;
    private static final String NOT_FOUND = "not found" ;
    private static final String CONNECTION = "connection" ;
    private static final String INSECURE_URI = "insecure-uri" ;
    private static final String CONNECTT_TYPE = "CONNECTT-TYPE" ;
    private static final Pattern ALLLOWED_FILE_NAME = Pattern.compile("[A-Za-z0-9][-_A-Za-z0-9\\.]*]") ;

    private String url ;
    public HttpFileServerHandler(String url){
        this.url = url ;
    }
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        if(!request.getDecoderResult().isSuccess()){
            sendError(ctx,BAD_REQUEST) ;
            return;
        }
        if(request.getMethod() != GET){
            sendError(ctx,METHOD_NOT_ALLOWED);
            return;
        }
        final String uri = request.getUri() ;
        final String path = sanitizeUri(uri) ;
        if(path == null){
            sendError(ctx,FORBIDDEN);
            return;
        }
        File file = new File(path) ;
        if(file.isHidden() || !file.exists()){
            sendError(ctx,NOT_FOUND);
            return;
        }
        if(!file.isFile()){
            sendError(ctx,FORBIDDEN);
            return;
        }
        RandomAccessFile randomAccessFile =  null ;
        try {
            randomAccessFile = new RandomAccessFile(file,"r") ;//以只读的形式打开文件
        }catch (FileNotFoundException e){
            sendError(ctx,NOT_FOUND);
            return;
        }
        long fileLength = randomAccessFile.length() ;
        HttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK) ;
        setContentLength(response,fileLength) ;
        setContentTypeHeader(response,file);
        if(isKeepAive(request)){
            response.headers().set(CONNECTION,HttpHeaders.Values.KEEP_ALIVE) ;
        }
        ctx.write(response) ;
        ChannelFuture sendFuture = null ;
        sendFuture = ctx.write(new ChunkedFile(randomAccessFile,0,fileLength,8192),ctx.newProgressivePromise()) ;
        sendFuture.addListener(new ChannelProgressiveFutureListener(){

            @Override
            public void operationComplete(ChannelProgressiveFuture channelProgressiveFuture) throws Exception {

            }

            @Override
            public void operationProgressed(ChannelProgressiveFuture channelProgressiveFuture, long l, long l1) throws Exception {

            }
        }) ;

    }

    private String sanitizeUri(String uri) {
        try {
            uri = URLDecoder.decode(uri,"UTF-8") ;
        } catch (UnsupportedEncodingException e) {
            try {
                url = URLDecoder.decode(uri,"ISO-8859-1") ;
            } catch (UnsupportedEncodingException ex) {
                throw new Error() ;
            }
        }

        uri = uri.replace('/',File.separatorChar) ;
        if(uri.contains(File.separator+".") || uri.contains("."+File.separator) || uri.startsWith(".")
            || uri.endsWith(".")){
            return null ;
        }
        return System.getProperty("user.dir") + File.separator + uri ;
    }

    private static void sendListing(ChannelHandlerContext ctx ,File dir){
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.OK) ;
        response.headers().set(CONNECTT_TYPE,"text/html;charset=UTF-8") ;
        StringBuilder buf = new StringBuilder() ;
        buf.append("<li>连接: <a></li>") ;
    }

    private boolean isKeepAive(FullHttpRequest request) {

        return true ;
    }

    private void setContentTypeHeader(HttpResponse response, File file) {
    }


    private void setContentLength(HttpResponse response, long fileLength) {
    }



    private void sendError(ChannelHandlerContext ctx,String msg){

    }
}
