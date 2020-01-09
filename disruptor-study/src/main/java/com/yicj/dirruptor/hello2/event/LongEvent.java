package com.yicj.dirruptor.hello2.event;

import lombok.Data;
import lombok.ToString;

//在disruptor中，Event代表数据，定义携带数据的事件
@ToString
public class LongEvent {
    private long value ;
    public void set(long value){
        this.value = value ;
    }
    public long get(){
        return this.value ;
    }

}
