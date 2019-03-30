package top.gunplan.nio.utils;

import java.io.IOException;
import java.nio.ByteBuffer;

import java.nio.channels.SocketChannel;

/**
 * @author dosdrtt
 */
public final class GunBytesUtil {
    private static byte[] incrementCopy(byte[] oldbytes, int increment) {
        byte[] newbbytes = new byte[oldbytes.length + increment];
        System.arraycopy(oldbytes, 0, newbbytes, 0, oldbytes.length);
        return newbbytes;
    }


    private static ByteBuffer heapbuff;

    public static void init(int size) {
        heapbuff = ByteBuffer.allocate(size);
    }

    public static byte[] readFromChannel(SocketChannel channel, int increment) throws IOException {
        byte[] save = new byte[increment];
        int nowpoint = 0;
        int maxsize = increment;
        int readlen;
        while ((readlen = channel.read(heapbuff)) > 0) {
            byte[] buffer = heapbuff.array();
            if (maxsize - nowpoint < buffer.length) {
                save = GunBytesUtil.incrementCopy(save, increment);
                maxsize += increment;
            }
            System.arraycopy(buffer, 0, save, nowpoint, buffer.length);
            nowpoint += readlen;
            heapbuff.clear();
        }
        byte[] realsave = new byte[nowpoint];
        System.arraycopy(save, 0, realsave, 0, nowpoint);
        return nowpoint != 0 ? realsave : null;

    }
}
