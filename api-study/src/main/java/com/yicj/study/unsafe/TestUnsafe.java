package com.yicj.study.unsafe;

import com.yicj.study.field.User;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class TestUnsafe {
    //获取Unsafe实例
    static final Unsafe unsafe ;
    //记录state在类中的偏移量
    static final long stateOffset ;
    //变量
    public volatile long result = 0;
    public int [] arr = {1,2,3,4,5,6} ;
    static {
        try {
            //获取成员变量
            Field field = Unsafe.class.getDeclaredField("theUnsafe") ;
            //设置为可访问
            field.setAccessible(true);
            //是静态字段，用hull来获取Unsafe实例
            unsafe = (Unsafe)field.get(null) ;
            //获取state变量在类中的偏移量
            stateOffset = unsafe.objectFieldOffset(TestUnsafe.class.getDeclaredField("result")) ;
        }catch (Exception e){
            throw new Error(e) ;
        }
    }

    public static void main(String[] args) {
        TestUnsafe testUnsafe=new TestUnsafe();
        //执行并返回结果
        for(int i=0;i<1000;i++){
            unsafe.getAndAddLong(testUnsafe,stateOffset ,3L);
        }
        System.out.println(testUnsafe.result);

        System.out.println(unsafe.arrayBaseOffset(testUnsafe.arr.getClass()));

        System.out.println(unsafe.arrayIndexScale(testUnsafe.arr.getClass()));
        System.out.println(unsafe.compareAndSwapLong(testUnsafe, stateOffset, 3000, 4000));
        System.out.println(unsafe.getLongVolatile(testUnsafe, stateOffset));
        unsafe.putLongVolatile(testUnsafe, stateOffset, 5000);
        System.out.println(testUnsafe.result);
        unsafe.putOrderedLong(testUnsafe, stateOffset, 5500);
        System.out.println(testUnsafe.result);
        Thread thread1 = new Thread(){
            public void run(){
                System.out.println("线程1开始沉睡");
                long start=System.currentTimeMillis();
                long end=System.currentTimeMillis()+8000;
                unsafe.park(true,end); //阻塞当前线程一段时间
                System.out.println("线程1在"+(System.currentTimeMillis()-start)+"ms后被线程2唤醒");
            }
        };
        Thread thread2=new Thread(){
            public void run(){
                try {
                    sleep(3000);
                    unsafe.unpark(thread1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread1.start();
        thread2.start();
        System.out.println("xxxxxxxxxxxxxxxxxxxx");
    }
}
