package com.yicj.dirruptor.hello.event;

import com.lmax.disruptor.EventTranslatorOneArg;

//事件转换类
public class LongEventTranslator implements EventTranslatorOneArg<LongEvent,Long> {

    @Override
    public void translateTo(LongEvent event, long sequence, Long arg0) {
        event.setNumber(arg0);
    }
}
