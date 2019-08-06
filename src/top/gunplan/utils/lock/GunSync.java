/*
 * Copyright (c) frankHan personal 2017-2018
 */

package top.gunplan.utils.lock;



public interface GunSync {
    void acquire(int value) throws InterruptedException;

    void release(int value);
}
