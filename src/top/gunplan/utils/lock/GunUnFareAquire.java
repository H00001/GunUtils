package top.gunplan.utils.lock;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

public class GunUnFareAquire implements GunSync {
    private final int flagszie;
    private volatile AtomicReference<Thread> currentThread = new AtomicReference<>();
    private volatile AtomicInteger nowflag = new AtomicInteger(0);
    private GunInlineLinkedBlockQueue0<Thread> queue = new GunInlineLinkedBlockQueue0<>();

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
                LockSupport.unpark(queue.get());
            }
        } else {
            throw new RuntimeException("call invida");
        }
    }

    static class GunInlineLinkedBlockQueue0<T> implements GunQueue<T> {

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

        @Override
        public void take(T node) {
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
        @Override
        public T get() {
            if (head.get() != null) {
                final GunLinkedListNode<T> node = head.get();
                if (head.compareAndSet(head.get(), head.get().getNext())) {
                    if (head.get() == null) {
                        tail = null;
                    }
                    return node.val;
                } else {
                    return get();
                }
            } else {
                return null;
            }
        }
    }


    static class GunInlineLinkedBlockQueue1<T> implements GunQueue<T> {

        private volatile GunLinkedListNode<T> head = null;
        private volatile GunLinkedListNode<T> tail = null;
        private AtomicInteger signal = new AtomicInteger(0);

        private static class GunLinkedListNode<T> {
            private GunLinkedListNode<T> prev;
            private GunLinkedListNode<T> next;
            private T val;

            GunLinkedListNode(T currentThread) {
                this.val = currentThread;
                this.prev = null;
                this.next = null;
            }

            public GunLinkedListNode<T> getPrev() {
                return prev;
            }

            void setPrev(GunLinkedListNode<T> prev) {
                this.prev = prev;
            }

            GunLinkedListNode<T> getNext() {
                return next;
            }

            void setNext(GunLinkedListNode<T> next) {
                this.next = next;
            }

            T getVal() {
                return val;
            }

            public void setVal(T val) {
                this.val = val;
            }
        }

        @Override
        public void take(T node) {
            if (tail == null) {
                if (this.signal.compareAndSet(0, 1)) {
                    final GunLinkedListNode<T> nodei = new GunLinkedListNode<>(node);
                    this.head = nodei;
                    this.tail = nodei;
                    this.signal.compareAndSet(1, 0);
                } else {
                    take(node);
                }
            } else {
                if (this.signal.compareAndSet(0, 1)) {
                    final GunLinkedListNode<T> nodei = new GunLinkedListNode<>(node);
                    this.tail.next = nodei;
                    nodei.prev = tail;
                    this.tail = nodei;
                    this.signal.compareAndSet(1, 0);
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
        @Override
        public T get() {
            throw new RuntimeException("hot to implements");
        }
    }
}
