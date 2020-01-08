package com.yicj.dirruptor.hello.handler;

import com.lmax.disruptor.EventHandler;
import com.yicj.dirruptor.hello.event.LongEvent;

//定义事件处理的具体实现
public class LongEventHandler implements EventHandler<LongEvent> {

    @Override
    public void onEvent(LongEvent event, long l, boolean b) throws Exception {
        System.out.println("Event : " + event);
    }
}
