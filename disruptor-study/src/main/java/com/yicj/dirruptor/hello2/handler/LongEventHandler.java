package com.yicj.dirruptor.hello2.handler;

import com.lmax.disruptor.EventHandler;
import com.yicj.dirruptor.hello2.event.LongEvent;

//在定义了Event之后我们需要创建一个消费者来处理这些事件，也是一个事件处理器
public class LongEventHandler implements EventHandler<LongEvent> {
    private String clientName;
    public LongEventHandler(String clientName){
        this.clientName = clientName ;
    }
    //事件监听，类似观察者模式
    @Override
    public void onEvent(LongEvent event, long l, boolean b) throws Exception {
        System.out.println(this.clientName + event.get());
        event.get() ;
    }
}
