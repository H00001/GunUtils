package top.gunplan.nio.utils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public final class GunBytesUtil {
    public final static byte[] incrementCopy(byte[] oldbytes, int increment) {
        byte[] newbbytes = new byte[oldbytes.length + increment];
        System.arraycopy(oldbytes, 0, newbbytes, 0, oldbytes.length);
        return newbbytes;
    }


    public static byte[] readFromChannel(SocketChannel channel, int increment) throws IOException {
        ByteBuffer heapbuff = ByteBuffer.allocate(increment);
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
