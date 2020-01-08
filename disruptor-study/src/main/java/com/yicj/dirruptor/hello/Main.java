package com.yicj.dirruptor.hello;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.yicj.dirruptor.hello.consume.C11EventHandler;
import com.yicj.dirruptor.hello.consume.C21EventHandler;
import com.yicj.dirruptor.hello.event.LongEvent;
import com.yicj.dirruptor.hello.event.LongEventFactory;
import com.yicj.dirruptor.hello.event.LongEventTranslator;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class Main {

    public static void main(String[] args) {
        int bufferSize = 1024 * 1024 ;//环形队列长度，必须是2的N次方
        EventFactory<LongEvent> eventFactory = new LongEventFactory() ;
        //定义disruptor，基于单生产者，阻塞策略
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        Disruptor<LongEvent> disruptor = new Disruptor<LongEvent>(
                eventFactory,bufferSize, threadFactory,
                ProducerType.SINGLE,new BlockingWaitStrategy()
        );
        /////////////////////////////////////////////////
        //XXX(disruptor) ; //这里是调用各种不同方法的地方
        parallel(disruptor) ;
        /////////////////////////////////////////////////
        RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer() ;
        //输入10
        ringBuffer.publishEvent(new LongEventTranslator(),10L);
        ringBuffer.publishEvent(new LongEventTranslator(),100L);
        disruptor.shutdown();
    }


    //并行计算实现
    public static void parallel(Disruptor<LongEvent> disruptor){
        disruptor.handleEventsWith(new C11EventHandler(),new C21EventHandler()) ;
        disruptor.start() ;
    }

}
