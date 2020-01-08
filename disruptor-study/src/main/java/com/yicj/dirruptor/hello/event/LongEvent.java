package com.yicj.dirruptor.hello.event;

import lombok.Data;

@Data
//事件(Event)就是通过 Disruptor 进行交换的数据类型
public class LongEvent {
    private long number;
}