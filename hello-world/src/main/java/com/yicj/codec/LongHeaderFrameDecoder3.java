package com.yicj.codec;

import com.yicj.codec.common.MyDecoderState;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class LongHeaderFrameDecoder3 extends ReplayingDecoder<MyDecoderState> {
    //HEAD的长度
    private int length ;
    public LongHeaderFrameDecoder3(){
        // 初始状态是读取头部的HEAD
        super(MyDecoderState.READ_HEAD);
    }
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext,
                          ByteBuf buf, List<Object> list) throws Exception {
        log.info("===========> status :{}", state());
        switch (state()){
            case READ_HEAD:
                length = buf.readInt() ;
                checkpoint(MyDecoderState.READ_HEAD);
            case READ_BODY:
                ByteBuf frame = buf.readBytes(length) ;
                checkpoint(MyDecoderState.READ_BODY);
                list.add(frame) ;
                break;
            default:
                throw new Error("Shouldn't reach here.") ;
        }
    }

}
