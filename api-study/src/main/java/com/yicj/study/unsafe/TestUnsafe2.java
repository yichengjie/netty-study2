package com.yicj.study.unsafe;

import com.yicj.study.field.User;
import sun.misc.Unsafe;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TestUnsafe2 {


    public static void main(String[] args) throws Exception{
        Field field = Unsafe.class.getDeclaredField("theUnsafe");
        field.setAccessible(true);
        Unsafe unsafe = (Unsafe)field.get(null);
        //testOperArray(unsafe) ;
        testOperObject6(unsafe) ;
    }

    //操作数组:
    // 可以获取数组的在内容中的基本偏移量（arrayBaseOffset），获取数组内元素的间隔（比例），
    // 根据数组对象和偏移量获取元素值（getObject），设置数组元素值（putObject），示例如下
    private static void testOperArray(Unsafe unsafe){
        Integer [] ints = {4,5,6} ;
        long i = unsafe.arrayBaseOffset(Integer[].class);
        System.out.println("string[] base offset is : " +i);
        //every index scale
        int scale = unsafe.arrayIndexScale(Integer[].class);

        System.out.println("string[] index scale is " + scale);
        //print first string in string[]
        System.out.println("first element in : " + unsafe.getObject(ints,i));
        //set 100 to first string
        unsafe.putObject(ints,i + scale * 0 , "a");
        //print first string in strings[] again
        System.out.println("after set , first element : " + unsafe.getObject(ints,i + scale * 0));
    }

    /**
     * 对象操作
     * 实例化Data
     * 可以通过类的class对象创建类对象（allocateInstance），获取对象属性的偏移量（objectFieldOffset）
     * ，通过偏移量设置对象的值（putObject）
     * 对象的反序列化
     * 当使用框架反序列化或者构建对象时，会假设从已存在的对象中重建，你期望使用反射来调用类的设置函数，
     * 或者更准确一点是能直接设置内部字段甚至是final字段的函数。问题是你想创建一个对象的实例，
     * 但你实际上又不需要构造函数，因为它可能会使问题更加困难而且会有副作用。
     */
    //调用allocateInstance函数避免了在我们不需要构造函数的时候却调用它
    private static void testOperObject1(Unsafe unsafe) throws Exception{
        //调用allocateInstance函数避免了在我们不需要构造函数的时候却调用它
        User user = (User) unsafe.allocateInstance(User.class);
        user.setId(1L);
        user.setName("yicj");
        System.out.println(user);
    }

    //返回成员属性在内存中的地址相对于对象内存地址的偏移量
    private static void testOperObject2(Unsafe unsafe) throws Exception{
        User user = new User(1L,"yicj") ;
        Field idField = User.class.getDeclaredField("id");
        long idFieldOffset = unsafe.objectFieldOffset(idField);
        Field nameField = User.class.getDeclaredField("name");
        long nameFieldOffset = unsafe.objectFieldOffset(nameField);
        //putLong，putInt，putDouble，putChar，putObject等方法，直接修改内存数据（可以越过访问权限）
        unsafe.putObject(user,idFieldOffset,"这个是新的值");
        unsafe.putObject(user,nameFieldOffset,"这个是新的值");
        System.out.println(user);
    }

    //我们可以在运行时创建一个类，比如从已编译的.class文件中。将类内容读取为字节数组，
    //并正确地传递给defineClass方法；当你必须动态创建类，而现有代码中有一些代理， 这是很有用的
    private static void testOperObject3(Unsafe unsafe) throws Exception{
        String path ="D:\\code\\User.class" ;
        File file = new File(path) ;
        FileInputStream input = new FileInputStream(file) ;
        byte[] content = new byte[(int)file.length()] ;
        input.read(content) ;
        Class c = unsafe.defineClass(null,content,0,content.length,null,null) ;
        System.out.println(c.getName());
        Object val = c.getMethod("getId").invoke(c.newInstance(), null);
        System.out.println(val);
    }

    //内存操作
    //可以在Java内存区域中分配内存（allocateMemory），设置内存（setMemory，用于初始化），
    // 在指定的内存位置中设置值（putInt\putBoolean\putDouble等基本类型）
    private static void testOperObject4(Unsafe unsafe) throws Exception{
        //分配一个8byte的内存
        long address = unsafe.allocateMemory(8L);
        //初始化内存填充数据
        unsafe.setMemory(address,8L,(byte)1);
        //测试输出
        System.out.println("add byte to memory: " + unsafe.getInt(address));
        //设置0-3 4个byte为0x7fff ffff
        unsafe.putInt(address,0x7fffffff);
        //设置4-7 4个byte为0x8000 0000
        unsafe.putInt(address + 4, 0x80000000);
        //int 占用4个byte
        System.out.println("add byte memory : " + unsafe.getInt(address));
        System.out.println("add byte memory : " + unsafe.getInt(address + 4));
    }

    //CAS操作
    // Compare And Swap（比较并交换），当需要改变的值为期望的值时，那么就替换它为新的值，是原子
    //（不可在分割）的操作。很多并发框架底层都用到了CAS操作，CAS操作优势是无锁，可以减少线程切换耗费
    // 的时间，但CAS经常失败运行容易引起性能问题，也存在ABA问题。在Unsafe中包含compareAndSwapObject、
    // compareAndSwapInt、compareAndSwapLong三个方法，compareAndSwapInt的简单示例如下。
    private static void testOperObject5(Unsafe unsafe) throws Exception{
        User data = new User();
        data.setId(1L);
        Field id = data.getClass().getDeclaredField("id");
        long l = unsafe.objectFieldOffset(id);
        id.setAccessible(true);
        //比较并交换，比如id的值如果是所期望的值1，那么就替换为2，否则不做处理
        unsafe.compareAndSwapLong(data,l,1L,2L);
        System.out.println(data);
    }

    //常量获取
    // * 可以获取地址大小（addressSize），页大小（pageSize），基本类型数组的偏移量
    // * （Unsafe.ARRAY_INT_BASE_OFFSET\Unsafe.ARRAY_BOOLEAN_BASE_OFFSET等）、
    // * 基本类型数组内元素的间隔（Unsafe.ARRAY_INT_INDEX_SCALE\Unsafe.ARRAY_BOOLEAN_INDEX_SCALE等）
    private static void testOperObject6(Unsafe unsafe) throws Exception{
        //get os address size
        System.out.println("address size is : " + unsafe.addressSize());
        //get os page size
        System.out.println("page size is : " + unsafe.pageSize());
        //int array base offset
        System.out.println("unsafe array int base offset : " + Unsafe.ARRAY_INT_BASE_OFFSET);
    }

    //线程许可
    // * 许可线程通过（park），或者让线程等待许可(unpark)
    private static void testOperObject7(Unsafe unsafe) throws Exception{
        Thread packThread = new Thread(()->{
            long startTime = System.currentTimeMillis() ;
            //纳秒，相对时间park
            unsafe.park(false,3000000000L);
            //毫秒，绝对时间park
            long endTime = System.currentTimeMillis() ;
            System.out.println("main thread end ,cost : " + (endTime - startTime) +"ms");
        }) ;
        packThread.start();
        TimeUnit.SECONDS.sleep(1);
        //注释掉下一行后，线程3秒后进行输出，否则1秒后输出
        unsafe.unpark(packThread);
    }

}
