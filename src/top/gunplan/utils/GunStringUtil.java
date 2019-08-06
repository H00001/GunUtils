/*
 * Copyright (c) frankHan personal 2017-2018
 */

package top.gunplan.utils;

/**
 * about this class, we do not suggest you explore
 * @author dosdrtt
 */
public final class GunStringUtil {
    public static String removeLastUrl(String in) {
        int fg = 0;
        for (int i = in.length() - 1; i >= 0; i--) {
            if (in.charAt(i) == '/') {
                fg = i;
                break;
            }
        }
        return in.substring(0, fg + 1);
    }
}
