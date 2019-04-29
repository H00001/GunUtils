package top.gunplan.utils;

import com.sun.istack.internal.NotNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author dosdrtt
 */
public final class GunBytesUtil {
    public static class GunWriteByteUtil {

        public GunWriteByteUtil(byte[] input) {
            this.input = input;
        }

        public int getNowflag() {
            return nowflag;
        }

        public byte[] getInput() {
            return input;
        }

        private int nowflag = 0;
        private byte[] input;

        public void writeByte(byte bin) {
            input[nowflag++] = bin;
        }

        public void write(byte[] save) {
            write(save.length, save);
        }

        public void write(int len, byte[] save) {
            System.arraycopy(save, 0, input, nowflag, len);
            nowflag += len;
        }

        public void write(Boolean save) {
            byte bs = (byte) (save ? 1 : 0);
            writeByte(bs);
        }

        public void write(String save) {
            byte[] bsave = save.getBytes();
            write(save.length(), bsave);
        }

        public void write(int save) {
            writeByte((byte) (save >> 8));
            writeByte((byte) save);
        }

        public void write64(int save) {
            write(save >> 16);
            write(save);
        }
    }


    public static class GunReadByteUtil {
        public GunReadByteUtil(byte[] input) {
            this.output = input;
        }

        private int nowflag = 0;
        private byte[] output;

        public byte readByte() {
            return output[nowflag++];
        }

        public int getNowflag() {
            return nowflag;
        }

        public byte[] getOutput() {
            return output;
        }

        public int readInt() {
            byte[] read = readByte(2);
            int res;
            res = read[0] < 0 ? (0xff & read[0]) : read[0];
            res = res << 8;
            res = res ^ (read[1] < 0 ? (0xff & read[1]) : read[1]);
            return res;
        }

        public short readUByte() {
            return (short) (0xff & readByte());
        }

        public boolean readBool() {
            return readByte() != 0;
        }

        public int readInt64() {
            int left = readInt();
            int right = readInt();
            return (left << 16) ^ right;
        }

        public byte[] readByte(int len) {
            byte[] op = new byte[len];
            System.arraycopy(output, nowflag, op, 0, len);
            nowflag += len;
            return op;
        }
    }

    private static byte[] incrementCopy(byte[] oldbytes, int increment) {
        byte[] newbbytes = new byte[oldbytes.length + increment];
        System.arraycopy(oldbytes, 0, newbbytes, 0, oldbytes.length);
        return newbbytes;
    }


    public static boolean compareBytesFromStart(final byte[] src, @NotNull final byte... b) {
        for (int i = 0; i < b.length; i++) {
            if (src[i] != b[i]) {
                return false;
            }
        }
        return true;
    }

    public static boolean compareBytesFromEnd(final byte[] src, @NotNull final byte... b) {
        for (int i = b.length - 1; i > 0; i--) {
            if (src[src.length - i] != b[b.length - i]) {
                return false;
            }
        }
        return true;
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

    public static byte[] readFromChannel(SocketChannel channel) throws IOException {
        return readFromChannel(channel, 512);
    }
}
