package com.yicj.dirruptor.hello2.event;

import com.lmax.disruptor.EventFactory;

//通过一个工厂来生产数据(Event)
public class LongEventFactory implements EventFactory<LongEvent> {
    @Override
    public LongEvent newInstance() {
        return new LongEvent();
    }
}
