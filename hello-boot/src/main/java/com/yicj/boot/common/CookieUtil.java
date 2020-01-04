package com.yicj.boot.common;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public class CookieUtil {

    //压缩
    private void compressCookie(Cookie c , HttpServletResponse res){
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream() ;
            DeflaterOutputStream dos = new DeflaterOutputStream(bos) ;
            dos.write(c.getValue().getBytes());
            dos.close();
            System.out.println("before compress length : " + c.getValue().getBytes().length);
            String compress = Base64.getEncoder().encodeToString(bos.toByteArray()) ;
            res.addCookie(new Cookie("compress",compress));
            System.out.println("after compress length : " + compress.getBytes().length);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    //加压缩
    private void unCompressCookie(Cookie c){
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte [] compress = Base64.getDecoder().decode(c.getValue().getBytes()) ;
            ByteArrayInputStream bis = new ByteArrayInputStream(compress) ;
            InflaterInputStream inflater = new InflaterInputStream(bis) ;
            byte [] b = new byte[1024] ;
            int count ;
            while ((count = inflater.read(b)) >= 0){
                out.write(b,0,count);
            }
            inflater.close();
            System.out.println(out.toByteArray());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
