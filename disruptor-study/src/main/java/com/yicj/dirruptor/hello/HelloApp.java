package com.yicj.dirruptor.hello;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.yicj.dirruptor.hello.event.LongEvent;
import com.yicj.dirruptor.hello.event.LongEventFactory;
import com.yicj.dirruptor.hello.handler.LongEventHandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HelloApp {

    public static void main(String[] args) {

    }


    //启动 Disruptor
    public void startDisruptor(){
        //定义用于事件处理的线程池
        //ExecutorService executor = Executors.newCachedThreadPool();
        //启动Disruptor
        EventFactory<LongEvent> eventFactory = new LongEventFactory() ;
        ExecutorService executor = Executors.newSingleThreadExecutor() ;
        int ringBufferSize = 1024 * 1024 ;// RingBuffer 大小，必须是2的N次方
        Disruptor<LongEvent> disruptor = new Disruptor<LongEvent>(eventFactory,
                ringBufferSize,executor, ProducerType.SINGLE,new YieldingWaitStrategy() );
        EventHandler<LongEvent> eventHandler = new LongEventHandler() ;
        disruptor.handleEventsWith(eventHandler) ;
        disruptor.start() ;
        disruptor.shutdown();
        executor.shutdown();
    }

    //发布事件
    public void publishEvent(Disruptor<LongEvent> disruptor,LongEvent e){
        RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();
        long sequence = ringBuffer.next();//请求下一个事件序号
        try {
            LongEvent event = ringBuffer.get(sequence);
            long data = e.getValue() ;//获取要通知事件传递的业务数据
            event.setValue(data);
        }finally {
            ringBuffer.publish(sequence);
        }


    }


}
