package com.yicj.dirruptor.hello;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.yicj.dirruptor.hello.consume.C11EventHandler;
import com.yicj.dirruptor.hello.consume.C12EventHandler;
import com.yicj.dirruptor.hello.consume.C21EventHandler;
import com.yicj.dirruptor.hello.consume.C22EventHandler;
import com.yicj.dirruptor.hello.event.LongEvent;
import com.yicj.dirruptor.hello.event.LongEventFactory;
import com.yicj.dirruptor.hello.event.LongEventTranslator;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        int bufferSize = 1024 * 1024 ;//环形队列长度，必须是2的N次方
        EventFactory<LongEvent> eventFactory = new LongEventFactory() ;
        //定义disruptor，基于单生产者，阻塞策略
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Disruptor<LongEvent> disruptor = new Disruptor<LongEvent>(eventFactory,
                bufferSize, threadFactory, ProducerType.SINGLE,
                new YieldingWaitStrategy());
        /////////////////////////////////////////////////
        //XXX(disruptor) ; //这里是调用各种不同方法的地方
        //parallel(disruptor) ;
        //serial(disruptor) ;
        //parallelWithPool(disruptor) ;
        serialWithPool(disruptor) ;
        /////////////////////////////////////////////////
        RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer() ;
        //输入10
        ringBuffer.publishEvent(new LongEventTranslator(),10L);
        //ringBuffer.publishEvent(new LongEventTranslator(),100L);
        Thread.sleep(3);
        disruptor.shutdown();
    }


    //并行计算实现
    public static void parallel(Disruptor<LongEvent> disruptor){
        disruptor.handleEventsWith(new C11EventHandler(),new C21EventHandler()) ;
        disruptor.start() ;
    }
    //串行计算，依次执行
    public static void serial(Disruptor<LongEvent> disruptor){
        disruptor.handleEventsWith(new C11EventHandler()).then(new C21EventHandler()) ;
        disruptor.start() ;
    }
    //菱形方式执行
    public static void diamond(Disruptor<LongEvent> disruptor){
        disruptor.handleEventsWith(new C11EventHandler(),new C12EventHandler()).then(new C21EventHandler()) ;
        disruptor.start() ;
    }

    public static void chain(Disruptor<LongEvent> disruptor){
        disruptor.handleEventsWith(new C11EventHandler()).then(new C12EventHandler()) ;
        disruptor.handleEventsWith(new C21EventHandler()).then(new C22EventHandler()) ;
        disruptor.start() ;
    }

    //同一个类的实例消费不同的数据
    public static void parallelWithPool(Disruptor<LongEvent> disruptor){
        disruptor.handleEventsWithWorkerPool(new C11EventHandler(),new C11EventHandler()) ;
        disruptor.handleEventsWithWorkerPool(new C21EventHandler(),new C21EventHandler()) ;
        disruptor.start() ;
    }

    public static void serialWithPool(Disruptor<LongEvent> disruptor){
        disruptor.handleEventsWithWorkerPool(new C11EventHandler(),new C11EventHandler())
                .then(new C21EventHandler(),new C21EventHandler());
        disruptor.start() ;
    }
}
