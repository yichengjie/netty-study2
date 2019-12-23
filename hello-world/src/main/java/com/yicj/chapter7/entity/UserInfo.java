package com.yicj.chapter7.entity;

import lombok.Data;
import org.msgpack.annotation.Message;

@Data
@Message
public class UserInfo {
    private String userName ;
    private int userID ;
    private int age ;
}
