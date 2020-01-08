package com.yicj.study.unsafe;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * Java数组大小的最大值为Integer.MAX_VALUE。使用直接内存分配，我们创建的数组大小受限于堆大小；
 *  * 实际上，这是堆外内存（off-heap memory）技术，在java.nio包中部分可用；
 *  * 这种方式的内存分配不在堆上，且不受GC管理，所以必须小心Unsafe.freeMemory()的使用。
 *  * 它也不执行任何边界检查，所以任何非法访问可能会导致JVM崩溃
 */
public class SuperArray {
    private static Unsafe unsafe = null;
    private static Field getUnsafe = null;
    static {
        try {
            getUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            getUnsafe.setAccessible(true);
            unsafe = (Unsafe) getUnsafe.get(null);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    private final static int BYTE = 1;
    private long size;
    private long address;
    public SuperArray(long size) {
        this.size = size;
        address = unsafe.allocateMemory(size * BYTE);
    }

    public void set(long i, byte value) {
        unsafe.putByte(address + i * BYTE, value);
    }

    public int get(long idx) {
        return unsafe.getByte(address + idx * BYTE);
    }

    public long size() {
        return size;
    }

    public static void main(String[] args) {
        long SUPER_SIZE = (long) Integer.MAX_VALUE * 2;
        SuperArray array = new SuperArray(SUPER_SIZE);
        System.out.println("Array size:" + array.size()); // 4294967294
        int sum = 0;
        for (int i = 0; i < 100; i++) {
            array.set((long) Integer.MAX_VALUE + i, (byte) 3);
            sum += array.get((long) Integer.MAX_VALUE + i);
        }
        System.out.println(sum);
    }
}
