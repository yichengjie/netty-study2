package com.yicj.dirruptor.hello4;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.EventHandlerGroup;
import com.lmax.disruptor.dsl.ProducerType;
import com.yicj.dirruptor.hello4.event.Trade;
import com.yicj.dirruptor.hello4.handler.Handler1;
import com.yicj.dirruptor.hello4.handler.Handler2;
import com.yicj.dirruptor.hello4.handler.Handler3;
import com.yicj.dirruptor.hello4.producer.TradePublisher;

//https://www.cnblogs.com/binarylei/p/9221560.html
/**
 * Handler1：Thread Id 10 订单信息保存 62e86a8c-1c05-4803-95a2-2178d9e15c65 到数据库中 ....
 * Handler2：Thread Id 11 订单信息 62e86a8c-1c05-4803-95a2-2178d9e15c65 发送到 karaf 系统中 ....
 * Handler3：Thread Id 12 订单信息 62e86a8c-1c05-4803-95a2-2178d9e15c65 处理中 ....
 * 总耗时:859
 * 可以看到 Handler3 在 Handler1 和 Handler2 执行完成后才执行。
 */
public class Main {  
    public static void main(String[] args) throws InterruptedException {  
       
        long beginTime=System.currentTimeMillis();  
        int bufferSize=1024;  
        ExecutorService executor=Executors.newFixedThreadPool(8);

        Disruptor<Trade> disruptor = new Disruptor<>(new EventFactory<Trade>() {
            @Override  
            public Trade newInstance() {  
                return new Trade();  
            }  
        }, bufferSize, executor, ProducerType.SINGLE, new BusySpinWaitStrategy());  
        //菱形操作
        //使用disruptor创建消费者组C1,C2  
        EventHandlerGroup<Trade> handlerGroup = 
                disruptor.handleEventsWith(new Handler1(), new Handler2());
        //声明在C1,C2完事之后执行JMS消息发送操作 也就是流程走到C3 
        handlerGroup.then(new Handler3());
        disruptor.start();//启动
        CountDownLatch latch=new CountDownLatch(1);  
        //生产者准备  
        executor.submit(new TradePublisher(latch, disruptor));
        latch.await();//等待生产者完事.
        disruptor.shutdown();
        executor.shutdown();  
        System.out.println("总耗时:"+(System.currentTimeMillis()-beginTime));  
    }  
}