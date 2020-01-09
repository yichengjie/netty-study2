package com.yicj.dirruptor.hello2.component;

import com.lmax.disruptor.RingBuffer;
import com.yicj.dirruptor.hello2.event.LongEvent;

import java.nio.ByteBuffer;

public class LongEventProducer {
    private final RingBuffer<LongEvent> ringBuffer ;
    public LongEventProducer(RingBuffer<LongEvent> ringBuffer){
        this.ringBuffer = ringBuffer ;
    }

    //发布事件
    public void onData(ByteBuffer bb){
        long sequence = ringBuffer.next() ;//获取下一个序号
        try {
            //获取disruptor的sequence
            LongEvent event = ringBuffer.get(sequence);
            //设置事件Context
            event.set(bb.getLong(0));
        }finally {
            ringBuffer.publish(sequence);
        }
    }

}
