package com.test;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

@Slf4j
public class TestDecoder {

    public static void main(String[] args) throws UnsupportedEncodingException {
        TestDecoder decoder = new TestDecoder() ;
        decoder.testLengthFieldBasedFrameDecoder();
    }

    public void testLengthFieldBasedFrameDecoder() throws UnsupportedEncodingException {
        /**
         * maxFrameLength :     发送的数据包最大长度
         * lengthFieldOffset:   长度域偏移量，指的是长度域位于整个数据包字节数组的下标
         * lengthFieldLength:   长度域的自己的字节数长度
         * lengthAdjustment:    长度域的偏移量矫正。如果长度域的值，除了包含有效数据域的长度外，
         *                      还包含了其他域（如长度域自身）长度，那么就需要进行矫正。
         *                      矫正值为：包长-长度域的值-长度域品偏移-长度域长
         *  initialBytesToStrip:丢弃的起始字节。丢弃处于有效数据前面的字节数量，比如前面的4个节点的长度域，
         *                      则它的值为4
         */
        int maxFrameLength = 1024 ;
        int lengthFieldOffset = 0 ;
        int lengthFieldLength = 4 ;
        int lengthAdjustment = 0 ;
        int initialBytesToStrip = 4 ;
        LengthFieldBasedFrameDecoder spliter = new LengthFieldBasedFrameDecoder(
                maxFrameLength,lengthFieldOffset,lengthFieldLength,lengthAdjustment,initialBytesToStrip) ;
        ChannelInitializer initializer  = new MyChannelInitializer(spliter) ;
        EmbeddedChannel channel = new EmbeddedChannel(initializer) ;
        for (int i = 0 ; i< 100 ; i++){
            ByteBuf buf = Unpooled.buffer() ;
            String s = "呵呵,I am " + i ;
            byte [] bytes = s.getBytes("UTF-8") ;
            buf.writeInt(bytes.length) ;
            buf.writeBytes(bytes) ;
            //注意这里是入站消息
            channel.writeInbound(buf) ;
        }
    }
    class MyChannelInitializer extends ChannelInitializer{
        private LengthFieldBasedFrameDecoder spliter ;
        private MyChannelInitializer(LengthFieldBasedFrameDecoder spliter){
            this.spliter = spliter ;
        }
        @Override
        protected void initChannel(Channel channel) throws Exception {
            ChannelPipeline pipeline = channel.pipeline();
            pipeline.addLast(spliter) ;
            pipeline.addLast(new StringDecoder(Charset.forName("UTF-8"))) ;
            pipeline.addLast(new StringProcessHandler());
        }
    }

    class StringProcessHandler extends ChannelHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            log.info("msg : {}" ,msg);
        }
    }


}
