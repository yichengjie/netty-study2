package com.yicj.dirruptor.hello.event;

import com.lmax.disruptor.EventFactory;
import com.yicj.dirruptor.hello.event.LongEvent;

public class LongEventFactory implements EventFactory<LongEvent> {
    @Override
    public LongEvent newInstance() {
        return new LongEvent() ;
    }
}
