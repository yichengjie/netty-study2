package com.yicj.chapter12_1.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MyProtocolBean {
    //类型： 系统编码 0xA表示A系统，0xB表示B系统
    private byte type ;
    //信息标志 0xA: 心跳包, 0xB:超时包， 0xC:业务信息包
    private byte flag ;
    //内容长度
    private int length ;
    //内容
    private String body ;
}
