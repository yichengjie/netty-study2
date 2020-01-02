package com.yicj.chapter9;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class AbsIntegerEncoderTest {

    @Test
    public void testEncoder(){
        ByteBuf buf = Unpooled.buffer() ;
        for (int i = 0 ; i < 10 ; i++){
            buf.writeInt(i * -1) ;
        }
        AbsIntegerEncoder encoder = new AbsIntegerEncoder();
        EmbeddedChannel channel = new EmbeddedChannel(encoder) ;
        boolean writeFlag = channel.writeOutbound(buf);
        log.info("write flag : {}", writeFlag);
        boolean finish = channel.finish();
        log.info("finish : {}", finish);

        //read bytes
        for(int i = 0 ; i < 10 ; i++){
            Object o = channel.readOutbound();
            log.info("i : {}" , o);
        }
        Object o = channel.readOutbound() ;
        log.info("o : {} ", o);
    }

}
