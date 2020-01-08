package com.yicj.dirruptor.hello.consume;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;
import com.yicj.dirruptor.hello.event.LongEvent;

public class C22EventHandler implements EventHandler<LongEvent>, WorkHandler<LongEvent> {

    @Override
    public void onEvent(LongEvent event, long l, boolean b) throws Exception {
        long number = event.getNumber() ;
        number *= 20 ;
        long t1 = System.currentTimeMillis();
        System.out.println(t1 +": c2-2 EventHandler consumer finished. number = " + number);
    }

    @Override
    public void onEvent(LongEvent event) throws Exception {
        long number = event.getNumber() ;
        number *= 20 ;
        long t1 = System.currentTimeMillis();
        System.out.println(t1 +": c2-2 WorkHandler consumer finished. number = " + number);
    }
}
