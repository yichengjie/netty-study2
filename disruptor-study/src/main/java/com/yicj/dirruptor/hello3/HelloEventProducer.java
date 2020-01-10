package com.yicj.dirruptor.hello3;

import com.lmax.disruptor.RingBuffer;

public class HelloEventProducer {
    private final RingBuffer<HelloEvent> ringBuffer;
    public HelloEventProducer(RingBuffer<HelloEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }
    /**
     * onData用来发布事件，每调用一次就发布一次事件
     * 它的参数会用过事件传递给消费者
     */
    public void onData(String str) {
        long sequence = ringBuffer.next();
        //System.out.println(sequence);
        try {
            HelloEvent event = ringBuffer.get(sequence);
            event.setValue(str);
        } finally {
            ringBuffer.publish(sequence);
        }
    }
}