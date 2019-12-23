package com.yicj.chapter7_1.entity;

import lombok.Data;
import org.msgpack.annotation.Message;

//注意使用MessagePack时序列化的对象一定要注解@Message,否则无法传递数据
@Data
@Message
public class UserInfo {
    private String userName ;
    private int userID ;
    private int age ;
}
