package com.yicj.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class Helloworld {

    public static void main(String[] args) {
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

    }

}
