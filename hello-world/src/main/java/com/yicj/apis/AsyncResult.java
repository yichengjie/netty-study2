package com.yicj.apis;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AsyncResult  implements IAsyncResult{

    private byte [] result ;
    private AtomicBoolean done = new AtomicBoolean(false) ;
    private Lock lock = new ReentrantLock() ;
    private Condition condition ;
    private long startTime ;
    public AsyncResult(){
        condition = lock.newCondition() ;//创建一个锁
        startTime = System.currentTimeMillis() ;
    }

    //检查需要的数据是否已经返回，如果没有返回，阻塞
    public byte [] get(){
        lock.lock();
        try {
            if(!done.get()){
                condition.await();
            }
        }catch (InterruptedException ex){
            throw new AssertionError(ex) ;
        }finally {
            lock.unlock();
        }
        return result ;
    }

    //检查需要的数据是否已经返回
    public boolean isDone(){
        return done.get() ;
    }

    public byte [] get(long timeout, TimeUnit tu)throws TimeoutException {
        lock.lock();
        try {
            boolean bVal = true ;
            try {
                if(!done.get()){
                    long overallTime = timeout - (System.currentTimeMillis() - startTime) ;
                    if(overallTime > 0){//设置等待超时的时间
                        bVal = condition.await(overallTime,tu) ;
                    }else {
                        bVal = false ;
                    }
                }
            }catch (InterruptedException ex){
                throw new AssertionError(ex) ;
            }
            if(!bVal && !done.get()){//抛出超时异常
                throw new TimeoutException("Operation time out.") ;
            }
        }finally {
            lock.unlock();
        }
        return result ;
    }

    public void result(byte [] info){
        try {
            lock.lock();
            if(!done.get()){
                result = info ;
                done.set(true);
                condition.signal();
            }
        }finally {
            lock.unlock();
        }
    }
}
