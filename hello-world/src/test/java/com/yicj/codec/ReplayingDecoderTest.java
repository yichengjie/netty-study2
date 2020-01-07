package com.yicj.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.UnsupportedEncodingException;

@Slf4j
public class ReplayingDecoderTest {

    @Test
    public void testDecodeWithState() throws UnsupportedEncodingException {
        //测试入站解码
        String content = "field,world" ;
        LongHeaderFrameDecoder3 decoder = new LongHeaderFrameDecoder3() ;
        EmbeddedChannel channel = new EmbeddedChannel(decoder) ;
        ByteBuf buf = Unpooled.buffer() ;
        buf.writeInt(content.length()) ;
        buf.writeBytes(content.getBytes("UTF-8")) ;
        channel.writeInbound(buf) ;

        ByteBuf obj = channel.readInbound();
        log.info("length : {}", obj.readableBytes() );
        byte [] infos = new byte[obj.readableBytes()] ;
        obj.readBytes(infos) ;
        String str = new String(infos,"UTF-8") ;
        log.info("str : {}", str);
    }
}
