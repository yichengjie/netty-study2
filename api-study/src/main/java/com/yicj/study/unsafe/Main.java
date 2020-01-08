package com.yicj.study.unsafe;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class Main {
    private String s ;
    private long s1 ;
    private long s2 ;

    public static void main(String[] args) throws Exception{
        Main main = new Main() ;
        main.s1 = 20 ;
        long s ,next ;
        long state = 256 ;
        System.out.println(1 << 6);
        System.out.println(1 << 10);
        System.out.println(1L << 7);
        System.out.println("Hello World");
        long except = 1 ;
        long real = 1 ;
        Unsafe unsafe = null ;
        Field theUnsafeInstance = Unsafe.class.getDeclaredField("theUnsafe") ;
        theUnsafeInstance.setAccessible(true);
        unsafe = (Unsafe) theUnsafeInstance.get(null) ;
        System.out.println("====================");
        Field[] fields = Main.class.getDeclaredFields();

        long s2offset = unsafe.objectFieldOffset(Main.class.getDeclaredField("s2"));
        System.out.println("s2offset :" + s2offset  );
        /*for (Field field : fields){
            System.out.println(field.getName() +"====> " + unsafe.objectFieldOffset(field));
        }*/
        /**
         * s====> 24
         * s1====> 8
         * s2====> 16
         */
        System.out.println("====================");
        boolean flag = unsafe.compareAndSwapLong(main, s2offset, 0, 21);
        System.out.println(main.s2);
    }
}
