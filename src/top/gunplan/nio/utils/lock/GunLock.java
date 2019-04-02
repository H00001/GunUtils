package top.gunplan.nio.utils.lock;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 */
public final class GunLock implements Lock {
    private final GunSync sync;

    public GunLock() {
        sync = new GunUnFareAquire(1);
    }

    @Override
    public void lock() {
        try {
            sync.acquire(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }


    @Override
    public boolean tryLock() {
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void unlock() {
        sync.release(1);
    }

    @Override
    public Condition newCondition() {
        return null;
    }


    public static int val = 0;

    public static void main(String[] args) throws InterruptedException {


         GunUnFareAquire.GunInlineLinkedBlockQueue<Integer> queue = new GunUnFareAquire.GunInlineLinkedBlockQueue<>();
        ArrayList<Integer> arrayList = new ArrayList<>();
        ExecutorService es = Executors.newFixedThreadPool(4);
        for (int j = 0; j < 10; j++) {
            es.submit(() -> {
                for (int i = 0; i < 2000; i++) {
                    //  queue.take(i);
                    arrayList.add(i);
                }
            });
        }
        Thread.sleep(3000);
        int now = 0;
        for (int i = 0; i < 20000; i++) {
            System.out.println(arrayList.get(i) + ":" + now++);
        }

    }
}
