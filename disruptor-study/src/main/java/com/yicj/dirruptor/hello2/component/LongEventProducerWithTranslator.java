package com.yicj.dirruptor.hello2.component;

import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;
import com.yicj.dirruptor.hello2.event.LongEvent;

import java.nio.ByteBuffer;

//
public class LongEventProducerWithTranslator {

    private final RingBuffer<LongEvent> ringBuffer ;
    public LongEventProducerWithTranslator(RingBuffer<LongEvent> ringBuffer){
        this.ringBuffer = ringBuffer ;
    }
    private static final EventTranslatorOneArg<LongEvent, ByteBuffer> TRANSLATOR =
            new EventTranslatorOneArg<LongEvent, ByteBuffer>() {
                @Override
                public void translateTo(LongEvent event, long l, ByteBuffer bb) {
                    event.set(bb.get(0));
                }
            } ;
    public void onData(ByteBuffer bb){
        ringBuffer.publishEvent(TRANSLATOR,bb);
    }
}
