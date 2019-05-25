package top.gunplan.utils.lock;

import java.util.Date;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;

public class GunReLock implements Lock {
    private volatile AtomicReference<Thread> owner = new AtomicReference<>(null);
    private volatile ConcurrentLinkedQueue<Thread> waitqueue = new ConcurrentLinkedQueue<>();

    @Override
    public void lock() {
        if (tryLock()) {
            //快速获取锁成功
        } else {
            //#1
            waitqueue.offer(Thread.currentThread());
            if (tryLock()) {
                //再一次尝试获取锁，防止unlock快速在#1过程中快速释放
                waitqueue.remove(Thread.currentThread());
                //获取到锁，删除在队列中的线程
                return;
            } else {
                //如果unlock在这里快速释放，queue 已经存在当前线程，
                // 那么因为park的顺序无关性，所以并不会出现无尽等待
                // 能够被成功唤醒
                LockSupport.park();
            }
            lock();
            //获取锁失败
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {
        return owner.compareAndSet(null, Thread.currentThread());
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        long now = System.currentTimeMillis();
        final long stoptime = unit.toNanos(time); //获取所需要时间
        while (System.currentTimeMillis() - now < stoptime) {
            if (tryLock()) {
                return true;
            }
            TimeUnit.MICROSECONDS.sleep(200);
        }
        return false;
    }

    @Override
    public void unlock() {
        if (owner.compareAndSet(Thread.currentThread(), null)) {
            LockSupport.unpark(waitqueue.poll());
        } else {
            //释放锁失败,只有一种可能，当前线程不持有锁
        }
    }

    @Override
    public Condition newCondition() {
        return new GunCondition();
    }

    final class GunCondition implements Condition {

        @Override
        public void await() throws InterruptedException {
            unlock();
            waitqueue.offer(Thread.currentThread());
            LockSupport.park();
            lock();
        }

        @Override
        public void awaitUninterruptibly() {

        }

        @Override
        public long awaitNanos(long nanosTimeout) throws InterruptedException {
            return 0;
        }

        @Override
        public boolean await(long time, TimeUnit unit) throws InterruptedException {
            return false;
        }

        @Override
        public boolean awaitUntil(Date deadline) throws InterruptedException {
            return false;
        }

        @Override
        public void signal() {
            LockSupport.unpark(waitqueue.poll());
        }

        @Override
        public void signalAll() {
            waitqueue.parallelStream().forEach(LockSupport::unpark);
            waitqueue.clear();
        }
    }
}
