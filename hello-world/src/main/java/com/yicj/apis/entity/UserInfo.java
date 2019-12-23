package com.yicj.apis.entity;

import java.io.Serializable;
import java.nio.ByteBuffer;

public class UserInfo implements Serializable {
    private static final long serialVersionUID = 1L ;
    private String userName ;
    private int userID ;
    private int age ;


    public UserInfo buildUserName(String userName){
        this.userName = userName ;
        return this ;
    }
    public UserInfo buildUserID(int userID) {
        this.userID = userID;
        return this ;
    }

    public final String getUserName() {
        return userName;
    }

    public final void setUserName(String userName) {
        this.userName = userName;
    }

    public final int getUserID() {
        return userID;
    }

    public final void setUserID(int userID) {
        this.userID = userID;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public byte[] codeC(){
        ByteBuffer buffer = ByteBuffer.allocate(1024) ;
        byte[] value = this.userName.getBytes() ;
        buffer.putInt(value.length) ;
        buffer.put(value) ;
        buffer.putInt(this.userID) ;
        buffer.flip();
        int len = buffer.remaining() ;
        byte [] result = new byte[len] ;
        buffer.get(result) ;
        return result ;
    }
}