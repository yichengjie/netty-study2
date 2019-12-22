package com.yicj.apis;

import org.msgpack.MessagePack;
import org.msgpack.template.Templates;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MessagePackTest {

    public static void main(String[] args) throws IOException {
        List<String> src = new ArrayList<String>() ;
        src.add("msgpack") ;
        src.add("kumofs") ;
        src.add("viver") ;
        MessagePack msgpack = new MessagePack() ;
        //序列化
        byte[] raw = msgpack.write(src);
        //反序列化
        List<String> dst1 = msgpack.read(raw, Templates.tList(Templates.TString)) ;
        System.out.println(dst1.get(0));
        System.out.println(dst1.get(1));
        System.out.println(dst1.get(2));
    }
}
