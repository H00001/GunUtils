package top.gunplan.utils;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author dosdrtt
 */
public final class GunBytesUtil {
    public static boolean compareBytesFromStart(final byte[] src, final byte... b) {
        for (int i = 0; i < b.length; i++) {
            if (src[i] != b[i]) {
                return false;
            }
        }
        return true;
    }

    public static boolean compareBytesFromEnd(final byte[] src, final byte... b) {
        for (int i = b.length - 1; i > 0; i--) {
            if (src[src.length - i] != b[b.length - i]) {
                return false;
            }
        }
        return true;
    }

    private static byte[] incrementCopy(byte[] oldbytes, int increment) {
        byte[] newbbytes = new byte[oldbytes.length + increment];
        System.arraycopy(oldbytes, 0, newbbytes, 0, oldbytes.length);
        return newbbytes;
    }


    private static ByteBuffer heapbuff;

    private static byte[] readFromChannel(SocketChannel channel, int increment) throws IOException {
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

    public static void init(int size) {
        heapbuff = ByteBuffer.allocate(size);
    }

    public interface GunExecByteUtil {

        void pAdd2();

        void pSub2();

        default int getLenSum() {
            return -1;
        }

        default int getNowflag() {
            return -1;
        }
    }

    public static class GunWriteByteStream implements GunExecByteUtil {


        public GunWriteByteStream(byte[] input) {
            this.input = input;
        }

        @Override
        public int getNowflag() {
            return nowflag;
        }

        public byte[] getInput() {
            return input;
        }

        @Override
        public void pAdd2() {
            nowflag += 2;
        }

        @Override
        public void pSub2() {
            nowflag -= 2;
        }

        @Override
        public int getLenSum() {
            return input.length;
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

        public void write32(int save) {
            write(save >> 16);
            write(save);
        }

        public void writeLong(long save) {
            write32((int) (save >> 32));
            write32((int) save);

        }
    }

    public static byte[] readFromChannel(SocketChannel channel) throws IOException {
        return readFromChannel(channel, 512);
    }

    public static class GunReadByteStream implements GunExecByteUtil {
        public GunReadByteStream(byte[] input) {
            this.output = input;
        }

        private int nowflag = 0;
        private byte[] output;

        public byte readByte() {
            return output[nowflag++];
        }

        @Override
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

        @Override
        public void pAdd2() {
            this.nowflag += 2;
        }

        @Override
        public void pSub2() {
            this.nowflag -= 2;
        }

        @Override
        public int getLenSum() {
            return output.length;
        }

        public short readUByte() {
            return (short) (0xff & readByte());
        }

        public boolean readBool() {
            return readByte() != 0;
        }

        public int readInt32() {
            int left = readInt();
            int right = readInt();
            return (left << 16) ^ right;
        }

        public long readLong() {
            long left = readInt32();
            long right = readInt32();
            return (left << 32) | (right);
        }

        public byte[] readByte(int len) {
            byte[] op = new byte[len];
            System.arraycopy(output, nowflag, op, 0, len);
            nowflag += len;
            return op;
        }
    }
}
