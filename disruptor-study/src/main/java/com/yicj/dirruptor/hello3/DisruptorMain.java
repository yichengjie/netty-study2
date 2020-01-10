package com.yicj.dirruptor.hello3;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
 
public class DisruptorMain {
    public static void main(String[] args){
        int ringBufferSize = 1024 * 1024;
        //ExecutorService executor = Executors.newFixedThreadPool(3);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        //WaitStrategy blockingWaitStrategy = new BlockingWaitStrategy();
        //WaitStrategy sleepingWaitStrategy = new SleepingWaitStrategy();
        WaitStrategy yieldingWaitStrategy = new YieldingWaitStrategy();
        EventFactory<HelloEvent> eventFactory = new HelloEventFactory();
        Disruptor<HelloEvent> disruptor = new Disruptor<>(eventFactory,
                        ringBufferSize, threadFactory, ProducerType.SINGLE
                        , yieldingWaitStrategy);
        EventHandler<HelloEvent> eventHandler = new HelloEventHandler();
        disruptor.handleEventsWith(eventHandler);
        disruptor.start();
        RingBuffer<HelloEvent> ringBuffer = disruptor.getRingBuffer();
        HelloEventProducer producer = new HelloEventProducer(ringBuffer);
        for(long l = 0; l<100; l++){
            producer.onData("yicj：Hello World！！！:" + l);
        }
        disruptor.shutdown();
    }
}