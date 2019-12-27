package com.yicj.chapter11_2;

import com.yicj.chapter11.entity.Address;
import com.yicj.chapter11.entity.Order;
import com.yicj.chapter11_2.codec.entity.HttpJsonRequest;
import com.yicj.chapter11_2.codec.entity.HttpJsonResponse;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.List;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaderUtil.isKeepAlive;

@Slf4j
public class HttpJsonServerHandler extends SimpleChannelInboundHandler<HttpJsonRequest> {
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, HttpJsonRequest jsonRequest) throws Exception {
        HttpRequest request = jsonRequest.getRequest() ;
        Order order = (Order) jsonRequest.getBody() ;
        log.info("Http server receive request : " + order);
        doBusiness(order) ;
        ChannelFuture future = ctx.writeAndFlush(new HttpJsonResponse(null, order));
        if(!isKeepAlive(request)){
            future.addListener(ChannelFutureListener.CLOSE) ;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("====> ", cause);
        if(ctx.channel().isActive()){
            this.sendError(ctx,HttpResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }


    private void sendError(ChannelHandlerContext ctx, HttpResponseStatus status){
        FullHttpResponse response=new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,status,
                Unpooled.copiedBuffer("Failure: "+status.toString()+"\r\n", CharsetUtil.UTF_8));
        response.headers().set(CONTENT_TYPE,"text/html;charset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private void doBusiness(Order order) {
        order.getCustomer().setFirstName("狄");
        order.getCustomer().setLastName("仁杰");
        List<String> midNames = new ArrayList<String>() ;
        midNames.add("李元霸") ;
        order.getCustomer().setMiddleNames(midNames);
        Address address = order.getBillTo() ;
        address.setCity("信阳");
        address.setState("河南");
        address.setStreet1("育英路");
        address.setPostCode("123456");
        order.setBillTo(address);
        order.setShipTo(address);
    }
}
