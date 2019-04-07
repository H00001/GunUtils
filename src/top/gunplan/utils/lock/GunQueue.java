package top.gunplan.utils.lock;

public interface GunQueue<T> {
    void take(T node);

    T get();
}
