package com.yicj.chapter9;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class FixedLengthFrameDecoderTest {

    @Test
    public void testFramesDecoded(){
        ByteBuf buf = Unpooled.buffer() ;
        for(int i = 0 ; i< 9 ; i++){
            buf.writeByte(i) ;
        }
        ByteBuf input = buf.duplicate() ;
        FixedLengthFrameDecoder decoder = new FixedLengthFrameDecoder(3);
        EmbeddedChannel channel = new EmbeddedChannel(decoder) ;
        channel.writeInbound(input.retain()) ;
        //标记Channel为已完成状态
        boolean finish = channel.finish();
        log.info("finish flag : {}",finish);
        // read message
        //读取所生成的消息，并且验证是否包含3帧(切片),其中每帧(切片)都为3字节
        ByteBuf read =  channel.readInbound() ;
        boolean readEqualsFlag = buf.readSlice(3).equals(read);
        read.release() ;
        log.info("read hello flag : {}", readEqualsFlag);

        read = channel.readInbound() ;
        readEqualsFlag = buf.readSlice(3).equals(read) ;
        read.release() ;
        log.info("read hello flag : {}", readEqualsFlag);

        read = channel.readInbound() ;
        readEqualsFlag = buf.readSlice(3).equals(read) ;
        read.release() ;
        log.info("read hello flag : {}", readEqualsFlag);


        read = channel.readInbound() ;
        buf.release() ;
        log.info("read : {}",read);
    }

    @Test
    public void testFramesDecoded2(){
        ByteBuf buf = Unpooled.buffer() ;
        for (int i = 0 ; i< 9 ; i++){
            buf.writeByte(i) ;
        }
        ByteBuf input = buf.duplicate() ;
        FixedLengthFrameDecoder decoder = new FixedLengthFrameDecoder(3);
        EmbeddedChannel channel = new EmbeddedChannel(decoder) ;
        //返回false，因为没有一个完整的可供读取的帧
        boolean writeFlag1 = channel.writeInbound(input.readBytes(2));
        log.info("write flag1 : {}", writeFlag1);
        boolean writeFlag2 = channel.writeInbound(input.readBytes(7));
        log.info("write flag2 : {}", writeFlag2);
        boolean finish = channel.finish();
        log.info("finish : {}",finish);
        ByteBuf read = channel.readInbound() ;
        boolean eqFlag = buf.readSlice(3).equals(read);
        read.release() ;
        log.info("eq flag1 : {}",eqFlag);
        //
        read = channel.readInbound() ;
        eqFlag = buf.readSlice(3).equals(read) ;
        read.release() ;
        log.info("eq flag2 : {}" ,eqFlag);
        //
        read = channel.readInbound() ;
        eqFlag = buf.readSlice(3).equals(read) ;
        read.release() ;
        log.info("eq flag3 : {}", eqFlag);
        //
        read = channel.readInbound() ;
        buf.release() ;
        log.info("read : {}", read);
    }
}
