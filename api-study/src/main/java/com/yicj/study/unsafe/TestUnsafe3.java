package com.yicj.study.unsafe;

import com.yicj.study.field.User;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class TestUnsafe3 {
    //获取Unsafe实例
    //记录state在类中的偏移量
    private Long result = 0L;
    public static void main(String[] args) throws Exception {
        test1() ;
    }

    private static void test1() throws Exception {
        //获取成员变量
        Field field = Unsafe.class.getDeclaredField("theUnsafe") ;
        //设置为可访问
        field.setAccessible(true);
        //是静态字段，用hull来获取Unsafe实例
        Unsafe unsafe = (Unsafe)field.get(null) ;
        long stateOffset = unsafe.objectFieldOffset(User.class.getDeclaredField("id")) ;
        User user = new User();
        //执行并返回结果
        /*boolean flag = */
        unsafe.compareAndSwapLong(user, stateOffset, 0L, 4000L);
        //System.out.println("flag : " + flag);
        System.out.println(user);
    }
    private static void test2() throws Exception {
        //获取成员变量
        Field field = Unsafe.class.getDeclaredField("theUnsafe") ;
        //设置为可访问
        field.setAccessible(true);
        //是静态字段，用hull来获取Unsafe实例
        Unsafe unsafe = (Unsafe)field.get(null) ;
        long stateOffset = unsafe.objectFieldOffset(TestUnsafe3.class.getDeclaredField("result")) ;
        TestUnsafe3 user = new TestUnsafe3();
        //执行并返回结果
        boolean flag = unsafe.compareAndSwapLong(user, stateOffset, 0L, 4000L);
        System.out.println("flag : " + flag);
        System.out.println("user : " + user.result);
    }
}
