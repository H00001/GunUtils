/*
 * Copyright (c) frankHan personal 2017-2018
 */

package top.gunplan.utils;

import java.nio.ByteBuffer;

/**
 * GunReadByteBufferUtil
 *
 * @author frank albert
 * @version 0.0.0.1
 * @date 2019-03-06 22:19
 */
@Deprecated
public final class GunReadByteBufferUtil {
    public static String readToString(ByteBuffer buffer) {
        byte[] b = readToByte(buffer);
        return new String(b);
    }

    public static byte[] readToByte(ByteBuffer buffer) {
        synchronized (buffer) {
            byte[] b = new byte[buffer.position()];
            buffer.flip();
            buffer.get(b);
            buffer.clear();
            return b;
        }
    }

    public static byte[] combine(byte[] b1, byte[] b2) {
        byte[] b3 = new byte[b1.length+b2.length];
        System.arraycopy(b1, 0, b3, 0, b1.length);
        System.arraycopy(b2,0,b3,b1.length,b2.length);
        return b3;
    }
}
