/*
 * Copyright (c) frankHan personal 2017-2018
 */

package top.gunplan.utils;

import java.util.regex.Pattern;

/**
 * GunNumberUtil
 *
 * @author frank albert
 * @version 0.0.0.1
 * @date 2019-08-06 22:19
 */
public class GunNumberUtil {
    private static final String NUM_16_STA = "0x";
    private static final Pattern NUMBER_PATTERN = Pattern.compile("^[-+]?[\\d]*$");

    public static boolean isNumber(String str) {
        if (str.startsWith(NUM_16_STA)) {
            return true;
        }

        return NUMBER_PATTERN.matcher(str).matches();
    }

    public static boolean isPowOf2(int val) {
        int s = 0;
        int sVal = val;
        for (; (val = (val & (val - 1))) != 0; s++) {

        }
        return s == 1 && sVal > 1;
    }


}
