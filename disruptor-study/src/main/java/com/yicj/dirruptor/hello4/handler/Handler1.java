package com.yicj.dirruptor.hello4.handler;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;
import com.yicj.dirruptor.hello4.event.Trade;

/**
 * 第一个 Handler1，存储到数据库中
 */
public class Handler1 implements EventHandler<Trade>, WorkHandler<Trade> {
      
    @Override  
    public void onEvent(Trade event, long sequence, boolean endOfBatch) throws Exception {
        this.onEvent(event);  
    }  
  
    @Override  
    public void onEvent(Trade event) throws Exception {
        long threadId = Thread.currentThread().getId();     // 获取当前线程id
        String id = event.getId();                          // 获取订单号
        System.out.println(String.format("%s：Thread Id %s 订单信息保存 %s 到数据库中 ....",
                this.getClass().getSimpleName(), threadId, id));
    }  
}  