package com.yicj.chapter9;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.TooLongFrameException;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

@Slf4j
public class FrameChunkDecoderTest {

    @Test
    public void testFramesDecoded(){
        ByteBuf buf = Unpooled.buffer() ;
        for(int i = 0 ; i< 9 ; i++){
            buf.writeByte(i) ;
        }
        ByteBuf input = buf.duplicate();
        FrameChunkDecoder decoder = new FrameChunkDecoder(3);
        EmbeddedChannel channel = new EmbeddedChannel(decoder);
        channel.writeInbound(input.readBytes(2)) ;
        try {
            //写入一个4字节大小的帧，并捕获预期的TooLongFrameException
            channel.writeInbound(input.readBytes(4)) ;
            //如果上面没有抛出异常，那么就会到达这个断言，并且测试失败
            Assert.fail();
        }catch (TooLongFrameException e){e.printStackTrace();}
        //写入剩余的2字节，并断言产生一个有效的帧
        boolean flag1 = channel.writeInbound(input.readBytes(3));
        log.info("flag1 : {}", flag1);
        boolean finish = channel.finish();
        log.info("finish : {}", finish);
        // Read frames
        ByteBuf read = channel.readInbound() ;
        boolean equals = buf.readSlice(2).equals(read);
        read.release() ;
        log.info("equals : {}", equals);

        read = channel.readInbound() ;
        equals = buf.skipBytes(4).readSlice(3).equals(read) ;
        read.release() ;
        log.info("equals : {}" ,equals);
    }
}
