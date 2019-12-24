package com.yicj.chapter11.codec;

import com.yicj.chapter11.codec.entity.HttpXmlRequest;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.util.List;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;

public class HttpXmlRequestDecoder extends AbstractHttpXmlDecoder<FullHttpRequest>{

    public HttpXmlRequestDecoder(Class<?> clazz) {
       this(clazz,false) ;
    }

    public HttpXmlRequestDecoder(Class<?> clazz, boolean isPrint){
        super(clazz,isPrint);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, FullHttpRequest arg1, List<Object> list) throws Exception {

        if(!arg1.decoderResult().isSuccess()){
            sendError(ctx,HttpResponseStatus.BAD_REQUEST);
            return;
        }
        HttpXmlRequest request = new HttpXmlRequest(arg1,decode0(ctx,arg1.content())) ;
        list.add(request) ;
    }

    private void sendError(ChannelHandlerContext ctx, HttpResponseStatus status){
        FullHttpResponse response=new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,status,
                Unpooled.copiedBuffer("Failure: "+status.toString()+"\r\n", CharsetUtil.UTF_8));
        response.headers().set(CONTENT_TYPE,"text/html;charset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}
