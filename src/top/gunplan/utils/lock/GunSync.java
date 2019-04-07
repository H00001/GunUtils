package top.gunplan.utils.lock;

import com.sun.corba.se.impl.orbutil.concurrent.Sync;

public interface GunSync {
    void acquire(int value) throws InterruptedException;

    void release(int value);
}
