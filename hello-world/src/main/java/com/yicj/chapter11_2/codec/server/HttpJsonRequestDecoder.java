package com.yicj.chapter11_2.codec.server;

import com.yicj.chapter11_2.codec.AbstractHttpJsonDecoder;
import com.yicj.chapter11_2.codec.entity.HttpJsonRequest;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import java.util.List;

//注意这里千万不要讲泛型的FullHttpResponse写成DefaultFullHttpResponse了，
//否则这个Decoder不会被调用，这里是真实为AggregatedFullHttpResponse
public class HttpJsonRequestDecoder extends AbstractHttpJsonDecoder<FullHttpRequest> {
    private Class<?> clazz ;
    public HttpJsonRequestDecoder(Class<?> clazz){
        this(clazz,false) ;
    }
    public HttpJsonRequestDecoder(Class<?> clazz, boolean isPrint){
        super(isPrint);
        this.clazz = clazz ;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, FullHttpRequest req, List<Object> list) throws Exception {
        if(!req.decoderResult().isSuccess()){
            sendError(ctx,HttpResponseStatus.BAD_REQUEST);
            return;
        }
        HttpJsonRequest request = new HttpJsonRequest(req,decode0(clazz,req.content())) ;
        list.add(request) ;
    }

    private void sendError(ChannelHandlerContext ctx, HttpResponseStatus status){
        FullHttpResponse response=new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,status,
                Unpooled.copiedBuffer("Failure: "+status.toString()+"\r\n", CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/html;charset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}
