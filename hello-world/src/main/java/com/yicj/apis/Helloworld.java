package com.yicj.apis;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Helloworld {


    interface AAACallback{
        void callback() ;
    }

    //client
    public static void main(String[] args) throws Exception {
        Helloworld helloworld = new Helloworld() ;
    }


    public  Helloworld test1(AAACallback callback){
        ByteBuf sourceBuf = Unpooled.buffer(16) ;
        ByteBuf destBuf = Unpooled.buffer(12) ;
        for(int i = 0 ; i < sourceBuf.capacity() ; i++){
            sourceBuf.writeByte(i + 1) ;
        }
        //从sourceBuf中读取数据到destBuf
        sourceBuf.readBytes(destBuf) ;
        System.out.println(sourceBuf.toString());
        System.out.println(destBuf.toString());
        for (int i = 0 ; i< destBuf.capacity() ; i++){
            System.out.print(destBuf.getByte(i) +", ");
        }
        if(callback !=null){
            callback.callback();
        }
        return this ;
    }




}
