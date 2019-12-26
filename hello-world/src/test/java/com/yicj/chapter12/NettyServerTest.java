package com.yicj.chapter12;

import com.yicj.chapter12.codec.NettyMessageDecoder;
import com.yicj.chapter12.codec.NettyMessageEncoder;
import com.yicj.chapter12.entity.Header;
import com.yicj.chapter12.entity.MessageType;
import com.yicj.chapter12.entity.NettyMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Test;

import java.io.IOException;

public class NettyServerTest {

    @Test
    public void testServerDecoder() throws IOException {
        int maxFrameLength = 1024 * 1024;
        int lengthFieldOffset = 4 ;
        int lengthFieldLength  =4;
        NettyMessageDecoder decoder = new NettyMessageDecoder(maxFrameLength,lengthFieldOffset,lengthFieldLength) ;
        ChannelInitializer initializer  = new ChannelInitializer() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ChannelPipeline p = ch.pipeline();
                p.addLast(decoder) ;
                p.addLast(new NettyMessageEncoder()) ;
            }
        } ;
        NettyMessage msg = this.buildLoginReq() ;
        EmbeddedChannel channel = new EmbeddedChannel(initializer) ;
        ByteBuf sendBuf = Unpooled.buffer() ;
        sendBuf.writeInt(msg.getHeader().getCrcCode()) ;
        sendBuf.writeInt(msg.getHeader().getLength()) ;
        sendBuf.writeLong(msg.getHeader().getSessionID()) ;
        sendBuf.writeByte(msg.getHeader().getType()) ;
        sendBuf.writeByte(msg.getHeader().getPriority()) ;
        sendBuf.writeInt(msg.getHeader().getAttachment().size()) ;
        sendBuf.writeInt(0) ;
        //sendBuf.setIndex(4,sendBuf.readableBytes()) ;
        //这里注意需要去掉前8个字节，上面的代码会报错
        sendBuf.setInt(4, sendBuf.readableBytes() - 8);
        channel.writeInbound(sendBuf) ;
    }

    @Test
    public void testServerEncoder(){
        ChannelInitializer initializer  = new ChannelInitializer() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ChannelPipeline p = ch.pipeline();
                p.addLast(new NettyMessageEncoder()) ;
            }
        } ;
        NettyMessage msg = this.buildLoginReq() ;
        EmbeddedChannel channel = new EmbeddedChannel(initializer) ;
        channel.writeOutbound(msg) ;
    }


    private NettyMessage buildLoginReq() {
        NettyMessage message  = new NettyMessage() ;
        Header header = new Header() ;
        header.setType(MessageType.LOGIN_REQ.value());
        message.setHeader(header);
        return message ;
    }
}
