package top.gunplan.nio.utils.lock;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

public class GunUnFareAquire implements GunSync {
    private final int flagszie;
    private volatile AtomicReference<Thread> currentThread = new AtomicReference<>();
    private volatile AtomicInteger nowflag = new AtomicInteger(0);
    private GunInlineLinkedBlockQueue<Thread> queue = new GunInlineLinkedBlockQueue<>();

    public GunUnFareAquire(final int flagsize) {
        this.flagszie = flagsize;
    }

    void addWaiter() {
        queue.take(Thread.currentThread());
        LockSupport.park();

    }

    @Override
    public void acquire(int need) throws InterruptedException {
        if (flagszie - nowflag.get() - need > 0) {
            if (currentThread.compareAndSet(null, Thread.currentThread())) {
                nowflag.compareAndSet(nowflag.get(), nowflag.get() - need);
            } else {
                //fast replace fail add waiter
                addWaiter();
                acquire(need);
            }
        } else {
            //add waiter
            addWaiter();
            acquire(need);
        }

    }


    @Override
    public void release(int value) {
        if (currentThread.get() == Thread.currentThread()) {
            if (nowflag.compareAndSet(nowflag.get(), nowflag.get() + value)) {
                currentThread.compareAndSet(Thread.currentThread(), null);
                LockSupport.unpark(queue.get().getVal());
            }
        } else {
            throw new RuntimeException("call invida");
        }
    }

    static class GunInlineLinkedBlockQueue<T> {

        private volatile AtomicReference<GunLinkedListNode<T>> head = new AtomicReference<>(null);
        private volatile GunLinkedListNode<T> tail = null;

        static class GunLinkedListNode<T> {
            private GunLinkedListNode<T> prev;
            private AtomicReference<GunLinkedListNode<T>> next = new AtomicReference<>(null);
            private T val;

            GunLinkedListNode(T currentThread) {
                this.val = currentThread;
            }

            public GunLinkedListNode<T> getPrev() {
                return prev;
            }

            void setPrev(GunLinkedListNode<T> prev) {
                this.prev = prev;
            }

            GunLinkedListNode<T> getNext() {
                return next.get();
            }

            boolean setNext(GunLinkedListNode<T> next) {
                return this.next.compareAndSet(null, next);
            }

            T getVal() {
                return val;
            }

            public void setCurrentThread(T currentThread) {
                this.val = currentThread;
            }
        }

        void take(T node) {
            if (tail == null) {
                if (head.compareAndSet(null, new GunLinkedListNode<>(node))) {
                    tail = head.get();
                } else {
                    /**
                     * The spin to set value
                     */
                    take(node);
                }
            } else {
                final GunLinkedListNode<T> newo = new GunLinkedListNode<>(node);
                if (tail.setNext(newo)) {
                    newo.setPrev(tail);
                    tail = tail.getNext();
                } else {
                    take(node);
                }
            }
        }

        /**
         * you should not call it in a thread envoroment
         *
         * @return GunLinkedListNode<T>
         */
        GunLinkedListNode<T> get() {
            if (head.get() != null) {
                final GunLinkedListNode<T> node = head.get();
                if (head.compareAndSet(head.get(), head.get().getNext())) {
                    if (head.get() == null) {
                        tail = null;
                    }
                    return node;
                } else {
                    return get();
                }
            } else {
                return null;
            }
        }
    }
}
