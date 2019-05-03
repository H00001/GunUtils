package top.gunplan.utils.lock;



public interface GunSync {
    void acquire(int value) throws InterruptedException;

    void release(int value);
}
