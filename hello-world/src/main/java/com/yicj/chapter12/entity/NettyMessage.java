package com.yicj.chapter12.entity;

import lombok.Data;

@Data
public class NettyMessage {
    private Header header ; //消息头
    private Object body ; // 消息体
}
